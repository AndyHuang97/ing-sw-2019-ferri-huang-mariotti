package it.polimi.se2019.client.gui;

import it.polimi.se2019.server.games.player.PlayerColor;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class PlayerBoardController {

    private static final Logger logger = Logger.getLogger(PlayerBoardController.class.getName());

    @FXML
    private TextField playerColor;


    private MainApp mainApp;

    @FXML
    private void initialize() {

    }

    /**
     *  Is called by the main application to set itself.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }


    /**
     * Adds damage from attacker player to damage bar.
     */
    @FXML
    public void handleDamage() {

        List<PlayerColor> colors = Arrays.asList(PlayerColor.BLUE);
        AnchorPane anchorPlayerBoard = (AnchorPane) mainApp.getPrimaryStage().getScene().lookup("#PlayerBoard");
        HBox hbox = (HBox) anchorPlayerBoard.lookup("#hbox");

        //logger.info(hbox.toString());
        //logger.info(hbox.getChildren().toString());
        int i = 0;
        for (Node n : hbox.getChildren()) {
            //logger.info(n.toString());
            if( i < 5) {
                if (((AnchorPane) n).getChildren().isEmpty()) {
                    logger.info("found an empty cell");
                    Circle circle = new Circle();
                    circle.setRadius(20.0);
                    circle.fillProperty().setValue(Paint.valueOf(PlayerColor.BLUE.getColor()));
                    AnchorPane.setTopAnchor(circle, 26.0);
                    AnchorPane.setRightAnchor(circle, 12.0);
                    AnchorPane.setBottomAnchor(circle, 27.0);
                    AnchorPane.setLeftAnchor(circle, 11.0);
                    ((AnchorPane) n).getChildren().add(circle);
                }
            }
            i++;

        }
    }

    public void handleMarker() {

    }
}
