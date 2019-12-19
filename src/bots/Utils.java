package bots;

import penguin_game.Game;
import penguin_game.GameObject;
import penguin_game.Iceberg;
import penguin_game.PenguinGroup;

public class Utils {
    public static Game game;

    /**
     * @param object: Some object in the game.
     * @param arr:    Array of objects (doesn't have to be the same type)
     * @param <T>     :nothing.
     * @return : return the closest object from arr to object parameter, or null if arr length is below 1
     */
    public static <T extends GameObject> T closestTo(GameObject object, T[] arr) {
        if (arr.length > 0) {
            int distance = object.__distance(arr[0]);
            T closestObj = arr[0];
            for (T temp : arr) {
                if (temp.__distance(object) < distance) {
                    distance = temp.__distance(object);
                    closestObj = temp;
                }
            }
            return closestObj;
        }
        return null;
    }

    public static int howManyInRange(GameObject GameObject, GameObject[] objects, int range) {
        int counter = 0;
        for (GameObject temp : objects) {
            if (GameObject.__distance(temp) <= range)
                counter++;
        }
        return counter;
    }

    public static void allIceBergsAttack(Game game,Iceberg enemyIceberg) {
        if (enemyIceberg != null && game.getMyIcebergs().length > 0) {
            Iceberg attackers[] = new Iceberg[game.getMyIcebergs().length];
            int c = 0;
            for (Iceberg mine : game.getMyIcebergs()) {
                if (mine.penguinAmount / 2 > enemyIceberg.penguinAmount / game.getMyIcebergs().length) {
                    attackers[c] = mine;
                    c++;
                }
            }
            if (attackers.length > 2) {
                for (Iceberg attack : attackers)
                    if(attack != null)
                    attack.sendPenguins(enemyIceberg, attack.penguinAmount / 2);
            }

        }
    }


    public static <T extends GameObject> boolean isInArray(T object, T[] array) {
        for (T arrayObject : array) {
            if (arrayObject == object)
                return true;
        }
        return false;
    }

    public static Iceberg[] getIcebergsUnderAttack(Game game) {
        Iceberg[] myIcebergs = game.getMyIcebergs();
        PenguinGroup[] enemyPenguinGroups = game.getEnemyPenguinGroups();
        Iceberg[] enemyDestinations = new Iceberg[enemyPenguinGroups.length];
        int counter = 0;
        for (PenguinGroup penguinGroup : enemyPenguinGroups) {
            enemyDestinations[counter] = penguinGroup.destination;
            counter++;
        }
        counter = 0;
        Iceberg[] icebergsUnderAttack = new Iceberg[enemyDestinations.length];
        for (Iceberg destination : enemyDestinations) {
            for (int j = 0; j < myIcebergs.length; j++) {
                if (destination == myIcebergs[j] && !isInArray(myIcebergs[j], icebergsUnderAttack)) {
                    icebergsUnderAttack[counter] = destination;
                    System.out.println("help me, turn:" + game.turn + " id:" + destination.id);
                    counter++;
                    break;
                }
            }
        }
        return icebergsUnderAttack;
    }


}