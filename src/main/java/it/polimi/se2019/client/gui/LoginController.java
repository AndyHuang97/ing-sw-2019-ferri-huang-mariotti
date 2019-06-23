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
    private Stage waitingStage;
    private View view;

    @FXML
    public void initialize() {
        try (InputStream input = new FileInputStream("src/main/resources/config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            ip.setText(prop.getProperty("server.host"));
            connectType.getItems().add(Constants.RMI);
            connectType.getItems().add(Constants.SOCKET);
            map.getItems().add(Constants.map0);
            map.getItems().add(Constants.map1);
            map.getItems().add(Constants.map2);
            map.getItems().add(Constants.map3);
        } catch(IOException e) {
            logger.info(e.toString());
        }
    }

    public void setLoginStage(Stage stage) {
        this.loginStage = stage;
    }

    public void setView(View view) {
        this.view = view;
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

    public class ChoiceNotSelected extends Exception {
        public ChoiceNotSelected(String errorMessage) {
            super(errorMessage);
        }
    }

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

    public void showNoSelectionAlert(String textField) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        //alert.initOwner(((GUIView) view).getPrimaryStage());
        alert.setTitle("No Selection");
        alert.setHeaderText("No " + textField + " selected.");
        alert.setContentText("Please input a " + textField + ".");
        alert.showAndWait();
    }

    public void showWrongFormatAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        //alert.initOwner(((GUIView) view).getPrimaryStage());
        alert.setTitle("Format error");
        alert.setHeaderText("Port must be an integer.");
        alert.setContentText("Please input an integer.");
        alert.showAndWait();
    }
    public void showWaiting() {

        Label waitingLabel = new Label("Waiting for other players to connect ...");
        Button enterButton = new Button("Enter");
        enterButton.setOnAction(event -> waitingStage.close());
        enterButton.setVisible(false);

        HBox hbox = new HBox(waitingLabel, enterButton);
        hbox.setAlignment(Pos.CENTER);
        Scene waitingScene = new Scene(new BorderPane(hbox),600.0,400.0);

        waitingStage = new Stage();
        waitingStage.setScene(waitingScene);
        waitingStage.showAndWait();
    }

    public void stopWating() {

        waitingStage.close();
    }
}
