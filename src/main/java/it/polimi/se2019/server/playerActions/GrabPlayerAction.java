package it.polimi.se2019.server.playerActions;

import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.SpawnTile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.ErrorResponse;

import java.util.List;

public class GrabPlayerAction extends PlayerAction {

    private static final int WEAPONPOSITION = 0;
    private static final String errorMessage = "Grab action failed";

    private Weapon weaponToGrab;

    public GrabPlayerAction(Game game, Player player) { super(game, player); }

    @Override
    public void unpack(List<Targetable> params) throws UnpackingException {
        try {
            weaponToGrab = (Weapon) params.get(WEAPONPOSITION);
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
                if(weapon == weaponToGrab) {
                    return true;
                }
            }
        } catch (ClassCastException e) {
            return false;
        }
        return false;
    }

    @Override
    public ErrorResponse getErrorMessage() {
        return new ErrorResponse(errorMessage);
    }

    @Override
    public void run() {
        Player player = getGame().getCurrentPlayer();

        player.getCharacterState().addWeapon(weaponToGrab);
    }
}
