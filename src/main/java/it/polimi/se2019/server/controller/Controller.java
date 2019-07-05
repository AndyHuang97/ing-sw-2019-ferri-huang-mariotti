package it.polimi.se2019.server.controller;


import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.exceptions.MessageParseException;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.GameManager;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.server.playeractions.PlayerAction;
import it.polimi.se2019.util.Observer;
import it.polimi.se2019.util.Request;
import it.polimi.se2019.util.RequestParser;
import it.polimi.se2019.util.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * This class implement the Controller of the MVC pattern. The Controller parse the inputs (Requests)
 * of the view and uses the Model's methods to edit Model's data.
 * Using the character state the Controller is able to manage the turn. Since there is only one Controller for
 * all the games running on the server, the Controller needs to behave differently on his state basis, so it needs
 * to save a state for every game.
 *
 * @author FF
 *
 */
public class Controller implements Observer<Request> {

    private GameManager gameManager;
    private Map<Game, ControllerState> controllerStateMap = new HashMap<>();

    /**
     * Default constructor that is linked to the gamemanager
     *
     * @param activeGames the gamemanager
     *
     */
    public Controller(GameManager activeGames) {
        this.gameManager = activeGames;
    }

    /**
     * This method is used to execute an action
     *
     * @param action the action
     *
     */
    public void applyAction(PlayerAction action){
        action.run();
    }

    /**
     * Create a PlayerAction object by parsing the request. Then the PlayerAction built is checked
     * and if it's runnable is run.
     *
     * @param request the request from the client already prefiltered
     *
     */
    @Override
    public void update(Request request) {
        try {
            String nickname = request.getNickname();
            Game game = gameManager.retrieveGame(nickname);

            Optional<Player> optPlayer = game.getPlayerList().stream()
                    .filter(p -> p.getUserData().getNickname().equals(nickname))
                    .findFirst();

            Player player = null;
            if (optPlayer.isPresent()) {
                player = optPlayer.get();
                Logger.getGlobal().info(player.getUserData().getNickname());
            }

            RequestParser requestParser = new RequestParser();
            requestParser.parse(request, game, player);
            List<PlayerAction> playerActionList = requestParser.getPlayerActionList();

            ControllerState controllerState = getStateFromGame(game);

            // nextState handles the input and returns a new State, then a message is sent from the new state;
            // if any model changes happened, the update will be sent before the selection message.
            ControllerState newControllerState = controllerState.nextState(playerActionList, game, player);
            this.saveGames(game, controllerState, newControllerState);

            CommandHandler commandHandler = gameManager.getPlayerCommandHandlerMap().get(game.getCurrentPlayer().getUserData().getNickname());
            controllerState.sendErrorMessages(commandHandler);

            setControllerStateForGame(game, newControllerState);

            requestUpdate(game);

            Logger.getGlobal().info("Sending "+newControllerState.getClass().getSimpleName()+" to "+game.getCurrentPlayer().getUserData().getNickname());
            newControllerState.sendSelectionMessage(commandHandler);

        } catch (GameManager.GameNotFoundException | MessageParseException | UnpackingException e) {

        }
    }

    /**
     * The getter for the gamemanager
     *
     * @return the gamemanager
     *
     */
    public GameManager getGameManager() {
        return gameManager;
    }

    /**
     * The setter for the gamemanager
     *
     * @param gameManager the new gamemanager
     *
     */
    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }



    /**
     * If the game is present in the controllerStateMap return the correspondent ControllerState value
     * else returns a new WaitingForRespawn (subclass of ControllerState)
     *
     * @param game the key you need to get the associated value
     * @return ControllerState of the selected Game
     *
     */
    public ControllerState getStateFromGame(Game game) {
        controllerStateMap.putIfAbsent(game, new WaitingForRespawn());
        return controllerStateMap.get(game);
    }

    /**
     * Set the ControllerState for the selected Game
     *
     * @param game the kay you need to set the associated value
     * @param controllerState the ControllerState you want to correspond to the key
     *
     */
    public synchronized void setControllerStateForGame(Game game, ControllerState controllerState) {
        controllerStateMap.put(game, controllerState);
    }

    /**
     * Send the buffer to the clients
     *
     * @param currentGame the game you want to update on the clients
     *
     */
    public void requestUpdate(Game currentGame) {
        Map<String, CommandHandler> commandHandlerMap = gameManager.getPlayerCommandHandlerMap();
        for (Player player : currentGame.getActivePlayerList()) {

            CommandHandler commandHandler = commandHandlerMap.get(player.getUserData().getNickname());

            try {
                commandHandler.sendBuffer();
            } catch (CommunicationError e) {
                Logger.getGlobal().info("Unable to send update trigger command");
            }
        }
    }

    /**
     * Only save when a turn is finished and we infer that from the state change
     *
     * @param game the game you want to save
     * @param oldControllerState to check if the turn ended
     * @param newControllerState to check if the turn ended
     *
     */
    public void saveGames(Game game, ControllerState oldControllerState, ControllerState newControllerState) {
        if ((oldControllerState.getClass().equals(WaitingForMainActions.class) && oldControllerState.getClass().equals(newControllerState.getClass()) && !oldControllerState.equals(newControllerState))||
                (oldControllerState.getClass().equals(WaitingForReload.class) && newControllerState.getClass().equals(WaitingForMainActions.class)) ||
                (oldControllerState.getClass().equals(WaitingForRespawn.class) && newControllerState.getClass().equals(WaitingForMainActions.class))) {
            gameManager.dumpToFile(game);
        }
    }

    /**
     * Notify players a game is finished
     *
     * @param game the game you want to notify
     * @throws it.polimi.se2019.util.Observer.CommunicationError if clients are down
     *
     */
    public void notifyGameEnd(Game game) throws Observer.CommunicationError {
        for (Player player : game.getActivePlayerList()) {
            gameManager.getPlayerCommandHandlerMap().get(player.getUserData().getNickname()).sendResponse(new Response(null, false, Constants.FINISHGAME));
        }
    }

}
