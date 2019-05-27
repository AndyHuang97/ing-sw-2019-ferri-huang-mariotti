package it.polimi.se2019.client.gui;

import it.polimi.se2019.server.games.player.PlayerColor;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This is the main class of the GUI.
 */
public class MainApp extends Application {

    private static final Logger logger = Logger.getLogger(MainApp.class.getName());

    private Map<String, Integer> playerInput;
    private PlayerColor playerColor;
    private Stage primaryStage;
    private BorderPane rootlayout;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        setPlayerColor(PlayerColor.BLUE);
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Adrenaline");

        initRootLayout();
        showGameBoard();

        primaryStage.setResizable(false);
        primaryStage.setFullScreen(true);
        primaryStage.sizeToScene();
        primaryStage.show();

    }

    public void initRootLayout() {
        try{
            // Load root layout from fxml file
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/RootLayout.fxml"));
            rootlayout = (BorderPane) loader.load();

            // Set the scene containing the root layout
            Scene scene = new Scene(rootlayout);
            primaryStage.setScene(scene);

            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);

        } catch(IOException e) {
            logger.warning("Could not find resource.");
        }
    }

    public void showGameBoard() {

        try{
            FXMLLoader gbLoader = new FXMLLoader();
            gbLoader.setLocation(MainApp.class.getResource("/fxml/GameBoard.fxml"));
            AnchorPane gameBoard = (AnchorPane) gbLoader.load();
            GameBoardController gbController = gbLoader.getController();
            gbController.setMainApp(this);

            // Set the scene containing the root layout
            rootlayout.setCenter(gameBoard);

            // initialization of the map must precede the initialization of the player boards
            gbController.initMap();
            gbController.initPlayerBoards(playerColor);



        } catch(IOException e) {
            logger.warning("Could not find resource.");
        }
    }

    @FXML
    public static void getInput(ActionEvent event) {

    }

    /**
     * Getter of the primary stage.
     * @return the primary stage.
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Getter of the player color.
     * @return the player color.
     */
    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    /**
     * Setter of the player color.
     * @param playerColor is the player's color.
     */
    public void setPlayerColor(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    public Map<String, Integer> getPlayerInput() {
        return playerInput;
    }
}
