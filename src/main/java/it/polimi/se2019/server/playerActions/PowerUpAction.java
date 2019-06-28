package it.polimi.se2019.server.playerActions;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.cards.Card;
import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.ErrorResponse;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class PowerUpAction extends PlayerAction {

    private static final int POWERUP_POSITION = 0;
    private static final int MAIN_EFFECT = 0;
    private static final String ERRORMESSAGE = "PowerUp selection failed";

    private PowerUp powerUpToDiscard;
    private Map<String, List<Targetable>> inputCommands;

    public PowerUpAction(Game game, Player player) {
        super(game, player);
    }

    @Override
    public void unpack(List<Targetable> params) throws UnpackingException {
        try {
            powerUpToDiscard = (PowerUp) params.get(POWERUP_POSITION);
            inputCommands = buildCommandDict(params);
            Logger.getGlobal().info(powerUpToDiscard.getId());
        } catch (ClassCastException e) {
            throw new UnpackingException();
        }
    }

    @Override
    public void run() {
        powerUpToDiscard.getActionUnitList().get(MAIN_EFFECT).run(getGame(), inputCommands);
    }

    @Override
    public boolean check() {
        return getPlayer().getCharacterState().getPowerUpBag().contains(powerUpToDiscard)
                &&
                powerUpToDiscard.getActionUnitList().get(MAIN_EFFECT).check(getGame(), inputCommands);
    }

    @Override
    public ErrorResponse getErrorMessage() {
        return new ErrorResponse(ERRORMESSAGE);
    }

    @Override
    public Card getCard() {
        return powerUpToDiscard;
    }

    @Override
    public String getId() {
        return Constants.POWERUP;
    }
}
