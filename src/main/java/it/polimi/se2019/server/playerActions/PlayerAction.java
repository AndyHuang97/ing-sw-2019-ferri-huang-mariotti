package it.polimi.se2019.server.playerActions;

import it.polimi.se2019.server.actions.Action;
import it.polimi.se2019.server.cards.Card;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.util.ErrorResponse;

import java.util.Arrays;
import java.util.List;

/**
 * PlayerAction objects are used by the Controller to modify the Model after
 * the View sends a valid Request
 */
public abstract class PlayerAction implements Targetable {
    private Game game;
    private Player player;
    private CommandHandler commandHandler;
    private int amount;

    @Deprecated
    private Action action;

    @Deprecated
    public PlayerAction(Game game, Player player, Action action) {
        this.game = game;
        this.player = player;
        this.action = action;
    }

    public PlayerAction(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    /**
     * This constructor is only used to build nameplate instances of the object, designed for the MovePlayerAction in particular.
     * The instances are mainly used to retrieve the Id of the different sublclasses and the distance for a MovePlayerAction.
     * @param amount is the parameter that indicates the distance.
     */
    public PlayerAction(int amount) {
        this.amount = amount;
    }

    public abstract void unpack(List<Targetable> params) throws UnpackingException;

    public abstract void run();

    public abstract boolean check();

    public abstract ErrorResponse getErrorMessage();

    public abstract Card getCard();

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    // TODO: remove getActionNumber
    public Action getAction() {
        return action;
    }

    // TODO: remove setActionNumber
    public void setAction(Action action) {
        this.action = action;
    }

    public int getAmount() {
        return amount;
    }

    public static final PlayerAction MOVE = new MovePlayerAction(0);
    public static final PlayerAction GRAB = new GrabPlayerAction(0);
    public static final PlayerAction SHOOT = new ShootWeaponSelection(0);
    public static final PlayerAction RELOAD = new ReloadPlayerAction(0);
    public static final PlayerAction POWERUP = new RespawnAction(0);

    public static List<PlayerAction> getAllPossibleActions() {
        return Arrays.asList(MOVE, GRAB, SHOOT, RELOAD, POWERUP);
    }
}
