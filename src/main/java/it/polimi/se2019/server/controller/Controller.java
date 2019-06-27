package it.polimi.se2019.server.controller;


import it.polimi.se2019.server.exceptions.IllegalPlayerActionException;
import it.polimi.se2019.server.exceptions.MessageParseException;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.GameManager;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.server.playerActions.PlayerAction;
import it.polimi.se2019.util.Observer;
import it.polimi.se2019.util.Request;
import it.polimi.se2019.util.RequestParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class implement the Controller of the MVC pattern. The Controller parse the inputs (Requests)
 * of the view and uses the Model's methods to edit Model's data.
 * Using the character state the Controller is able to manage the turn. Since there is only one Controller for
 * every game running on the server, the Controller needs to behave differently on his state basis, so it needs
 * to save a state for every game.
 */
public class Controller implements Observer<Request> {

    private GameManager gameManager;
    private Map<Game, ControllerState> controllerStateMap = new HashMap<>();

    public Controller(GameManager activeGames) {
        this.gameManager = activeGames;
    }

    public void applyAction(PlayerAction action){
        action.run();
    }

    /**
     * Create a PlayerAction object by parsing the request. Then the PlayerAction built is checked
     * and if it's runnable is run.
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
                System.out.println(player);
            }

            RequestParser requestParser = new RequestParser();
            requestParser.parse(request, game, player);
            List<PlayerAction> playerActionList = requestParser.getPlayerActionList();

            ControllerState controllerState = getStateFromGame(game);

            // nextState handles the input and returns a new State, then a message is sent from the new state;
            // if any model changes happened, the update will be sent before the selection message.
            //TODO avoid using the update CommandHandler's update method, it shall be called only by notifications from the model
            // need to add a new method in CommandHandler for selection purposes.
            ControllerState newControllerState = controllerState.nextState(playerActionList, game, player);
            System.out.println(getStateFromGame(game));
            setControllerStateForGame(game, newControllerState);
            System.out.println(getStateFromGame(game));
            CommandHandler commandHandler = requestParser.getCommandHandler();
            newControllerState.sendSelectionMessage(commandHandler);


        } catch (GameManager.GameNotFoundException | MessageParseException | UnpackingException e) {

        } catch (IllegalPlayerActionException e) {
            System.out.println("Send illegal action error...");
        }
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }



    /**
     * If the game is present in the controllerStateMap return the correspondent ControllerState value
     * else returns a new WaitingForRespawn (subclass of ControllerState)
     * @param game the key you need to get the associated value
     * @return ControllerState of the selected Game
     */
    public ControllerState getStateFromGame(Game game) {
        controllerStateMap.putIfAbsent(game, new WaitingForRespawn());
        return controllerStateMap.get(game);
    }

    /**
     * Set the ControllerState for the selected Game
     * @param game the kay you need to set the associated value
     * @param controllerState the ControllerState you want to correspond to the key
     */
    public void setControllerStateForGame(Game game, ControllerState controllerState) {
        controllerStateMap.put(game, controllerState);
    }
}
