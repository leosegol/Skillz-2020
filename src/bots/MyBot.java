package bots;

import bots.wrapper.*;
import bots.missions.*;
import penguin_game.*;




/**
 * This is an example for a bot.
 */
public class MyBot implements SkillzBot {
    /**
     * Makes the bot run a single turn.
     *
     * @param game - the current game state.
     */
    @Override
    public void doTurn(Game game) {
        if (game.turn == 1)
            MyGame.init(game);
        MyGame.update(game);
        handleIcebergs(game);
    }

    private void handleIcebergs(Game game) {
        for (Iceberg myIceberg : game.getMyIcebergs()){
            Protocol<Iceberg> protocol = MissionManager.createIcebergMission(myIceberg);
            protocol.act(myIceberg);
        }
    }
}

