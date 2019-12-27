package bots;

import bots.missions.*;
import bots.wrapper.*;
import penguin_game.*;

import java.util.List;
import java.util.Map;

/**
 * This is an example for a bot.
 */
public class MyBot implements SkillzBot {
    public static Map<MyIceberg, List<Mission>> icebergsMissions;

    /**
     * Makes the bot run a single turn.
     *
     * @param game - the current game state.
     */
    @Override
    public void doTurn(Game game) {
        MyGame myGame = new MyGame(game);
        handleIcebergs(myGame);
    }
    private void handleIcebergs(MyGame game) {
        /*
        Dictionary of iceberg and List of missions in the execute order/priority.
        (If one mission doesn't execute, it will choose the next Mission in the list)
        */
        MyBot.icebergsMissions = MissionManager.createMissionsForIcebergs(game); // give access to other classes
        Map<MyIceberg, List<Mission>> icebergsMissions = MyBot.icebergsMissions;
        Map<MyIceberg, Integer> penguinsIcebergNeeds;
        //iterating through every iceberg and executing his mission.
        for (MyIceberg iceberg : icebergsMissions.keySet())
            for (int i = 0; i < icebergsMissions.get(iceberg).size(); i ++)
                if (icebergsMissions.get(iceberg).get(i).act(game, iceberg)) // if acted, do not execute the next mission
                    break;
    }
}
