package it.polimi.se2019.server.controller;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.server.playeractions.PlayerAction;
import it.polimi.se2019.util.Observer;
import it.polimi.se2019.util.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * This is the abstract of the state of the controller, every state needs to send a message to the client and receive a response
 * to switch to another state or to to stay on the same state. A state can generate an error if the client selection is invalid.
 *
 * @author FF
 *
 */
public abstract class ControllerState {
    private static List<String> errorMessages = new ArrayList<>();

    /**
     * This needs to be implemented but sends to the client the action they need to perform
     *
     * @param commandHandler the player commandhandler
     *
     */
    public abstract void sendSelectionMessage(CommandHandler commandHandler);

    /**
     * This method contains all the logic of a state. It checks whether the input is among those allowed in the state.
     * If if fails it stays in the same state and keeps waiting for the same input, otherwise it performs the check
     * and run methods of the actions, with possible modifications on the model and then goes to a new state.
     *
     * @param playerActions the list of actions received from the player
     * @param game the game on which to execute the actions
     * @param player the player sending the input
     * @return the new state of the controller
     *
     */
    public abstract ControllerState nextState(List<PlayerAction> playerActions, Game game, Player player);

    /**
     * Sends an error message to the client by combining the currently stored error messages and then flush the currently stored error messages
     *
     * @param commandHandler the player commandhandler
     *
     */
    public void sendErrorMessages(CommandHandler commandHandler) {
        final String DASH_SPACE = "- ";
        final Character NEWLINE = '\n';

        StringBuilder stringBuilder = new StringBuilder();

        for (String error : errorMessages) {
            stringBuilder.append(DASH_SPACE);
            stringBuilder.append(error);
            stringBuilder.append(NEWLINE);
        }

        try {
            commandHandler.update(new Response(null, false, stringBuilder.toString()));
        } catch (Observer.CommunicationError communicationError) {
            Logger.getGlobal().warning(communicationError.toString());
        }

        errorMessages = new ArrayList<>();
    }

    public void resetErrorMessages() {
        errorMessages = new ArrayList<>();
    }


    /**
     * Adds an error message to the list of stored error messages (errorMessages). These messages will be sent to the
     * Views by the Controller and showed to the player, then the stored error messages will be flushed.
     *
     * @param errorMessage the message that will be stored
     *
     */
    public static void addErrorMessage(String errorMessage) {
        if (!errorMessages.stream().anyMatch(storedMessage -> storedMessage.equals(errorMessage))) {
            errorMessages.add(errorMessage);
        }
    }

    /**
     * Check if a playerAction can be run by executing the check on it, if it cannot be run add an error
     *
     * @param playerAction the action to check
     *
     */
    public static boolean checkPlayerActionAndSaveError(PlayerAction playerAction) {
        boolean runnable = playerAction.check();

        if (!runnable) {
            addErrorMessage(playerAction.getErrorMessage().toString());
        }

        return runnable;
    }
}
