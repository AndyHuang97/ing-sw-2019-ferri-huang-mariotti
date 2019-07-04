package it.polimi.se2019.client.gui;

import it.polimi.se2019.client.View;
import it.polimi.se2019.client.util.Constants;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Logger;

public class GUIView extends View {

    private GUIController guiController;
    private BorderPane rootLayout = null;
    private Stage primaryStage;

    public GUIView() {
        this.setCliTrueGuiFalse(false);
    }


    public GUIView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.setCliTrueGuiFalse(false);
    }

    @Override
    public void askInput() {
        if (!getInputRequested().isEmpty()) {
            getInputRequested().remove(0).run();
        }
    }

    @Override
    public void showMessage(String message) {
        switch (message) {
            case Constants.MAIN_ACTION:
                guiController.setInfoText("Select one action or powerup");
                guiController.storeMessage(message);
                guiController.showActionButtons();
                guiController.showPowerUps(Arrays.asList(Constants.TELEPORTER, Constants.NEWTON));
                guiController.showPass();
                return;
            case Constants.RESPAWN:
                guiController.setInfoText("Select one powerup for respawn");
                guiController.storeMessage(message);
                guiController.getPowerUpForRespawn();
                return;
            case Constants.RELOAD:
                guiController.setInfoText("Select one or more weapons to reload");
                guiController.storeMessage(message);
                guiController.showPass();
                guiController.getReload();
                return;
            case Constants.SHOOT:
                guiController.setInfoText("Select one effect");
                guiController.storeMessage(message);
                guiController.getActionUnit();
                guiController.showPass();
                return;
            case Constants.TARGETING_SCOPE:
                guiController.setInfoText("Select one or more Targeting Scopes");
                guiController.storeMessage(message);
                guiController.showPowerUps(Collections.singletonList(Constants.TARGETING_SCOPE));
                guiController.showPass();
                return;
            case Constants.TAGBACK_GRENADE:
                guiController.setInfoText("Select one or more Tagback Grenades");
                guiController.storeMessage(message);
                guiController.showPowerUps(Collections.singletonList(Constants.TAGBACK_GRENADE));
                guiController.showPass();
                return;
            case Constants.FINISHGAME:
                guiController.showRanking();
            default:
                return;
        }
    }

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


        this.getPrimaryStage().show();
    }

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




    public GUIController getGuiController() {
        return guiController;
    }

    public void setGuiController(GUIController guiController) {
        this.guiController = guiController;
    }

    public BorderPane getRootLayout() {
        return rootLayout;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
