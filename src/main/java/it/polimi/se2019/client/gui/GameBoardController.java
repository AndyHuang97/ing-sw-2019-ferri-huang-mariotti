package it.polimi.se2019.client.gui;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class GameBoardController {

    @FXML
    private AnchorPane playerBoard;

    private MainApp mainApp;

    @FXML
    private void initialize() {

    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public AnchorPane getPlayerBoard() {
        return playerBoard;
    }

    public void setPlayerBoard(AnchorPane playerBoard) {
        this.playerBoard = playerBoard;
    }
}
