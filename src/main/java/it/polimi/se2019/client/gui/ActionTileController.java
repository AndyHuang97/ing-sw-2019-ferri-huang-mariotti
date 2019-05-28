package it.polimi.se2019.client.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
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

        infoText = (Label) (root.getCenter()).lookup("#infoText");
        infoText.setText("Select an action(1)");

        progressBar = (GridPane) (root.getCenter()).lookup("#progressBar");

        confirmButton = (Button) (root.getCenter()).lookup("#confirmButton");
        cancelButton = (Button) (root.getCenter()).lookup("#cancelButton");
    }

    @FXML
    public void handleMMM(){

        tileGrid.toFront();
        tileGrid.getChildren().stream()
                .map(n -> (AnchorPane) n) // gets the anchorpane
                .filter(ap -> !ap.getChildren().isEmpty())
                .map(ap -> (Button) ap.getChildren().get(0))
                .forEach(b -> {
                    b.setStyle("-fx-background-color: "+mainApp.getBackgroundColor());
                    b.setDisable(false);
                });
        tileGrid.setDisable(false);
        tileGrid.setVisible(true);

        infoText.setText("Select 3(max) tiles ");
        confirmButton.setDisable(false);
        cancelButton.setDisable(false);
        IntStream.range(0, 3)
                .forEach(i -> ((Circle) progressBar.getChildren().get(i)).setVisible(true));

    }
}
