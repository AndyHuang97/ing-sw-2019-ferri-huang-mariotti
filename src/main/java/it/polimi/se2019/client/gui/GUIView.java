package it.polimi.se2019.client.gui;

import it.polimi.se2019.client.View;
import it.polimi.se2019.client.util.Constants;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Logger;


/**
 * The GUIView class is an extension of the abstract View class. It implements the methods of the View for input buidling
 * and view selection for the gui application.
 *
 * @author andreahuang
 */
public class GUIView extends View {

    private GUIController guiController;
    private BorderPane rootLayout = null;
    private Stage primaryStage;
    private boolean userInput;
    private int inputTimeout;

    /**
     * This is the main constructor used to instatiate a GUIView, it store the primaryStage for the gui controller.
     *
     * @param primaryStage is the main stage of the javafx application.
     */
    public GUIView(Stage primaryStage) {
        inputTimeout = Integer.parseInt(ClientGui.prop.getProperty("game.input_timeout_seconds"));
        this.primaryStage = primaryStage;
        this.setCliTrueGuiFalse(false);
    }

    /**
     * The askInput method removes a runnable object from the list of request input. It runs it and asks
     * for input to the player.
     */
    @Override
    public void askInput() {
        if (!getInputRequested().isEmpty()) {
            getInputRequested().remove(0).run();
        }
    }


    public void timer(boolean quitApp) {
        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            userInput = false;
            while (!userInput && (System.currentTimeMillis() - startTime) < inputTimeout * 1000) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    // do nothing
                }
            }
            if (!userInput) {
                if (quitApp) {
                    Logger.getGlobal().info("Timer expired, quitting...");
                    Platform.exit();
                    System.exit(0);
                } else {
                    Logger.getGlobal().info("Timer expired, passing");
                    Platform.runLater(() -> guiController.handlePass());
                }
            }
        }).start();
    }

    @Override
    public void showMessage(String message) {
        showInternalMessage(message, true);
    }

    /**
     * The showMessage method interprets the message received from the server. It shows a different view based
     * on the message, and different allowed actions.
     *
     * @param message a response message containing info on the performed action.
     */
    public void showInternalMessage(String message, boolean startTimer) {
        switch (message) {
            case Constants.MAIN_ACTION:
                guiController.setInfoText("Select one action or powerup");
                guiController.storeMessage(message);
                guiController.showActionButtons();
                guiController.showPowerUps(Arrays.asList(Constants.TELEPORTER, Constants.NEWTON));
                guiController.showPass();
                if (startTimer) timer(false);
                return;
            case Constants.RESPAWN:
                guiController.setInfoText("Select one powerup for respawn");
                guiController.storeMessage(message);
                guiController.getPowerUpForRespawn();
                if (startTimer) timer(true);
                return;
            case Constants.RELOAD:
                guiController.setInfoText("Select one or more weapons to reload");
                guiController.storeMessage(message);
                guiController.showPass();
                guiController.getReload();
                if (startTimer) timer(false);
                return;
            case Constants.SHOOT:
                guiController.setInfoText("Select one effect");
                guiController.storeMessage(message);
                guiController.getActionUnit();
                guiController.showPass();
                if (startTimer) timer(false);
                return;
            case Constants.TARGETING_SCOPE:
                guiController.setInfoText("Select one or more Targeting Scopes");
                guiController.storeMessage(message);
                guiController.showPowerUps(Collections.singletonList(Constants.TARGETING_SCOPE));
                guiController.showPass();
                if (startTimer) timer(false);
                return;
            case Constants.TAGBACK_GRENADE:
                guiController.setInfoText("Select one or more Tagback Grenades");
                guiController.storeMessage(message);
                guiController.showPowerUps(Collections.singletonList(Constants.TAGBACK_GRENADE));
                guiController.showPass();
                if (startTimer) timer(false);
                return;
            case Constants.FINISHGAME:
                guiController.showRanking();
            default:
                return;
        }
    }

    /**
     * The reportError method shows an alert box to display the error message.
     *
     * @param error an error message containing info about an action's violations.
     */
    @Override
    public void reportError(String error) {
        if (!error.equals("")) {
            Logger.getGlobal().info("Reporting error: " + error);
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.WINDOW_MODAL);
            alert.initOwner(primaryStage);
            alert.setTitle("Action failed!");
            alert.setHeaderText("The cause of the failure is:");
            alert.setContentText(error);
            alert.showAndWait();
        }
    }

    /**
     * The showGame method renders the whole interface by reading the updated model.
     * It does a preemptive initialization only once to give some static behaviour to various graphic objects.
     *
     */
    @Override
    public void showGame() {

        if (rootLayout == null) {
            this.initGameBoard();
            this.getPrimaryStage().setResizable(false);
            this.getPrimaryStage().setFullScreen(true);
            this.getPrimaryStage().sizeToScene();
        }
        this.getGuiController().showMap();
        this.getGuiController().showPlayerBoards();
        this.getGuiController().showMyCards();
        this.getGuiController().showCurrentPlayer();


        this.getPrimaryStage().show();
    }

    /**
     * The initGameBoard method is used to load all the fxml files and adds some static behaviours.
     *
     */
    private void initGameBoard() {

        try{
            FXMLLoader gbLoader = new FXMLLoader();
            gbLoader.setLocation(getClass().getResource("/fxml/GameBoard.fxml"));
            AnchorPane gameBoard = gbLoader.load();
            guiController = gbLoader.getController();
            guiController.setView(this);
            setGuiController(guiController);

            // Set the scene containing the root layout
            rootLayout = new BorderPane();
            rootLayout.setPrefSize(1280, 768);
            Scene scene = new Scene(rootLayout);
            scene.getStylesheets().add("/css/root.css");
            rootLayout.setId("game-background");
            primaryStage.setScene(scene);
            rootLayout.setCenter(gameBoard);

            // initialization of the map must precede the initialization of the player boards

            guiController.init();

        } catch(IOException e) {
            Logger.getGlobal().warning(e.toString());
        }
    }

    /**
     * The getBackgroundColor method gives back a better coloring for graphic visualization associated to one
     * client player.
     *
     * @return a color string.
     */
    public String getBackgroundColor() {
        switch (super.getPlayerColor()) {
            case BLUE:
                return "teal";
            case YELLOW:
                return "yellow";
            case GREEN:
                return "green";
            case GREY:
                return "grey";
            case PURPLE:
                return "purple";
            default:
                return null;
        }
    }

    /**
     * Getter for guiController.
     *
     * @return the guiController
     */
    public GUIController getGuiController() {
        return guiController;
    }

    /**
     * Setter for guiController.
     *
     * @param guiController the new guiController.
     */
    public void setGuiController(GUIController guiController) {
        this.guiController = guiController;
    }

    /**
     * Getter for primaryStage.
     *
     * @return the primaryStage.
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Setter for userInput.
     *
     * @param userInput is a boolean indicating if a user input is to be expected.
     */
    public void setUserInput(boolean userInput) {
        this.userInput = userInput;
    }
}
