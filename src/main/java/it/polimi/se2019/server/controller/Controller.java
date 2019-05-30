package it.polimi.se2019.server.controller;


import it.polimi.se2019.server.exceptions.MessageParseException;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.GameManager;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.server.playerActions.PlayerAction;
import it.polimi.se2019.util.Observer;
import it.polimi.se2019.util.Request;
import it.polimi.se2019.util.RequestParser;

import java.util.List;

public class Controller implements Observer<Request> {

    private GameManager gameManager;
    private TurnHandler turnHandler;

    public Controller(GameManager activeGames) {
        this.gameManager = activeGames;
    }

    public void applyAction(PlayerAction action){
        action.run();
    }

    @Override
    public void update(Request request) {
        /**
         * Create an Action object by parsing the messageType and params list from
         * the array, then setUp the action
         */
        try {
            RequestParser requestParser = new RequestParser();
            requestParser.parse(request, gameManager);
            List<PlayerAction> playerActionList = requestParser.getPlayerActionList();

            boolean runnable = true;

            for (PlayerAction playerAction : playerActionList) {
                if (!playerAction.check()) {
                    CommandHandler commandHandler = requestParser.getCommandHandler();
                    commandHandler.reportError(playerAction.getErrorMessage());
                    runnable = false;
                }
            }

            if (runnable) {
                for (PlayerAction playerAction : playerActionList) {
                    applyAction(playerAction);
                }
            }
        } catch (GameManager.GameNotFoundException | MessageParseException | UnpackingException e) {

        }
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }
}
