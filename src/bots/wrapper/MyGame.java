package bots.wrapper;

import bots.Constant;
import bots.MissionManager;
import bots.Utils;
import penguin_game.Game;

import java.util.HashSet;
import java.util.LinkedList;

public class MyGame {

    public static Game game;

    public static void updateGame(Game game) {
        MyGame.game = game;

        Constant.PenguinGroups.allPenguinGroup = new LinkedList<>(Utils.convertToMyPenguinGroupType(game.getAllPenguinGroups()));
        Constant.PenguinGroups.myPenguinGroups = new LinkedList<>(Utils.convertToMyPenguinGroupType(game.getMyPenguinGroups()));
        Constant.PenguinGroups.enemyPenguinGroups = new LinkedList<>(Utils.convertToMyPenguinGroupType(game.getEnemyPenguinGroups()));

        Constant.Icebergs.myIcebergs = new LinkedList<>(Utils.convertToMyIcebergType(game.getMyIcebergs()));
        Constant.Icebergs.enemyIcebergs = new LinkedList<>(Utils.convertToMyIcebergType(game.getEnemyIcebergs()));
        Constant.Icebergs.neutralIcebergs = new LinkedList<>(Utils.convertToMyIcebergType(game.getNeutralIcebergs()));
        Constant.Icebergs.allIcebergs = new LinkedList<>(Utils.convertToMyIcebergType(game.getAllIcebergs()));
        Constant.Icebergs.notMyIcebergs = Utils.getNotMyIcebergs();

        Constant.Game.maxTurns = game.maxTurns;
        Constant.Game.turn = game.turn;
        Constant.Game.turnsLeft = Constant.Game.maxTurns - Constant.Game.turn;

        Constant.Players.mySelf = game.getMyself();
        Constant.Players.enemyPlayer = game.getEnemy();
        Constant.Players.neutral = game.getNeutral();
        Constant.Players.allPlayers = game.getAllPlayers();

        Constant.Groups.allMyIcebergGroups = Utils.powerSet(new HashSet<>(Constant.Icebergs.myIcebergs), 3);
        Constant.Groups.allMissions = MissionManager.allMissions();
        Constant.Groups.allMissionGroups = MissionManager.allMissionGroups(1);
    }
}
