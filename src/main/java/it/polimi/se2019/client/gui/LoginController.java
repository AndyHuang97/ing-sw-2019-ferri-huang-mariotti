package it.polimi.se2019.client.gui;

import it.polimi.se2019.client.View;
import it.polimi.se2019.client.util.Constants;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * The LoginController class sets up the stage for a player's login input.
 * It asks a nickname, an ip, a connection type, and a map to connect to the server.
 *
 * @author andreahuang
 *
 */
public class LoginController {

    private static final Logger logger = Logger.getLogger(LoginController.class.getName());

    @FXML
    private ChoiceBox connectType;
    @FXML
    private ChoiceBox map;
    @FXML
    private TextField nickname;
    @FXML
    private TextField ip;

    private Stage loginStage;
    private View view;

    /**
     * The initialize method is used to initialize the choice box objects.
     *
     */
    @FXML
    public void initialize() {
        ip.setText(ClientGui.prop.getProperty("server.host"));
        connectType.getItems().add(Constants.RMI);
        connectType.getItems().add(Constants.SOCKET);
        map.getItems().add(Constants.map0);
        map.getItems().add(Constants.map1);
        map.getItems().add(Constants.map2);
        map.getItems().add(Constants.map3);
    }

    /**
     * Setter for the login stage.
     *
     * @param stage the stage of the login
     */
    public void setLoginStage(Stage stage) {
        this.loginStage = stage;
    }

    /**
     * Setter for the view. The view provides the connect method
     * @param view the view of the client
     */
    public void setView(View view) {
        this.view = view;
    }

    /**
     * Exception for nickname not found in the login form.
     *
     */
    public class NicknameNotFound extends Exception {
        public NicknameNotFound(String errorMessage) {
            super(errorMessage);
        }
    }

    /**
     * Exception for ip no found in the login form.
     *
     */
    public class IpNotFound extends Exception{
        public IpNotFound(String errorMessage) {
            super(errorMessage);
        }
    }

    /**
     * Exception for choice not found in the login form.
     *
     */
    public class ChoiceNotSelected extends Exception {
        public ChoiceNotSelected(String errorMessage) {
            super(errorMessage);
        }
    }

    /**
     * The handleConnect method handle the mouse click event on the connect button.
     * If successful, it starts a connection with a server.
     *
     */
    @FXML
    public void handleConnect() {
        try {

            if (nickname.getText().equals("")) { throw new NicknameNotFound("Nickname not found"); }
            if (ip.getText().equals("")) { throw new IpNotFound("Ip not found"); }
            if (connectType.getValue() == null) { throw new ChoiceNotSelected("connection type"); }
            if (map.getValue() == null) { throw new ChoiceNotSelected("map"); }
            view.setNickname(nickname.getText());
            view.connect(nickname.getText(), ip.getText(), (String) connectType.getValue(), (String) map.getValue());
            loginStage.close();

        } catch (NicknameNotFound e) {
            showNoSelectionAlert("nickname");
        } catch (IpNotFound e) {
            showNoSelectionAlert("ip");
        } catch (NumberFormatException e) {
            showWrongFormatAlert();
        } catch (ChoiceNotSelected e) {
            showNoSelectionAlert(e.getMessage());
        }
    }

    /**
     * The showNoSelectionAlert method shows an alert box displaying a no selection warning.
     *
     * @param textField
     */
    public void showNoSelectionAlert(String textField) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("No Selection");
        alert.setHeaderText("No " + textField + " selected.");
        alert.setContentText("Please input a " + textField + ".");
        alert.showAndWait();
    }

    /**
     * The showWrongFormatAlert method shows an alert box displaying a wrong format warning.
     */

    public void showWrongFormatAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Format error");
        alert.setHeaderText("Port must be an integer.");
        alert.setContentText("Please input an integer.");
        alert.showAndWait();
    }
}
