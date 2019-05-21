package it.polimi.se2019.server.controller;


import it.polimi.se2019.server.exceptions.MessageParseException;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.GameManager;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.server.playerActions.PlayerAction;
import it.polimi.se2019.server.virtualview.VirtualView;
import it.polimi.se2019.util.*;

import java.sql.Struct;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Controller implements Observer<Request> {

    private GameManager gameManager;
    private VirtualView virtualView;
    private TurnHandler turnHandler;

    public Controller(GameManager activeGames, VirtualView virtualView) {
        this.gameManager = activeGames;
        this.virtualView = virtualView;
    }

    public void onNotify(Request request) {
        /**
         * Create an Action object by parsing the messageType and params list from
         * the array, then run the action
         */
    }

    public void applyAction(PlayerAction action){

    }

    // TODO: Maybe refactor the error message proagation with exceptions
    @Override
    public void update(Request request) {
        Game game = new Game();

        game.performMove("Move");
    }

    public VirtualView getVirtualView() {
        return virtualView;
    }

    public void setVirtualView(VirtualView virtualView) {
        this.virtualView = virtualView;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }
}
