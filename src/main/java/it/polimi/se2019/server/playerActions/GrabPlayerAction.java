package it.polimi.se2019.server.playerActions;

import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.SpawnTile;
import it.polimi.se2019.server.games.player.Player;

import java.util.List;

public class GrabPlayerAction extends PlayerAction {
    private Weapon weaponToGrab;
    private final String errorMessage = "Grab action failed";

    public GrabPlayerAction(Game game, Player player) { super(game, player); }

    @Override
    public void unpack(List<Targetable> params) throws UnpackingException {
        try {
            weaponToGrab = (Weapon) params.get(0);
        } catch (ClassCastException e) {
            throw new UnpackingException();
        }
    }

    /**
     * Checks if the Weapon the player is trying to grab is in the weapon crate of the Tile the player is in.
     */
    @Override
    public boolean check() {
        try {
            SpawnTile playerPosition = (SpawnTile) getPlayer().getCharacterState().getTile();

            List<Weapon> weaponCrate = playerPosition.getWeaponCrate();

            for (Weapon weapon : weaponCrate) {
                if(weapon ==weaponToGrab) {
                    return true;
                }
            }
        } catch (ClassCastException e) {
            return false;
        }
        return false;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public void run() {

    }
}
