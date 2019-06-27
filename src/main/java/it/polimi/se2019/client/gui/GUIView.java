package it.polimi.se2019.client.gui;

import it.polimi.se2019.client.View;
import it.polimi.se2019.client.util.Constants;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Logger;

public class GUIView extends View {

    private GUIController guiController;
    private BorderPane rootLayout;
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
            case Constants.RESPAWN:
                guiController.getActionTileController().getPowerUpForRespawn();
        }
    }

    @Override
    public void reportError(String error) {

    }

    @Override
    public void showGame() {

        // the next line is used for testing purposes only.
        //((Model)super.getModel()).boardDeserialize();

        this.initRootLayout();
        this.showGameBoard();

        this.getPrimaryStage().setResizable(false);
        this.getPrimaryStage().setFullScreen(true);
        this.getPrimaryStage().sizeToScene();
        this.getPrimaryStage().show();
        // TODO: redraw gameboard

    }

    public void initRootLayout() {
        try{
            // Load root layout from fxml file
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/RootLayout.fxml"));
            rootLayout = loader.load();

            // Set the scene containing the root layout
            Scene scene = new Scene(rootLayout);
            scene.getStylesheets().add("/css/root.css");
            primaryStage.setScene(scene);

        } catch(IOException e) {
            Logger.getGlobal().warning(e.toString());
        }
    }

    public void showGameBoard() {

        try{
            FXMLLoader gbLoader = new FXMLLoader();
            gbLoader.setLocation(ClientGui.class.getResource("/fxml/GameBoard.fxml"));
            AnchorPane gameBoard = gbLoader.load();
            guiController = gbLoader.getController();
            guiController.setView(this);
            setGuiController(guiController);

            // Set the scene containing the root layout
            rootLayout.setCenter(gameBoard);

            // initialization of the map must precede the initialization of the player boards

            guiController.init(getModel().getGame().getPlayerByColor(getPlayerColor()));

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
