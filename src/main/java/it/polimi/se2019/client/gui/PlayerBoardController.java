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
    @FXML
    private TextField damageAmount;
    @FXML
    private HBox hbox;

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

        String color = playerColor.getText();
        Integer amount = Integer.parseInt(damageAmount.getText());
        logger.info(color + " - " + amount);

        //logger.info(hbox.toString());
        //logger.info(hbox.getChildren().toString());
        int i = 0;
        for (Node n : hbox.getChildren()) {
            logger.info(((AnchorPane) n).getChildren().toString());
            logger.info(i + "," + amount);
            if (((AnchorPane) n).getChildren().isEmpty() && i < amount) {
                logger.info("EMPTY CELL");

                Node token = getDmgMarkerToken(color);

                AnchorPane.setTopAnchor(token, 12.0);
                AnchorPane.setRightAnchor(token, 7.0);
                AnchorPane.setBottomAnchor(token, 11.0);
                AnchorPane.setLeftAnchor(token, 7.0);
                ((AnchorPane) n).getChildren().add(token);
                i++;
            }



        }
    }

    // TODO make the token image and replace the circle node
    public Node getDmgMarkerToken(String color) {
        Circle circle = new Circle();
        circle.setRadius(10.0);
        circle.fillProperty().setValue(Paint.valueOf(color));
        return circle;
    }

    public void handleMarker() {

    }
}
