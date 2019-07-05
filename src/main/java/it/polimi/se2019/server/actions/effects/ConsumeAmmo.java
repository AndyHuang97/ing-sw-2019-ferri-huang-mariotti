package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.AmmoColor;

import java.util.List;
import java.util.Map;

/**
 * This effect consumes the indicated ammo from the current player.
 *
 * @author andreahuang
 *
 */
public class ConsumeAmmo implements Effect {

    private Map<AmmoColor, Integer> ammoToConsume;

    /**
     * Default constructor. It sets up the ammo to consume variable.
     *
     * @param ammoToConsume
     */
    public ConsumeAmmo(Map<AmmoColor, Integer> ammoToConsume) {
        this.ammoToConsume = ammoToConsume;
    }

    /**
     * This method calls the consume ammo method on the player to consume the indicated ammo.
     *
     * @param  game the game on which to perform the effect.
     * @param targets the targets is the input of the current player. Either tiles or players.
     */
    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        game.getCurrentPlayer().getCharacterState().consumeAmmo(ammoToConsume, game);
    }
}
