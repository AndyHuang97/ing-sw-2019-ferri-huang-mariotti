package it.polimi.se2019.client.gui;

import com.google.gson.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

public class MapController {

    private static final Logger logger = Logger.getLogger(MapController.class.getName());
    private static final String JSON_PATH = "src/main/resources/json/maps/map";
    private static final String IMAGE_PATH = "/images/maps/map";
    private static final String JSON = ".json";
    private static final String PNG = ".png";
    private MainApp mainApp;

    @FXML
    private ImageView mapImage;
    @FXML
    private GridPane tileGrid;
    @FXML
    private TextField mapNumber;
    @FXML
    private GridPane ammoGrid;
    @FXML
    private GridPane playerGrid;
    @FXML
    private GridPane blueWeapons;
    @FXML
    private GridPane yellowWeapons;
    @FXML
    private GridPane redWeapons;
    @FXML
    private AnchorPane powerupDeck;
    @FXML
    private AnchorPane weaponDeck;

    /**
     * The map initializer.
     */
    @FXML
    public void initialize() {
        // need to wait initialization of other parameters
    }

    /**
     *  Is called by the main application to set itself.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }


    /**
     * Handles the loading of a map. It shows the clickable tiles based on
     * the map's json file and the present ammo tiles.
     */
    @FXML
    public void handleMapLoading() {

        try {
            int mapIndex = 0;
            //int mapIndex = Integer.parseInt(mapNumber.getText());
            mapImage.setImage(new Image(IMAGE_PATH + mapIndex + PNG));

            BufferedReader bufferedReader = new BufferedReader(
                    new FileReader(JSON_PATH + mapIndex + JSON));
            try {
                JsonParser parser = new JsonParser();
                JsonObject json = parser.parse(bufferedReader).getAsJsonObject();
                JsonArray jsonArray = json.getAsJsonArray("tiles");

                ObservableList<Node> tileChildren = tileGrid.getChildren();
                ObservableList<Node> ammoChildren = ammoGrid.getChildren();

                // remove buttons from the anchor pane if present
                tileChildren.stream()
                        .map(n -> (AnchorPane) n)
                        .filter(ap -> !ap.getChildren().isEmpty())
                        .forEach(pane -> pane.getChildren().remove(0));

                // here the number of elements of the json array and the dimension of the grid pane
                // is the same
                int i = 0;
                for (JsonElement tileElement : jsonArray) {
                    JsonObject jsonTile = tileElement.getAsJsonObject();

                    setUpTileGrid(jsonTile, tileChildren, i);
                    setUpAmmoGrid(jsonTile, ammoChildren, i);

                    i++;
                }
                tileGrid.setVisible(false);

            } catch (IllegalArgumentException e) {
                logger.warning("Invalid map.");
            } finally {
                bufferedReader.close();
            }
        } catch (IOException e) {
            logger.warning("Buffer I/O errors.");
        }
    }

    public void setUpTileGrid(JsonObject jsonTile, ObservableList<Node> children, int i) {

        String type = jsonTile.get("type").getAsString();
        if (!type.equals("NoTile")) {
            AnchorPane anchorPane = (AnchorPane) children.get(i);
            Button button = new Button("");
            button.setOpacity(0.4);
            AnchorPane.setTopAnchor(button, 0.0);
            AnchorPane.setRightAnchor(button, 0.0);
            AnchorPane.setBottomAnchor(button, 0.0);
            AnchorPane.setLeftAnchor(button, 0.0);
            // TODO handle event
            button.setOnAction(event -> {

                mainApp.getPlayerInput().put("tile", i);
            });

            anchorPane.getChildren().add(button);
        }
    }

    public void setUpAmmoGrid(JsonObject jsonTile, ObservableList<Node> children, int i) {

        String type = jsonTile.get("type").getAsString();
        if (type.equals("NormalTile")) {
            // gets the i-th anchorpane containing the imageview
            AnchorPane anchorPane = (AnchorPane) ((HBox) children.get(i)).getChildren().get(0);
            ImageView iv = (ImageView) anchorPane.getChildren().get(0);
            iv.setImage(new Image("/images/ammo/AD_ammo_042.png"));
            // TODO handle event
            iv.setOnMouseClicked(event -> System.out.println(i));
        }

    }

    /**
     * Handles the selection of a button from tile grid.
     * @param event
     */
    public void handleTileSelected(ActionEvent event) {
        mainApp.getInput(event);

    }

    /**
     * Handles the selection of a button from ammo tile grid.
     * @param event
     */
    public void handleAmmoTileSelected(ActionEvent event) {

    }

    /**
     * Handles the selection of a button from player grid.
     * @param event
     */
    public void handlePlayerSelected(ActionEvent event) {

    }

    /**
     * Getter for the tile grid.
     * @return tile grid.
     */
    public GridPane getTileGrid() {
        return tileGrid;
    }

}
