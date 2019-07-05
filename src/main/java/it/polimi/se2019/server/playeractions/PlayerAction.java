package it.polimi.se2019.server.playeractions;

import it.polimi.se2019.server.cards.Card;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.AmmoColor;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.util.CommandConstants;
import it.polimi.se2019.util.ErrorResponse;

import java.util.*;
import java.util.stream.Collectors;

/**
 * PlayerAction objects are used by the Controller to modify the Model after
 * the View sends a valid Request
 *
 * @author Rodolfo Mariotti
 */
public abstract class PlayerAction implements Targetable {
    private Game game;
    private Player player;
    private CommandHandler commandHandler;
    private int amount;

    private static final String DEFAULT_ERROR = "Action failed!";
    private String errorToReport;

    /**
     * Constructor used to build a PlayerAction object.
     *
     * @param game reference to the model
     * @param player the player that needs to run the action
     */
    public PlayerAction(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    /**
     * This constructor is only used to build nameplate instances of the object, designed for the MovePlayerAction in particular.
     * The instances are mainly used to retrieve the Id of the different subclasses and the distance for a MovePlayerAction.
     *
     * @param amount is the parameter that indicates the distance.
     */
    public PlayerAction(int amount) {
        this.amount = amount;
    }

    /**
     * This method is used to set the attributes of the object without using the controller. The params
     * vector should contain all the data that needs to be set by the playerAction.
     *
     * @param params contains data to "initialize" the object
     * @throws UnpackingException the params does not contains enough data or the data is corrupted/malformed
     */
    public abstract void unpack(List<Targetable> params) throws UnpackingException;

    /**
     * This method is used to apply the effect of the player action. The model will be edited by accessing
     * the game attribute that contains a reference to the game. This method it's called by the controller
     * after running the check() method
     */
    public abstract void run();

    /**
     * This method is used to check if this player action is valid. It runs some correctness tests and
     * return true if the action is runnable.
     *
     * @return true if the action is runnable and false if it is not
     */
    public abstract boolean check();

    /**
     * This method returns the error message that the controller will send to the view to in case this actions
     * is not runnable
     *
     * @return fancy error message that should be shown to the player
     */
    public ErrorResponse getErrorMessage() {
        if (errorToReport != null) {
            return new ErrorResponse(errorToReport);
        } else {
            return new ErrorResponse(DEFAULT_ERROR);
        }
    }

    /**
     * Returns the card used in this action.
     *
     * @return card used for this actions
     */
    public abstract Card getCard();

    /**
     * Getter method for the game attribute.
     *
     * @return reference to the model
     */
    public Game getGame() {
        return game;
    }

    /**
     * Setter method for the game attribute.
     *
     * @param game set the reference to the model used by some methods of this class
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Getter method for the player attribute.
     *
     * @return the player that requested to run this action
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Setter method for the player attribute.
     *
     * @param player set the player that requested to run this action
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Getter method for the amount attribute.
     *
     * @return an attribute often used as a placeholder
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Getter method for the errorToReport attribute.
     *
     * @return the error to report as string
     */
    public String getErrorToReport() {
        return errorToReport;
    }

    /**
     * Setter method for the errorToReport attribute.
     *
     * @param errorToReport set the error message
     */
    public void setErrorToReport(String errorToReport) {
        this.errorToReport = errorToReport;
    }

    /**
     * This helper method serves as adapter because while the playerAction uses a sorted array to parse the
     * params, the action units require a map because uses named params. This method translates the param
     * array in a map of params.
     *
     * @param params sorted array of params
     * @return map of params
     */
    public Map<String, List<Targetable>> buildCommandDict(List<Targetable> params) {
        Map<String, List<Targetable>> commandDict = new HashMap<>();

        // assuming that all params were converted correctly
        List<Targetable> targetList = params.stream()
                .filter(t -> getGame().getPlayerList().contains(t))
                .collect(Collectors.toList());
        commandDict.put(CommandConstants.TARGETLIST, targetList);

        List<Targetable> tileList = params.stream()
                .filter(t -> getGame().getBoard().getTileList().contains(t))
                .collect(Collectors.toList());
        commandDict.put(CommandConstants.TILELIST, tileList);

        List<Targetable> targetedAmmoColor = params.stream()
                .filter(t -> Arrays.asList(AmmoColor.values()).contains(t)).collect(Collectors.toList());
        commandDict.put(CommandConstants.AMMOCOLOR, targetedAmmoColor);

        return commandDict;
    }

    public static final PlayerAction MOVE = new MovePlayerAction(0);
    public static final PlayerAction GRAB = new GrabPlayerAction(0);
    public static final PlayerAction SHOOT = new ShootPlayerAction(0);
    public static final PlayerAction RELOAD = new ReloadPlayerAction(0);
    public static final PlayerAction POWERUP = new PowerUpAction(0);
    public static final PlayerAction NOP = new NoOperation(0);
    public static final PlayerAction RESPAWN = new RespawnAction(0);
    public static final PlayerAction SHOOT_WEAPON = new ShootWeaponSelection(0);

    /**
     * Get a list of all possible action.
     *
     * @return list of possible actions
     */
    public static List<PlayerAction> getAllPossibleActions() {
        return Arrays.asList(MOVE, GRAB, SHOOT, RELOAD, POWERUP, NOP, RESPAWN, SHOOT_WEAPON);
    }

    /**
     * Helper method, builds an error message. Basically add spaces and punctuation to the phrase and some fancy
     * symbols.
     *
     * @param stringList parts of the message
     * @return fancy error message
     */
    protected static String buildErrorMessage(List<String> stringList) {
        final Character SPACE = ' ';
        final Character POINT = '.';

        StringBuilder stringBuilder = new StringBuilder();
        Iterator<String> iterator = stringList.iterator();

        while (iterator.hasNext()) {
            String string = iterator.next();

            stringBuilder.append(string);

            if (iterator.hasNext()) {
                stringBuilder.append(SPACE);
            } else {
                stringBuilder.append(POINT);
            }
        }

        return stringBuilder.toString();
    }
}
