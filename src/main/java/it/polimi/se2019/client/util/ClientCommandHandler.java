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

    public void handle(Response request) {
        if (this.view.isCliTrueGuiFalse()) internalHandle(request);
        else Platform.runLater(() -> internalHandle(request));
    }

    private void internalHandle(Response request) {
        if (request.getMessage().equals("ping")) {
            return;
        }
        if (request.getSuccess()) {
            // game initialization
            this.view.setGame(request.getGame());
            try {
                this.view.setPlayerColor(request.getGame().getPlayerByNickname(this.view.getNickname()).getColor());
            } catch (PlayerNotFoundException e) {
                Logger.getGlobal().warning(e.toString());
            }
            this.view.showGame();
        }
    }
}
