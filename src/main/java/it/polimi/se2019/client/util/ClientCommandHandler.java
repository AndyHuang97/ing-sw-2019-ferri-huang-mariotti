package it.polimi.se2019.client.util;

import it.polimi.se2019.client.View;
import it.polimi.se2019.server.exceptions.PlayerNotFoundException;
import it.polimi.se2019.util.Response;
import javafx.application.Platform;

import java.util.logging.Logger;

public class ClientCommandHandler {
    private View view;
    
    public ClientCommandHandler(View view) {
        this.view = view;
    }

    private void gameStart(Response request) {
        // game initialization
        this.view.setGame(request.getGame());
        try {
            this.view.setPlayerColor(request.getGame().getPlayerByNickname(this.view.getNickname()).getColor());
        } catch (PlayerNotFoundException e) {
            Logger.getGlobal().warning(e.toString());
        }
        this.view.showGame();
    }

    private void gameUpdate(Response request) {
        Logger.getGlobal().info("Update Data not null in command handler");
        this.view.update(request);
        request.getUpdateData().forEach(stateUpdate -> Logger.getGlobal().info("Received an update: " + stateUpdate.toString()));
        this.view.showGame();
    }

    public void handle(Response request) {
        if (view.isCliTrueGuiFalse()) {
            if (request.getSuccess() && request.getMessage().equals(Constants.FINISHGAME)) {
                this.view.showMessage(request.getMessage());
            } else if (request.getSuccess() && request.getGame() != null) {
                gameStart(request);
            } else if (request.getSuccess() && request.getUpdateData() != null) {
                gameUpdate(request);
            } else if (!request.getSuccess()) {
                this.view.reportError(request.getMessage());
            } else {
                new Thread(() -> internalCliHandle(request)).start();
            }
        } else {
            Platform.runLater(() -> internalGuiHandle(request));
        }
    }

    private synchronized void internalCliHandle(Response request) {
        this.view.showMessage(request.getMessage());
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
            this.view.showMessage(request.getMessage());
        } else {
            this.view.reportError(request.getMessage());
        }
    }
}
