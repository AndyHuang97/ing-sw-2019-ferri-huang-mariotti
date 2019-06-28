package it.polimi.se2019.server.playerActions;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.cards.Card;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.CommandConstants;
import it.polimi.se2019.util.ErrorResponse;

import java.util.*;
import java.util.stream.Collectors;

public class ShootPlayerAction extends PlayerAction {

    private static final String ERRORMESSAGE = "Shoot action failed";

    private Weapon chosenWeapon;
    private ActionUnit chosenActionUnit;
    private Map<String, List<Targetable>> inputCommands;

    public ShootPlayerAction(Game game, Player player) { super(game, player); }
    public ShootPlayerAction(int amount) { super(amount);}

    @Override
    public void unpack(List<Targetable> params) throws UnpackingException {

        inputCommands = buildCommandDict(params);

        chosenWeapon = (Weapon) params.stream()
                .filter(t -> getPlayer().getCharacterState().getWeaponBag().contains(t))
                .findAny().orElseThrow(UnpackingException::new);

        chosenActionUnit = (ActionUnit) params.stream()
                .filter(t -> chosenWeapon.getActionUnitList().contains(t) || chosenWeapon.getOptionalEffectList().contains(t))
                .findAny().orElseThrow(UnpackingException::new);
    }

    @Override
    public void run() {
        // since ActionUnit.run() signature changed we need to build a Map<String, List<Targetable>>
        // in order to run the action
        chosenActionUnit.run(getGame(), inputCommands);
    }

    @Override
    public boolean check() {
        // Is weapon loaded?
        if (!chosenWeapon.isLoaded()) {
            return false;
        }

        // Is chosenActionUnit in chosenWeapon?
        /* should be already checked by unpack
        if (chosenWeapon.getActionUnitList().stream().noneMatch(availableActionUnit -> availableActionUnit == chosenActionUnit)) {
            return false;
        }
         */

        return chosenActionUnit.check(getGame(), inputCommands);
    }

    public Weapon getChosenWeapon() {
        return chosenWeapon;
    }

    @Override
    public ErrorResponse getErrorMessage() {
        return new ErrorResponse(ERRORMESSAGE);
    }

    @Override
    public Card getCard() {
        return chosenWeapon;
    }

    @Override
    public String getId() {
        return Constants.SHOOT;
    }
}
