package it.polimi.se2019.client.gui;

import it.polimi.se2019.client.Model;
import it.polimi.se2019.client.View;
import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.games.player.PlayerColor;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.io.*;
import java.util.logging.Logger;

/**
 * This is the main class that starts the javafx application.
 */
public class ClientGui extends Application {

    private static final Logger logger = Logger.getLogger(ClientGui.class.getName());

    private LoginController loginController;
    private BorderPane rootlayout;
    private Stage primaryStage;
    private View view;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // this next line is really important to make everything work
        Platform.setImplicitExit(false);

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Adrenaline");

        view = new GUIView(primaryStage);

        //showLogin();

        //testing

        //view.setPlayerColor(PlayerColor.GREEN);
        //((Model)view.getModel()).initGame();
        //view.showGame();

        //view.showMessage(Constants.POWERUP);

    }

    public void showLogin() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/Login.fxml"));
            AnchorPane login = loader.load();
            LoginController controller = loader.getController();
            controller.setView(view);
            this.setLoginController(controller);


            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            controller.setLoginStage(loginStage);

            Scene scene = new Scene(login);
            loginStage.setScene(scene);
            loginStage.initOwner(primaryStage);
            loginStage.showAndWait();

        } catch (IOException e) {
            logger.warning(e.toString());
        }
    }


    public LoginController getLoginController() {
        return loginController;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }



    /**
     * Sends input via network.
     */
    public void sendInput(){

    }

    /**
     * Getter of the primary stage.
     * @return the primary stage.
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

}
