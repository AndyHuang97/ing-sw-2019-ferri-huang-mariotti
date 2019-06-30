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
import java.util.stream.Collectors;

public class PowerUpAction extends PlayerAction {

    private static final int MAIN_EFFECT = 0;
    private static final String ERRORMESSAGE = "PowerUp selection failed";

    private List<PowerUp> powerUpsToDiscard;
    private Map<String, List<Targetable>> inputCommands;

    public PowerUpAction(Game game, Player player) {
        super(game, player);
    }
    public PowerUpAction(int amount) {super (amount);}

    @Override
    public void unpack(List<Targetable> params) throws UnpackingException {
        try {
            powerUpsToDiscard = params.stream().filter(t -> getPlayer().getCharacterState().getPowerUpBag().contains(t))
                    .map(t -> (PowerUp) t).collect(Collectors.toList());
            inputCommands = buildCommandDict(params);
            powerUpsToDiscard.forEach(p-> Logger.getGlobal().info(p.getId()));
        } catch (ClassCastException e) {
            throw new UnpackingException();
        }
    }

    @Override
    public void run() {
        powerUpsToDiscard.forEach(powerUp -> {
            powerUp.getActionUnitList().get(MAIN_EFFECT).run(getGame(), inputCommands);
            getGame().getCurrentActionUnitsList().remove(powerUp.getActionUnitList().get(MAIN_EFFECT));
            getPlayer().getCharacterState().removePowerUp(powerUp);
            getGame().discardPowerup(powerUp);
        });
    }

    @Override
    public boolean check() {
        Logger.getGlobal().info(String.valueOf(getPlayer().getCharacterState().getPowerUpBag().containsAll(powerUpsToDiscard)));
        return getPlayer().getCharacterState().getPowerUpBag().containsAll(powerUpsToDiscard)
                &&
                powerUpsToDiscard.stream().allMatch(powerUp -> powerUp.getActionUnitList().get(MAIN_EFFECT).check(getGame(), inputCommands));
    }

    @Override
    public ErrorResponse getErrorMessage() {
        return new ErrorResponse(ERRORMESSAGE);
    }

    @Override
    public Card getCard() {
        return null;
    }

    @Override
    public String getId() {
        return Constants.POWERUP;
    }

    public List<PowerUp> getPowerUpsToDiscard() {
        return powerUpsToDiscard;
    }
}
