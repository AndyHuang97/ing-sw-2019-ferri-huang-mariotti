package it.polimi.se2019.client.util;

import it.polimi.se2019.client.View;
import it.polimi.se2019.server.exceptions.PlayerNotFoundException;
import it.polimi.se2019.util.Response;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The client CommandHandler is what bridges the view to the network, like the server command handler it wraps the network.
 * The difference here is that here are only handled the commands arriving form the server.
 *
 * @author FF
 *
 */
public class ClientCommandHandler {
    private View view;

    /**
     * Just a constructor
     *
     */
    public ClientCommandHandler(View view) {
        this.view = view;
    }

    /**
     * This is called internally to show the game at the start when it receives the game copy
     *
     * @param request the request containing the game
     *
     */
    private void gameStart(Response request) {
        // game initialization
        view.setGame(request.getGame());
        try {
            view.setPlayerColor(request.getGame().getPlayerByNickname(view.getNickname()).getColor());
        } catch (PlayerNotFoundException e) {
            Logger.getGlobal().warning(e.toString());
        }
        view.showGame();
    }

    /**
     * This is called internally to update the game copy and to show it
     *
     * @param request the request containing the game update
     *
     */
    private void gameUpdate(Response request) {
        Logger.getGlobal().info("Update Data not null in command handler");
        view.update(request);
        request.getUpdateData().forEach(stateUpdate -> Logger.getGlobal().info("Received an update: " + stateUpdate.toString()));
        view.showGame();
    }

    /**
     * This handles the request from the server, the cli and gui treat the requests in a different way, ping requests are treated separately.
     * The order is very important to the stability of the app
     *
     * @param request the request containing the message
     *
     */
    public void handle(Response request) {
        if (request.getMessage().contains("ping")) {
            view.pong();
        } else if (view.isCliTrueGuiFalse()) {
            if (request.getSuccess() && request.getMessage().equals(Constants.FINISHGAME)) {
                view.showMessage(request.getMessage());
            } else if (request.getSuccess() && request.getGame() != null) {
                gameStart(request);
            } else if (request.getSuccess() && request.getUpdateData() != null) {
                gameUpdate(request);
            } else if (!request.getSuccess()) {
                view.reportError(request.getMessage());
            } else {
                new Thread(() -> internalCliHandle(request)).start();
            }
        } else {
            Platform.runLater(() -> internalGuiHandle(request));
        }
    }

    /**
     * The threaded request handler for the cli
     *
     * @param request the request containing the message
     *
     */
    private synchronized void internalCliHandle(Response request) {
        view.showMessage(request.getMessage());
    }

    /**
     * The threaded request handler for the gui
     *
     * @param request the request containing the message
     *
     */
    private synchronized void internalGuiHandle(Response request) {
        if (request.getSuccess()) {
            // game initialization
            if (request.getGame() != null) {
                gameStart(request);
            }
            if (request.getUpdateData() != null) {
                gameUpdate(request);
            }
            view.showMessage(request.getMessage());
        } else {
            view.reportError(request.getMessage());
        }
    }
}
