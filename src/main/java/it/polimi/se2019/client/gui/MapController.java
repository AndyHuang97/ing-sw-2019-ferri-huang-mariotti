package it.polimi.se2019.client.gui;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.client.util.Util;
import it.polimi.se2019.server.exceptions.TileNotFoundException;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class MapController {

    private static final Logger logger = Logger.getLogger(MapController.class.getName());

    private MainApp mainApp;
    private List<GridPane> weaponCrateList;

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
        weaponCrateList = Arrays.asList(blueWeapons,redWeapons,yellowWeapons);
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

        mapImage.setImage(new Image(Constants.MAP_IMAGE + mainApp.getGame().getBoard().getId() + ".png"));

        try {
            // here the number of elements of the json array and the dimension of the grid pane
            // is the same
            Tile[][] tileMap = mainApp.getGame().getBoard().getTileMap();
            IntStream.range(0, tileMap[0].length)
                    .forEach(y -> IntStream.range(0, tileMap.length)
                            .forEach(x -> {
                                setUpGridButton(tileMap, x, y);
                                setUpGridAmmo(tileMap, x, y);
                            }));

            resetTileGridColor();
            tileGrid.setVisible(false);
            ammoGrid.setDisable(true);
            showPlayers();
            setUpWeaponCrates();
            showWeaponCrates();

        } catch (IllegalArgumentException e) {
            logger.warning("Invalid map.");
            e.printStackTrace();
        }
    }


    public void setUpGridButton(Tile[][] tileMap, int x, int y) {

        if (tileMap[x][y] != null) {
            AnchorPane anchorPane = (AnchorPane) tileGrid.getChildren().get(Util.convertToIndex(x,y));
            Button button = new Button("");
            button.setOpacity(0.4);
            AnchorPane.setTopAnchor(button, 0.0);
            AnchorPane.setRightAnchor(button, 0.0);
            AnchorPane.setBottomAnchor(button, 0.0);
            AnchorPane.setLeftAnchor(button, 0.0);

            button.setOnAction(event -> {
                button.setStyle("");
                button.setDisable(true);
                BorderPane root = (BorderPane) mainApp.getPrimaryStage().getScene().getRoot();
                GridPane progressBar = (GridPane) (root.getCenter()).lookup(Constants.PROGRESS_BAR);

                Util.isFirstSelection(root, progressBar);
                Util.updateCircle(progressBar);

                boolean last = progressBar.getChildren().stream()
                        .map(n -> (Circle) n)
                        .filter(Node::isVisible)
                        .allMatch(c -> c.getFill() == Paint.valueOf("green"));

                if (last) {
                    // if its the last element that needs to be selected it disables visibility of all other
                    // buttons
                    tileGrid.getChildren().stream()
                            .map(n -> (AnchorPane) n)
                            .filter(ap -> !ap.getChildren().isEmpty())
                            .map(ap -> (Button) ap.getChildren().get(0))
                            .filter(b -> !b.isDisable())
                            .forEach(b -> b.setVisible(false));
                }

                handleTileSelected(x,y);
            });

            anchorPane.getChildren().add(button);
        }
    }

    /**
     * This is the first initialization of the ammo grid, which defines the effect of mouse click
     * for every single ammo card.
     * @param tileMap is tile map.
     * @param x is the x coordinate of the tile.
     * @param y is the y coordintate of the tile.
     */
    public void setUpGridAmmo(Tile[][] tileMap, int x, int y) {

        if (tileMap[x][y] != null) {
            if (!tileMap[x][y].isSpawnTile()) {
                // gets the i-th anchorpane containing the imageview
                AnchorPane anchorPane = (AnchorPane) ((HBox) ammoGrid.getChildren().get(Util.convertToIndex(x, y))).getChildren().get(0);
                ImageView iv = (ImageView) anchorPane.getChildren().get(0);
                String id = mainApp.getGame().getBoard().getTileMap()[x][y].getAmmoCrate().getName();
                //TODO separate the image loading form the setup, no need to iterate the mouse click behavior
                iv.setImage(new Image(Constants.AMMO_PATH + id + ".png"));
                iv.setVisible(true);

                iv.setOnMouseClicked(event -> {
                    anchorPane.setDisable(true);
                    anchorPane.setOpacity(0.6);
                    BorderPane root = (BorderPane) mainApp.getPrimaryStage().getScene().getRoot();
                    GridPane progressBar = (GridPane) (root.getCenter()).lookup(Constants.PROGRESS_BAR);

                    Util.isFirstSelection(root, progressBar);
                    Util.updateCircle(progressBar);

                    ammoGrid.getChildren().stream()
                            .map(n -> (AnchorPane) ((HBox) n).getChildren().get(0))
                            .filter(ap -> ap != anchorPane)
                            .filter(ap -> ap.getChildren().get(0).isVisible())
                            .forEach(ap -> ap.setStyle(""));

                    handleAmmoCrateSelected(x, y);
                });
            }
        }
    }

    /**
     * Sets the mouse click behavior on weapon crates' cards.
     */
    public void setUpWeaponCrates() {

        BorderPane root = (BorderPane) mainApp.getPrimaryStage().getScene().getRoot();
        GridPane progressBar = (GridPane) (root.getCenter()).lookup(Constants.PROGRESS_BAR);

        weaponCrateList.stream()
                .forEach(wc -> {
                    wc.setDisable(true);
                    wc.getChildren().stream()
                            .forEach(n ->
                                    n.setOnMouseClicked(event -> {
                                        wc.setDisable(true);
                                        n.setDisable(true);
                                        n.setOpacity(0.6);

                                        Util.isFirstSelection(root, progressBar);
                                        Util.updateCircle(progressBar);

                                        handleWeaponSelected(1,1);
                                    })
                            );
                });
    }

    /**
     * Plainly shows the weapon crates.
     */
    public void showWeaponCrates() {
        Tile[][] tileMap = mainApp.getGame().getBoard().getTileMap();
        IntStream.range(0, tileMap[0].length)
                .forEach(y -> IntStream.range(0, tileMap.length)
                        .filter(x -> tileMap[x][y] != null)
                        .filter(x -> tileMap[x][y].isSpawnTile())
                        .forEach(x -> {
                            //System.out.println(x + "," + y);
                            String roomColor = tileMap[x][y].getRoomColor().getColor();
                            Optional<GridPane> optGrid = weaponCrateList.stream()
                                    .filter(wc -> wc.getId().split("Weapons")[0].equalsIgnoreCase(roomColor))
                                    .findFirst();
                            if (optGrid.isPresent()){
                                GridPane actualGrid = optGrid.get();
                                IntStream.range(0, 3)
                                        .forEach(i -> {
                                            ImageView iv = (ImageView) actualGrid.getChildren().get(i);
                                            String weaponID = tileMap[x][y].getWeaponCrate().get(i).getName();
                                            //System.out.println(Constants.WEAPON_PATH+weaponID+".png");
                                            iv.setImage(new Image(Constants.WEAPON_PATH+weaponID+".png"));
                                        });
                            }
                        }));
    }

    /**
     * Shows player on map.
     */
    public void showPlayers() {

        mainApp.getGame().getPlayerList().stream()
                .forEach(p -> {
                    try {
                        int[] coords = mainApp.getGame().getBoard().getTilePosition(p.getCharacterState().getTile());
                        VBox vbox = (VBox) playerGrid.getChildren().get(Util.convertToIndex(coords[0], coords[1]));

                        ObservableList<Node> firstRow = ((HBox) vbox.getChildren().get(1)).getChildren();
                        ObservableList<Node> secondRow = ((HBox) vbox.getChildren().get(2)).getChildren();

                        boolean isSecondRow = firstRow.stream()
                                .allMatch(Node::isVisible);
                        if (!isSecondRow) {
                            addPlayerCircle(firstRow, p);
                        }
                        else {
                            addPlayerCircle(secondRow, p);
                        }
                    } catch (TileNotFoundException e) {
                        e.printStackTrace();
                    }

                });
    }

    /**
     * Handles the selection of a button from tile grid.
     * @param x is the x coordinate in the grid.
     * @param y is the y coordinate in the grid.
     */
    public void handleTileSelected(int x, int y) {
        System.out.println("Tile selected: " + x + "," + y);
        mainApp.getInput(x,y);

    }

    public void handleCardSelected(int id) {
        mainApp.handleCardSelection();
    }

    public void handleWeaponSelected(int x, int y) {
        System.out.println("Weapon selected: " + x + "," + y);
        handleCardSelected(x);
    }

    /**
     * Handles the selection of an ammo card from ammo tile grid.
     * @param x is the x coordinate in the grid.
     * @param y is the y coordinate in the grid.
     */
    public void handleAmmoCrateSelected(int x, int y) {
        System.out.println("Ammocrate selected: "+Util.convertToIndex(x, y));
        handleCardSelected(x);
    }

    /**
     * Handles the selection of a button from player grid.
     * @param event
     */
    public void handlePlayerSelected(ActionEvent event) {

    }

    /**
     * Adds player in a row of the player grid.
     */
    public void addPlayerCircle(ObservableList<Node> row, Player p) {
        Optional<Node> optNode = row.stream()
                .filter(n -> !n.isVisible())
                .findFirst();
        optNode.ifPresent(node -> {
            node.setVisible(true);
            ((Circle) node).setFill(Paint.valueOf(p.getColor().getColor()));
            node.setOnMouseClicked(event -> {

            });
        });
    }

    /**
     * Disables and resets all grids.
     */
    public void disableGrids() {
        tileGrid.setVisible(false);
        resetTileGridColor();
        ammoGrid.setDisable(true);
        resetAmmoGridBorder();
        playerGrid.setDisable(true);
        resetWeaponGrids();
    }

    /**
     * Resets the tile grid's buttons to the player's color
     */
    public void resetTileGridColor() {
        tileGrid.getChildren().stream()
                .map(n -> (AnchorPane) n) // gets the anchorpane
                .filter(ap -> !ap.getChildren().isEmpty())
                .map(ap -> (Button) ap.getChildren().get(0))
                .forEach(b -> {
                    b.setStyle("-fx-background-color: "+mainApp.getBackgroundColor());
                    b.setDisable(false);
                    b.setVisible(true);
                });
    }

    /**
     * Resets the ammo grid's panes border.
     */
    public void resetAmmoGridBorder() {
        ammoGrid.getChildren().stream()
                .map(n -> (AnchorPane) ((HBox) n).getChildren().get(0))
                .filter(ap -> ap.getChildren().get(0).isVisible())
                .forEach(ap -> {
                    if(!ap.getStyleClass().isEmpty()){
                        ap.getStyleClass().remove(0);
                    }
                    ap.setDisable(false);
                    ap.setOpacity(1.0);
                });
    }

    /**
     * Resets the weapons grids' border.
     */
    public void resetWeaponGrids() {
        weaponCrateList.stream()
                .forEach(wc -> {
                    //wc.getStyleClass().remove(0);
                    if(!wc.getStyleClass().isEmpty()){
                        wc.getStyleClass().remove(0);
                    }
                    wc.getChildren().stream()
                            .forEach(n -> {
                                n.setDisable(false);
                                n.setOpacity(1.0);
                            });
                });
    }

    /**
     * Getter for the tile grid.
     * @return tile grid.
     */
    public GridPane getTileGrid() {
        return tileGrid;
    }
}
