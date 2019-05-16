package it.polimi.se2019.server.playerActions;

import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;

import java.util.List;

public class ShootPlayerAction extends PlayerAction {

    private final String errorMessage = "Shoot action failed";
    private Player target;
    private Weapon choosenWeapon;

    public ShootPlayerAction(Game game, Player player) { super(game, player); }

    @Override
    public void unpack(List<Targetable> params) throws UnpackingException {

    }

    @Override
    public void run() {

    }

    @Override
    public boolean check() {
        /**
         * Check that target is in Player view
         * Check that Player is using a valid weapon (and has ammo)
         */
        return true;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }
}
