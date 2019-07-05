package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.AmmoColor;

import java.util.List;
import java.util.Map;

/**
 * This effect adds ammo to the curent player of an amout specified by the ammoToAdd parameter.
 *
 * @author andreahuang
 *
 */
public class AddAmmo implements Effect {

    private Map<AmmoColor, Integer> ammoToAdd;

    /**
     * Default constructor. It sets up the ammo to add to the current player.
     *
     * @param ammoToAdd
     */
    public AddAmmo(Map<AmmoColor, Integer> ammoToAdd) {
        this.ammoToAdd = ammoToAdd;
    }

    /**
     * The method that actually adds the ammos to the player.
     *
     * @param  game the game on which to perform the effect.
     * @param targets the targets is the input of the current player. Either tiles or players.
     */
    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        game.getCurrentPlayer().getCharacterState().addAmmo(ammoToAdd);
    }
}
