package bots;


import bots.missions.CaptureIceberg;
import bots.missions.Mission;
import bots.missions.SupportIceberg;
import bots.missions.UpgradeIceberg;
import bots.tasks.*;
import bots.wrapper.MyIceberg;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MissionManager {

    public static Map<Mission, Integer> activeMissions = new ConcurrentHashMap<>(); //All single missions that takes place atm.

    /**
     * @param mission - missions
     * @return - options to execute the mission (each option is by different iceberg group).
     */
    public static List<TaskGroup> waysToExecute(Mission mission) {
        List<TaskGroup> waysToExec = new LinkedList<>();
        if (mission instanceof CaptureIceberg)
            for (Set<MyIceberg> icebergs : Constant.Groups.allMyIcebergGroups)
                waysToExec.add(howToCapture(new LinkedList<>(icebergs), (CaptureIceberg) mission));
        if (mission instanceof SupportIceberg)
            for (Set<MyIceberg> icebergs : Constant.Groups.allMyIcebergGroups)
                if (!icebergs.contains(mission.getTarget()))
                    waysToExec.add(howToSupport(new LinkedList<>(icebergs), (SupportIceberg) mission));
        if (mission instanceof UpgradeIceberg) {
            waysToExec.add(new TaskGroup(new Upgrade(mission.getTarget())));
        }
        waysToExec.removeIf(Objects::isNull);
        return waysToExec;
    }

    public static int totalBenefit(Set<Mission> missionGroup) {
        int benefit = 0;
        for (Mission mission : missionGroup) {
            benefit += mission.benefit();
        }
        return benefit;
    }

    /**
     * @param supporters     - contributing icebergs to support
     * @param supportIceberg - mission
     * @return Set of tasks (task for each supporter)
     */
    private static TaskGroup howToSupport(List<MyIceberg> supporters, SupportIceberg supportIceberg) {
        TaskGroup tasks = new TaskGroup();
        //TODO - how to support... how much each iceberg should send.
        for (MyIceberg iceberg : supporters)
            tasks.add(new Support(iceberg, supportIceberg.getTarget(), 0));
        return tasks;
    }

    /**
     * attackers - friendly (ours)
     * target - enemy iceberg
     *
     * @param attackers      - contributing icebergs to attack
     * @param captureIceberg - mission
     * @return - Set of tasks
     */
    private static TaskGroup howToCapture(List<MyIceberg> attackers, CaptureIceberg captureIceberg) {
        TaskGroup tasks = new TaskGroup();
        int neededPenguins = captureIceberg.getTarget().iceberg.penguinAmount + 1;
        if (captureIceberg.getTarget().iceberg.owner.equals(Constant.Players.enemyPlayer))
            neededPenguins += captureIceberg.getTarget().iceberg.penguinsPerTurn *
                    captureIceberg.getTarget().farthest(attackers).iceberg.getTurnsTillArrival(captureIceberg.getTarget().iceberg);


        double availablePenguins = 0;
        for (MyIceberg iceberg : attackers) {
            if (iceberg.getFreePenguins() - iceberg.getPenguinsComingFromIceberg(captureIceberg.getTarget()) <= 0)
                return tasks;
            availablePenguins += iceberg.getFreePenguins() - iceberg.getPenguinsComingFromIceberg(captureIceberg.getTarget());
        }

        if (availablePenguins > neededPenguins) {
            for (MyIceberg iceberg : attackers) {
                int realFreePenguins = iceberg.getFreePenguins() - iceberg.getPenguinsComingFromIceberg(captureIceberg.getTarget());
                tasks.add(new Attack(iceberg, captureIceberg.getTarget(), (int) Math.round((realFreePenguins / availablePenguins) * neededPenguins)));
            }
        }
        return tasks;
    }

    /**
     * @return all single missions.
     */
    public static Set<Mission> allMissions() {
        Set<Mission> missions = new HashSet<>();

        for (MyIceberg iceberg : Constant.Icebergs.allIcebergs) {
            if (!iceberg.iceberg.owner.equals(Constant.Players.mySelf))
                missions.add(new CaptureIceberg(iceberg));
            else {
                if (Constant.Icebergs.myIcebergs.size() != 0)
                    missions.add(new SupportIceberg(iceberg));
                if (iceberg.iceberg.canUpgrade())
                    missions.add(new UpgradeIceberg(iceberg));
            }
        }
        missions.removeIf(MissionManager::isActive);
        return missions;
    }

    private static boolean isActive(Mission mission) {
        if (mission instanceof UpgradeIceberg)
            return false;
        for (Mission activeMission : activeMissions.keySet())
            if (activeMission.getType().equals(mission.getType()))
                return true;
        return false;
    }

    /**
     * @param size - maxim size of groups
     * @return all mission groups with size <= param.size
     */
    public static Set<Set<Mission>> allMissionGroups(int size) {
        return Utils.powerSet(Constant.Groups.allMissions, size);
    }

    public static List<TaskGroup> allCombinations(List<List<TaskGroup>> matrix) {
        int combination = 0;

        int maxCombinations = matrix.get(0).size();
        for (int layer = 1; layer < matrix.size(); layer++)
            maxCombinations *= matrix.get(layer).size();

        List<TaskGroup> combinationList = new LinkedList<>();
        while (combination < maxCombinations) {
            combinationList.add(createCombination(matrix, combination));
            combination++;
        }
        combinationList.removeIf(Objects::isNull);
        return combinationList;
    }

    public static TaskGroup createCombination(List<List<TaskGroup>> matrix, int comb) {
        TaskGroup combination = new TaskGroup();
        int[] index = new int[matrix.size()];
        for (int layer = matrix.size() - 1; layer >= 0; layer--) {
            index[layer] = comb % matrix.get(layer).size();
            comb /= matrix.get(layer).size();
        }

        for (int layer = 0; layer < matrix.size(); layer++) {
            if (!combination.hasShared(matrix.get(layer).get(index[layer])))
                combination.addAll(matrix.get(layer).get(index[layer]));
            else
                return null;
        }
        return combination;
    }

    public static Set<Mission> firstExecutableMissions() {
        for (Set<Mission> missionGroup : Constant.Groups.allMissionGroups) {
            if (howToExecuteMissionGroup(missionGroup).getTasks().size() != 0)
                return missionGroup;
        }
        return new HashSet<Mission>();
    }

    /**
     * this function decides how to execute each mission in a missionGroup.
     *
     * @param missions - missionGroup
     * @return - tasks for each mission (all tasks in the same list)
     */
    public static TaskGroup howToExecuteMissionGroup(Set<Mission> missions) {
        List<List<TaskGroup>> matrix = new LinkedList<>();
        //create matrix
        for (Mission mission : missions) {
            matrix.add(mission.getWaysToExecute());
        }

        List<TaskGroup> availableTaskGroups = allCombinations(matrix);
        if (availableTaskGroups.isEmpty())
            return new TaskGroup();
        TaskGroup holder = availableTaskGroups.get(0);
        for (TaskGroup taskGroup : availableTaskGroups)
            if (taskGroup.getTotalLoss() < holder.getTotalLoss())
                holder = taskGroup;
        return holder;
    }

    /**
     * This function decide which missionGroup to execute. (totalBenefit - totalLoss)
     *
     * @return set of tasks that will execute the chosen missionGroup.
     */
    public static Set<Taskable> createTasksForIcebergs() {
        List<Set<Mission>> allMissionGroups = new LinkedList<>(Constant.Groups.allMissionGroups);
        Set<Mission> holder = firstExecutableMissions();
        if (holder.size() == 0)
            return new HashSet<>();
        for (Set<Mission> missionGroup : allMissionGroups)
            if (totalBenefit(holder) - howToExecuteMissionGroup(holder).getTotalLoss() <
                    totalBenefit(missionGroup) - howToExecuteMissionGroup(missionGroup).getTotalLoss() && howToExecuteMissionGroup(missionGroup).getTasks().size() != 0)
                holder = missionGroup;
        activateMissionGroup(holder, howToExecuteMissionGroup(holder));
        return howToExecuteMissionGroup(holder).getTasks();
    }

    public static void activateMissionGroup(Set<Mission> missionGroup, TaskGroup taskGroup) {
        Map<Mission, TaskGroup> missionTaskGroupMap = new HashMap<>();
        for (Mission mission : missionGroup)
            missionTaskGroupMap.put(mission, new TaskGroup());
        for (Taskable task : taskGroup.getTasks())
            for (Mission mission : missionGroup)
                if (task.getTarget().equals(mission.getTarget())) {
                    missionTaskGroupMap.get(mission).add(task);
                    break;
                }
        for (Mission mission : missionGroup)
            if (!(mission instanceof UpgradeIceberg)) {
                System.out.println("mission: " + mission);
                for (Taskable task : missionTaskGroupMap.get(mission).getTasks())
                    System.out.println("iceberg: " + task.getActor() + ", penguins " + task.penguins());
                activeMissions.put(mission, mission.getTarget().
                        farthest(missionTaskGroupMap.get(mission).usedIcebergs()).iceberg.getTurnsTillArrival(mission.getTarget().iceberg));
            }
    }
}