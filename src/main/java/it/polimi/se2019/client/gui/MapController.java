package it.polimi.se2019.client.gui;

import com.google.gson.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

public class MapController {

    private static final Logger logger = Logger.getLogger(MapController.class.getName());
    private static final String JSON_PATH = "src/main/resources/json/maps/map";
    private static final String IMAGE_PATH = "/images/maps/map";
    private static final String JSON = ".json";
    private static final String PNG = ".png";

    @FXML
    private ImageView mapImage;
    @FXML
    private GridPane tileList;
    @FXML
    private TextField mapNumber;
    @FXML
    private GridPane ammoList;
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

    private MainApp mainApp;

    /**
     * The map initializer.
     */
    @FXML
    public void initialize() {

    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    public void handleMapLoading() {

        try {
            int mapIndex = Integer.parseInt(mapNumber.getText());
            mapImage.setImage(new Image(IMAGE_PATH + mapIndex + PNG));

            BufferedReader bufferedReader = new BufferedReader(
                    new FileReader(JSON_PATH + mapIndex + JSON));
            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(bufferedReader).getAsJsonObject();
            JsonArray jsonArray = json.getAsJsonArray("tiles");

            ObservableList<Node> children = tileList.getChildren();

            // remove buttons from the anchors if present
            children.stream()
                    .map(n -> (AnchorPane) n)
                    .filter(ap -> !ap.getChildren().isEmpty())
                    .forEach(pane -> pane.getChildren().remove(0));

            // here the number of elements of the json array and the dimension of the grid pane
            // is the same
            int i = 0;
            for (JsonElement tileElement : jsonArray) {
                JsonObject jsonTile = tileElement.getAsJsonObject();


                String name = jsonTile.get("type").getAsString();
                if (!name.equals("NoTile")) {
                    AnchorPane anchorPane = (AnchorPane) children.get(i);


                    Button button = new Button("");
                    button.setOpacity(0.5);
                    AnchorPane.setTopAnchor(button, 0.0);
                    AnchorPane.setRightAnchor(button, 0.0);
                    AnchorPane.setBottomAnchor(button, 0.0);
                    AnchorPane.setLeftAnchor(button, 0.0);
                    anchorPane.getChildren().add(button);
                    tileList.toFront();
                }
                i++;
            }
            bufferedReader.close();
        }catch (FileNotFoundException e) {
            logger.warning("File not found.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            logger.warning("Invalid map.");
            e.printStackTrace();
        }

    }

}
