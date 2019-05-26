package it.polimi.se2019.server.playerActions;

import it.polimi.se2019.server.actions.Action;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.util.ErrorResponse;

import java.util.List;

/**
 * PlayerAction objects are used by the Controller to modify the model after
 * the View sends a valid Request
 */
public abstract class PlayerAction {

    private Game game;
    private Player player;
    private CommandHandler commandHandler;

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

    // TODO: remove getAction
    public Action getAction() {
        return action;
    }

    // TODO: remove setAction
    public void setAction(Action action) {
        this.action = action;
    }
}
