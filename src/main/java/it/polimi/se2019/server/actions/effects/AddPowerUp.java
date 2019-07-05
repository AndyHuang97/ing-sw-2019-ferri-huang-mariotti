package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;

import java.util.List;
import java.util.Map;

/**
 * This effect gives a powerUp to the current player.
 *
 * @author andreahuang
 *
 */
public class AddPowerUp implements Effect {

    /**
     * This method draws a card for the current player, if
     *
     * @param  game the game on which to perform the effect.
     * @param targets the targets is the input of the current player. Either tiles or players.
     */
    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        if (game.getCurrentPlayer().getCharacterState().getPowerUpBag().size() < 3) {
            game.getCurrentPlayer().getCharacterState().addPowerUp(game.getPowerUpDeck().drawCard());
        }
    }
}
