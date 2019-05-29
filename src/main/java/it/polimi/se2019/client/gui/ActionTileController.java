package it.polimi.se2019.client.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;

import java.util.stream.IntStream;

public class ActionTileController {

    private MainApp mainApp;
    private GridPane tileGrid;
    private GridPane ammoGrid;
    private GridPane playerGrid;
    private Label infoText;
    private GridPane progressBar;
    private Button confirmButton;
    private Button cancelButton;

    @FXML
    private Button mmm;
    @FXML
    private Button mg;
    @FXML
    private Button s;
    @FXML
    private Button r;

    public void initialize() {

    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void initParams() {
        initGrids();
        initInfo();
    }

    public void initGrids() {
        BorderPane root = (BorderPane) mainApp.getPrimaryStage().getScene().getRoot();
        VBox vBox = (VBox) (root.getCenter()).lookup("#leftVbox");
        AnchorPane map = (AnchorPane) vBox.getChildren().get(0);

        tileGrid = (GridPane) map.lookup("#tileGrid");
        ammoGrid = (GridPane) map.lookup("#ammoGrid");
        playerGrid = (GridPane) map.lookup("#playerGrid");
    }

    public void initInfo() {
        BorderPane root = (BorderPane) mainApp.getPrimaryStage().getScene().getRoot();

        infoText = (Label) root.getCenter().lookup("#infoText");
        infoText.setText("Select an action(1)");

        progressBar = (GridPane) root.getCenter().lookup("#progressBar");

        confirmButton = (Button) root.getCenter().lookup("#confirmButton");
        cancelButton = (Button) root.getCenter().lookup("#cancelButton");
    }

    public void disableActionButtons() {
        mmm.setDisable(true);
        mg.setDisable(true);
        s.setDisable(true);
        r.setDisable(true);
    }

    @FXML
    public void handleMove(){

        disableActionButtons();
        tileGrid.toFront();
        tileGrid.setDisable(false);
        tileGrid.setVisible(true);

        infoText.setText("Select 1 tile ");
        cancelButton.setDisable(false);

        IntStream.range(0, 4)
                .forEach(i -> progressBar.getChildren().get(i).setVisible(true));
    }

    @FXML
    public void handleGrab() {

        disableActionButtons();
        ammoGrid.toFront();
        ammoGrid.setDisable(false);
        ammoGrid.setVisible(true);

        infoText.setText("Select 1 card ");
        cancelButton.setDisable(false);


        ammoGrid.getChildren().stream()
                .map(n -> (AnchorPane) ((HBox) n).getChildren().get(0))
                .filter(ap -> ap.getChildren().get(0).isVisible())
                .forEach(ap -> ap.setStyle("-fx-border-color: red"));
        IntStream.range(0, 1)
                .forEach(i -> ((Circle) progressBar.getChildren().get(i)).setVisible(true));
    }
}
