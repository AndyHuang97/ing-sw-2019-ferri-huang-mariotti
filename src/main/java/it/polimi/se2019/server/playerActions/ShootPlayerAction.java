package it.polimi.se2019.server.playerActions;

import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;
import it.polimi.se2019.util.ErrorResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShootPlayerAction extends PlayerAction {

    private static final int TARGETPOSITIONINPARAMS = 0;
    private static final int WEAPONPOSITIONINPARAMS = 1;
    private static final int ACTIONUNITPOSITIONINPARAMS = 2;
    private static final int TILEPOSITIONINPARAMS = 3;
    private static final int EFFECTTILEPOSITIONINPARAMS = 4;

    private final String errorMessage = "Shoot action failed";
    private Player target;
    private Weapon chosenWeapon;
    private ActionUnit chosenActionUnit;
    private Tile chosenTile;
    private Tile effectTile;


    public ShootPlayerAction(Game game, Player player) { super(game, player); }

    @Override
    public void unpack(List<Targetable> params) throws UnpackingException {
        target = (Player) params.get(TARGETPOSITIONINPARAMS);
        chosenWeapon = (Weapon) params.get(WEAPONPOSITIONINPARAMS);
        chosenActionUnit = (ActionUnit) params.get(ACTIONUNITPOSITIONINPARAMS);
        chosenTile = (Tile) params.get(TILEPOSITIONINPARAMS);
        effectTile = (Tile) params.get(EFFECTTILEPOSITIONINPARAMS);
    }

    @Override
    public void run() {
        // since ActionUnit.run() signature changed we need to build a Map<String, List<Targetable>>
        // in order to run the action
        Map<String, List<Targetable>> effectCommands = buildCommandDict();

        chosenActionUnit.run(getGame(), effectCommands);
    }

    @Override
    public boolean check() {
        /**
         * Check that target is in Player view
         * Check that Player is using a valid weapon (and has ammo)
         *  - check that target position matches the weapon requirement
         */

        // Is weapon loaded?
        if (!chosenWeapon.isLoaded()) {
            System.out.println("lol");
            return false;
        }

        // Is chosenActionUnit in chosenWeapon?
        if (chosenWeapon.getActionUnitList().stream().noneMatch(availableActionUnit -> availableActionUnit == chosenActionUnit)) {
            System.out.println("asd");
            return false;
        }

        // build params for Condition (every possible Condition)
        Map<String, List<Targetable>> conditionCommands = buildCommandDict();

        return chosenActionUnit.check(getGame(), conditionCommands);
    }

    private Map<String, List<Targetable>> buildCommandDict() {
        Map<String, List<Targetable>> commandDict = new HashMap<>();

        List<Targetable> targetList = new ArrayList<>();
        targetList.add(target);

        commandDict.put(CommandConstants.TARGETLIST, targetList);

        List<Targetable> chosenTileList = new ArrayList<>();
        chosenTileList.add(chosenTile);

        commandDict.put(CommandConstants.TILE, chosenTileList);

        List<Targetable> tileList = new ArrayList<>();
        tileList.add(chosenTile);
        tileList.add(effectTile);

        commandDict.put(CommandConstants.TILELIST, tileList);

        return commandDict;

    }

    @Override
    public ErrorResponse getErrorMessage() {
        return null;
    }
}
