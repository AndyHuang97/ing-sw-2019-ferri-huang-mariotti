package it.polimi.se2019.client.gui;

import it.polimi.se2019.client.util.Constants;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class LoginController {

    private static final Logger logger = Logger.getLogger(LoginController.class.getName());

    @FXML
    private ChoiceBox connectType;
    @FXML
    private TextField nickname;
    @FXML
    private TextField ip;

    private Stage loginStage;
    private MainApp mainApp;

    @FXML
    public void initialize() {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %5$s%6$s%n");
        try (InputStream input = new FileInputStream("src/main/resources/config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            ip.setText(prop.getProperty("server.host"));
            connectType.getItems().add(Constants.RMI);
            connectType.getItems().add(Constants.SOCKET);
        } catch(IOException e) {
            logger.info(e.toString());
        }
    }

    public void setLoginStage(Stage stage) {
        this.loginStage = stage;
    }
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public class NicknameNotFound extends Exception {
        public NicknameNotFound(String errorMessage) {
            super(errorMessage);
        }
    }

    public class IpNotFound extends Exception{
        public IpNotFound(String errorMessage) {
            super(errorMessage);
        }
    }

    public class ConnectionTypeNotSelected extends Exception {
        public ConnectionTypeNotSelected(String errorMessage) {
            super(errorMessage);
        }
    }

    @FXML
    public void handleConnect() {
        String exception = null;
        try {

            if (nickname.getText().equals("")) { throw new NicknameNotFound("Nickname not found"); }
            if (ip.getText().equals("")) { throw new IpNotFound("Ip not found"); }
            if (connectType.getValue() == null) { throw new ConnectionTypeNotSelected("Connection type not selected"); }

            mainApp.connect(nickname.getText(), ip.getText(), (String) connectType.getValue());
            loginStage.close();
        } catch (NicknameNotFound e) {
            showNoSelectionAlert("nickname");
        } catch (IpNotFound e) {
            showNoSelectionAlert("ip");
        } catch (NumberFormatException e) {
            showWrongFormatAlert();
        } catch (ConnectionTypeNotSelected e) {
            showNoSelectionAlert("connection type");
        }
    }

    public void showNoSelectionAlert(String textField) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initOwner(mainApp.getPrimaryStage());
        alert.setTitle("No Selection");
        alert.setHeaderText("No " + textField + " selected.");
        alert.setContentText("Please input a " + textField + ".");
        alert.showAndWait();
    }

    public void showWrongFormatAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initOwner(mainApp.getPrimaryStage());
        alert.setTitle("Format error");
        alert.setHeaderText("Port must be an integer.");
        alert.setContentText("Please input an integer.");
        alert.showAndWait();
    }
}
