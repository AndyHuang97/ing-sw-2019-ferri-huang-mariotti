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

public class ClientCommandHandler {
    private View view;
    
    public ClientCommandHandler(View view) {
        this.view = view;
    }

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

    private void gameUpdate(Response request) {
        Logger.getGlobal().info("Update Data not null in command handler");
        view.update(request);
        request.getUpdateData().forEach(stateUpdate -> Logger.getGlobal().info("Received an update: " + stateUpdate.toString()));
        view.showGame();
    }

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

    private synchronized void internalCliHandle(Response request) {
        view.showMessage(request.getMessage());
    }

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
