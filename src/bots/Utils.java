package bots;

import bots.wrapper.MyGame;
import bots.wrapper.MyIceberg;
import penguin_game.Iceberg;

import java.util.*;

public class Utils {
    
    private static <T> List<T> asList(Collection<T> a){
        List<T> returnList = new LinkedList<>();
        returnList.addAll(a);
        return returnList;
    }
    
    public static List<MyIceberg> convertToMyIcebergType(Iceberg[] arr) {
        LinkedList<MyIceberg> myIcebergs = new LinkedList<>();
        for (Iceberg iceberg : arr) {
            myIcebergs.add(new MyIceberg(iceberg));
        }
        return myIcebergs;
    }

    public static List<MyIceberg> myThreatenedIcebergs(MyGame game) {
        List<MyIceberg> threatenedIcebergs = new LinkedList<>();
        for (MyIceberg iceberg : game.getMyIcebergs()) {
            if (iceberg.amountToDefend(game) <= 0)
                threatenedIcebergs.add(iceberg);
        }
        return threatenedIcebergs;
    }

    public static void setupIcebergPenguins(MyGame game) {
        for (MyIceberg iceberg : game.getMyIcebergs()) {
            iceberg.savePenguins(iceberg.amountToDefend(game));
        }
    }

    //max icebergs in group - 3
    private static Set<Set<MyIceberg>> allMyIcebergGroups(MyGame game){
        Set<Set<MyIceberg>> allIcebergGroups = new HashSet<>();
        int numberOfPenguins = 3;

        List<MyIceberg> availableIcebergs = game.getMyIcebergs();
        availableIcebergs.remove(myThreatenedIcebergs(game));


    }
    /**
     * attckers - friendly (ours)
     * target - enemy iceberg
     *
     * @param game      - game info
     * @param attackers - contributing icebergs to attack
     * @param target    - enemy iceberg
     * @return - map of icebergs who contribute to the attack as keys and
     * penguin amount that each iceberg is contributing as value
     */
    public static Map<MyIceberg, Integer> penguinsFromEachIceberg(MyGame game, List<MyIceberg> attackers, MyIceberg target)
    {
        Map<MyIceberg, Integer> penguinsFromIcebergs = new HashMap<>();
        int neededPenguins = target.farthest(attackers).iceberg.getTurnsTillArrival(target.iceberg)
                * target.iceberg.penguinsPerTurn + target.iceberg.penguinAmount + 1;

        double availablePenguins = 0;
        for (MyIceberg iceberg : attackers) {
            if (iceberg.getFreePenguins() - iceberg.getPenguinsComingFromIceberg(game, target) <= 0)
                return null;
            availablePenguins += iceberg.getFreePenguins() - iceberg.getPenguinsComingFromIceberg(game, target);
        }

        if (availablePenguins > neededPenguins) {
            for (MyIceberg iceberg : attackers) {
                int realFreePenguins = iceberg.getFreePenguins() - iceberg.getPenguinsComingFromIceberg(game, target);
                penguinsFromIcebergs.put(iceberg, (int) Math.round((realFreePenguins / availablePenguins) * neededPenguins));
            }
        }
        return null;
    }

    /**
     * @param game - game info
     * @return - all options to attack each enemy iceberg
     * key - target (enemy iceberg)
     * value - list of options to attack the iceberg
     * value(Map):
     * key - attacking Iceberg
     * value - penguins amount
     */
    public static Map<MyIceberg, Set<Map<MyIceberg, Integer>>> optionsToAttack(MyGame game) {
        //TODO - create this function after you finished penguinsFromEachIceberg
        Map<MyIceberg, Set<Map<MyIceberg, Integer>>> optionToAttackEnemy = new HashMap<>();
        Set<Set<MyIceberg>> icebergGroups = allMyIcebergGroups(game);
        List<MyIceberg> specificGroup = new LinkedList<MyIceberg>();
        for (MyIceberg enemyIceberg: game.getEnemyIcebergs()){
            Set<Map<MyIceberg, Integer>> waysToAttack = new HashSet<>();
            for(Set<MyIceberg> group: icebergGroups){
                specificGroup.addAll(group);
                waysToAttack.add(penguinsFromEachIceberg(game, specificGroup, enemyIceberg));
            }
            optionToAttackEnemy.put(enemyIceberg, waysToAttack);
        }
        return optionToAttackEnemy;
    }
}