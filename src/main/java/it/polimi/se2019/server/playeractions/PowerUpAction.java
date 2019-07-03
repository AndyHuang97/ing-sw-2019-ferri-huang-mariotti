package it.polimi.se2019.server.playeractions;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.cards.Card;
import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PowerUpAction extends PlayerAction {

    private static final int MAIN_EFFECT = 0;
    private static final String ERROR_MESSAGE = "PowerUp selection failed:";
    private static final String NO_SELECTION_REMINDER = "no PowerUp selected";
    private static final String NOT_IN_BAG_REMINDER = "selected PowerUps are not in your bag";

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
            Logger.getGlobal().info(powerUpsToDiscard.toString());
            powerUpsToDiscard.forEach(powerUp -> System.out.println(powerUp.getId()));
            inputCommands = buildCommandDict(params);
            powerUpsToDiscard.forEach(p-> Logger.getGlobal().info(p.getId()));
        } catch (ClassCastException e) {
            throw new UnpackingException();
        }
    }

    @Override
    public void run() {
        for (int i = 0; i < powerUpsToDiscard.size(); i++) {
            powerUpsToDiscard.get(i).getActionUnitList().get(MAIN_EFFECT).run(getGame(), extractOneCommand(inputCommands, i));
            getGame().getCurrentActionUnitsList().remove(powerUpsToDiscard.get(i).getActionUnitList().get(MAIN_EFFECT));
            getPlayer().getCharacterState().removePowerUp(powerUpsToDiscard.get(i));
            getGame().discardPowerup(powerUpsToDiscard.get(i));
        }
    }

    @Override
    public boolean check() {
        Logger.getGlobal().info(String.valueOf(getPlayer().getCharacterState().getPowerUpBag().containsAll(powerUpsToDiscard)));

        if (powerUpsToDiscard.isEmpty()) {
            setErrorToReport(buildErrorMessage(Arrays.asList(ERROR_MESSAGE, NO_SELECTION_REMINDER)));
            return false;
        }
        else if (!getPlayer().getCharacterState().getPowerUpBag().containsAll(powerUpsToDiscard)) {
            setErrorToReport(buildErrorMessage(Arrays.asList(ERROR_MESSAGE, NOT_IN_BAG_REMINDER)));
            return false;
        }

        return powerUpsToDiscard.stream().allMatch(powerUp -> powerUp.getActionUnitList().get(MAIN_EFFECT).check(getGame(), inputCommands));
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


    // assumes that all list values have the same length
    private Map<String, List<Targetable>> extractOneCommand(Map<String, List<Targetable>> commands, int position) {
        Logger.getGlobal().info("position: "+position);
        Map<String, List<Targetable>> oneCommand = new HashMap<>();
        commands.keySet().stream().filter(key -> commands.get(key).size()>0).forEach(key -> oneCommand.putIfAbsent(key, new ArrayList<>()));
        oneCommand.keySet().forEach(key -> oneCommand.get(key).add(commands.get(key).get(position)));
        return oneCommand;
    }
}
