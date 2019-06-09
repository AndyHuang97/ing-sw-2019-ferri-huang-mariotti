package it.polimi.se2019.client.util;

import it.polimi.se2019.client.gui.MainApp;
import it.polimi.se2019.server.exceptions.PlayerNotFoundException;
import it.polimi.se2019.util.Response;
import javafx.application.Platform;

public class ClientCommandHandler {
    MainApp mainApp;
    
    public ClientCommandHandler(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void handle(Response request) {
        Platform.runLater(() -> internalHandle(request));
    }

    private void internalHandle(Response request) {
        if (request.getMessage().equals("ping")) {
            return;
        }
        if (request.getSuccess()) {
            this.mainApp.setGame(request.getGame());
            try {
                this.mainApp.setPlayerColor(request.getGame().getPlayerByNickname(mainApp.getNickname()).getColor());
            } catch (PlayerNotFoundException e) {
                e.printStackTrace();
            }
            this.mainApp.boardDeserialize();
            this.mainApp.initRootLayout();
            this.mainApp.showGameBoard();

            this.mainApp.getPrimaryStage().setResizable(false);
            this.mainApp.getPrimaryStage().setFullScreen(true);
            this.mainApp.getPrimaryStage().sizeToScene();
            this.mainApp.getPrimaryStage().show();
            // TODO: redraw gameboard
        }
    }
}
