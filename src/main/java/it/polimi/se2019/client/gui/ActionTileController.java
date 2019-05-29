package it.polimi.se2019.client.gui;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.client.util.Util;
import it.polimi.se2019.server.exceptions.TileNotFoundException;
import it.polimi.se2019.server.games.board.Tile;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class ActionTileController {

    private MainApp mainApp;
    private GridPane tileGrid;
    private GridPane ammoGrid;
    private GridPane playerGrid;
    private Label infoText;
    private GridPane progressBar;
    private Button cancelButton;
    private List<GridPane> weaponCrateList;
    private GridPane myWeapons;
    private GridPane myPowerups;

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

    public void init() {
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

        weaponCrateList = new ArrayList<>();
        weaponCrateList.add((GridPane) map.lookup("#blueWeapons"));
        weaponCrateList.add((GridPane) map.lookup("#redWeapons"));
        weaponCrateList.add((GridPane) map.lookup("#yellowWeapons"));

        myWeapons = (GridPane) vBox.lookup("#myWeapons");
        myPowerups = (GridPane) vBox.lookup("#myPowerups");

    }

    public void initInfo() {
        BorderPane root = (BorderPane) mainApp.getPrimaryStage().getScene().getRoot();

        infoText = (Label) root.getCenter().lookup("#infoText");
        infoText.setText("Select an action(1)");

        progressBar = (GridPane) root.getCenter().lookup(Constants.PROGRESS_BAR);

        //confirmButton = (Button) root.getCenter().lookup("#confirmButton");
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

        setUpProgressBar(3);
    }

    @FXML
    public void handleGrab() {

        disableActionButtons();
        ammoGrid.toFront();
        ammoGrid.setDisable(false);
        ammoGrid.setVisible(true);

        infoText.setText("Select 1 card ");
        cancelButton.setDisable(false);

        Tile t  = mainApp.getGame().getCurrentPlayer().getCharacterState().getTile();
        System.out.println(t);
        try {
            int[] coords = mainApp.getGame().getBoard().getTilePosition(t);
            if (t.isSpawnTile()) {
                System.out.println("spawntile");
                String roomColor = t.getRoomColor().getColor();
                Optional<GridPane> optGrid = weaponCrateList.stream()
                        .filter(wc -> wc.getId().split("Weapons")[0].equalsIgnoreCase(roomColor))
                        .findFirst();
                if (optGrid.isPresent()){
                    optGrid.get().setDisable(false);
                    optGrid.get().getStyleClass().add("my-node");
                }
            }
            else {
                System.out.println("normaltile");
                HBox hBox = (HBox) ammoGrid.getChildren().get(Util.convertToIndex(coords[0], coords[1]));
                Node n = hBox.getChildren().get(0);
                n.getStyleClass().add("my-node");
            }
        } catch (TileNotFoundException e) {
            e.printStackTrace();
        }

        /*ammoGrid.getChildren().stream()
                .map(n -> (AnchorPane) ((HBox) n).getChildren().get(0))
                .filter(ap -> ap.getChildren().get(0).isVisible())
                .forEach(ap -> ap.setStyle("-fx-border-color: red"));
         */
        setUpProgressBar(1);
    }

    @FXML
    public void handleShoot() {

        disableActionButtons();
        infoText.setText("Select 1 card ");
        cancelButton.setDisable(false);

        myWeapons.setDisable(false);
        myWeapons.getStyleClass().add("my-node");

        setUpProgressBar(1);
    }

    @FXML
    public void handleReload() {

    }

    public void handleActionUnit() {

    }

    public void setUpProgressBar(int numOfTargets) {
        IntStream.range(0, numOfTargets)
                .forEach(i -> ((Circle) progressBar.getChildren().get(i)).setVisible(true));
    }

}
