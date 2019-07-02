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

public abstract class ControllerState {
    static private List<String> errorMessages = new ArrayList<>();

    /**
     *
     * @param commandHandler
     * @return sends a message to the correct commandHandler of the current player
     */
    //TODO get the correct commandHandler
    public abstract void sendSelectionMessage(CommandHandler commandHandler);

    /**
     * This method contains all the logic of a state. It checks whether the input is among those allowed in the state.
     * If if fails it stays in the same state and keeps waiting for the same input, otherwise it performs the check
     * and run methods of the actions, with possible modifications on the model and then goes to a new state.
     * @param playerActions the list of actions received from the player
     * @param game the game on which to execute the actions
     * @param player the player sending the input
     * @return the new state of the controlelr
     */
    public abstract ControllerState nextState(List<PlayerAction> playerActions, Game game, Player player);

    public void sendErrorMessages(CommandHandler commandHandler) {
        final Character[] DASH_SPACE = {'-', ' '};
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

    public static void addErrorMessage(String errorMessage) {
        errorMessages.add(errorMessage);
    }

    public static boolean checkPlayerActionAndSaveError(PlayerAction playerAction) {
        boolean runnable = playerAction.check();

        if (!runnable) {
            addErrorMessage(playerAction.getErrorMessage().toString());
        }

        return runnable;
    }
}
