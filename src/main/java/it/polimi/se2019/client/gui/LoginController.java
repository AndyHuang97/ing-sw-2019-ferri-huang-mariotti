package it.polimi.se2019.client.gui;

import it.polimi.se2019.client.util.ConnectionType;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private ChoiceBox connectType;
    @FXML
    private TextField nickname;
    @FXML
    private TextField ip;
    @FXML
    private TextField port;

    private MainApp mainApp;

    @FXML
    public void initialize() {
        connectType.getItems().add(ConnectionType.RMI);
        connectType.getItems().add(ConnectionType.SOCKET);
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    public void handleConnect() {
        try {
            if (nickname.getText().equals("")) { }

            mainApp.connect(nickname.getText(), ip.getText(), Integer.parseInt(port.getText()), (ConnectionType) connectType.getValue());
        } catch (NumberFormatException e) {

        }
    }


}
