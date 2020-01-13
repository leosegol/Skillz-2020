package bots;

import bots.wrapper.*;
import penguin_game.*;
import java.util.*;

public class Constant {

    public static class Icebergs{
        public static List<MyIceberg> myIcebergs;
        public static List<MyIceberg> enemyIcebergs;
        public static List<MyIceberg> neutralIcebergs;
        public static List<MyIceberg> allIcebergs;
    }

    public static class PenguinGroups{
        public static List<MyPenguinGroup> myPenguinGroups;
        public static List<MyPenguinGroup> enemyPenguinGroups;
        public static List<MyPenguinGroup> allPenguinGroup;
    }

    public static class Game{
        public static int turn;
        public static int maxTurns;
    }

    public static class Players{
        public static Player[] allPlayers;
        public static Player enemyPlayer;
        public static Player mySelf;
        public static Player neutral;
    }
}
