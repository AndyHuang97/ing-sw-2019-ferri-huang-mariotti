package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;

import java.util.List;
import java.util.Map;

public class AddPowerUp implements Effect {

    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        if (game.getCurrentPlayer().getCharacterState().getPowerUpBag().size() < 3) {
            game.getCurrentPlayer().getCharacterState().addPowerUp(game.getPowerupDeck().drawCard());
        }
    }
}
