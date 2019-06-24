package it.polimi.se2019.server.playerActions;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.actions.Action;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.util.ErrorResponse;

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

    public PlayerAction(int amount) {
        this.amount = amount;
    }

    public abstract void unpack(List<Targetable> params) throws UnpackingException;

    public abstract void run();

    public abstract boolean check();

    public abstract ErrorResponse getErrorMessage();

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
}
