package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;

import java.util.List;
import java.util.Map;

public class AddPowerUp implements Effect {

    private static final int POWERUPPOSITION = 0;

    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        game.getCurrentPlayer().getCharacterState().addPowerUp(game.getPowerupDeck().drawCard());
    }
}
