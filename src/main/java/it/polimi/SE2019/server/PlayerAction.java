package it.polimi.SE2019.server;

import it.polimi.SE2019.server.actions.Action;
import it.polimi.SE2019.server.games.Game;
import it.polimi.SE2019.server.games.player.Player;

public class PlayerAction {
    private Game game;
    private Player player;
    private Action action;

    public PlayerAction(Game game, Player player, Action action) {
        this.game = game;
        this.player = player;
        this.action = action;
    }

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

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
