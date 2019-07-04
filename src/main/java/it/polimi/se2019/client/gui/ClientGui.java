package it.polimi.se2019.client.gui;

import it.polimi.se2019.client.View;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.io.*;
import java.util.logging.Logger;

/**
 * This is the main class that starts the javafx application. It shows an initial login window, and then lets the player
 * enter the game when enough players are connected to play.
 *
 * @author andreahuang
 */
public class ClientGui extends Application {

    private static final Logger logger = Logger.getLogger(ClientGui.class.getName());

    private Stage primaryStage;
    private View view;

    /**
     * The main method that starts the gui application.
     *
     * @param args is the vector of args accepted by the application
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * The start method instantiates the GUIView, passing the primary stage, and then it shows the login window.
     *
     * @param primaryStage is the main stage on which the game is shown.
     */
    @Override
    public void start(Stage primaryStage) {
        // this next line is really important to make everything work
        Platform.setImplicitExit(false);

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Adrenaline");

        view = new GUIView(primaryStage);

        showLogin();

        //testing
//        view.setPlayerColor(PlayerColor.GREEN);
//        ((Model)view.getModel()).initGame();
//        view.showGame();
//        view.showMessage(Constants.FINISHGAME);
////        Player player = view.getModel().getGame().getPlayerByColor(PlayerColor.GREEN);
////        Weapon weapon = player.getCharacterState().getWeaponBag().get(0);
////        view.getModel().getGame().setCurrentWeapon(weapon);
////        weapon.getActionUnitList().forEach(au -> Logger.getGlobal().info(au.getId()));
////        weapon.getOptionalEffectList().forEach(au -> Logger.getGlobal().info(au.getId()));
////        view.showMessage(Constants.SHOOT);
//        view.setNickname("Giorno");
//        view.showGame();


    }

    /**
     * This showLogin method loads the login pane from its fxml, and sets the style of the scene.
     * It lets the player choose its name, the server's ip, the connection type, and map.
     *
     */
    public void showLogin() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/Login.fxml"));
            Pane login = loader.load();
            LoginController controller = loader.getController();
            controller.setView(view);


            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            controller.setLoginStage(loginStage);

            Scene scene = new Scene(login);
            scene.getStylesheets().add("/css/root.css");
            login.setId("login-background");
            loginStage.setScene(scene);
            loginStage.initOwner(primaryStage);
            loginStage.setResizable(false);
            loginStage.showAndWait();

        } catch (IOException e) {
            logger.warning(e.toString());
        }
    }

    /**
     * Getter of the primary stage. It lets different parts of the gui controller to access the primary stage.
     *
     * @return the primary stage.
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

}
