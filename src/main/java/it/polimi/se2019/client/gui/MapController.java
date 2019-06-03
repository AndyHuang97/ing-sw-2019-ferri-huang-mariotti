package it.polimi.se2019.client.gui;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.client.util.NamedImage;
import it.polimi.se2019.client.util.Util;
import it.polimi.se2019.server.exceptions.TileNotFoundException;
import it.polimi.se2019.server.games.KillShotTrack;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MapController {

    private static final Logger logger = Logger.getLogger(MapController.class.getName());

    private MainApp mainApp;
    private List<GridPane> weaponCrateList;

    @FXML
    private ImageView mapImage;
    @FXML
    private GridPane killShotTrackPane;
    @FXML
    private GridPane frenzyPane;
    @FXML
    private GridPane tileGrid;
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
    @FXML
    private GridPane progressBar;

    /**
     * The map initializer that is called wen the Map.fxml file is loaded.
     *
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
     *
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
                                initTileGrid(tileMap, x, y);
                                initAmmoGrid(tileMap, x, y);
                                initPlayerGrid(tileMap, x, y);
                            }));

            resetTileGridStyle();
            tileGrid.setVisible(false);
            ammoGrid.setDisable(true);
            showKillShotTrack();
            showPlayers();
            initWeaponCrates();
            showWeaponCrates();

        } catch (IllegalArgumentException e) {
            logger.warning(e.toString());
        }
    }


    /**
     * Adds a button to every tile of the map.
     *
     * @param tileMap is the tile map.
     * @param x is the x coordinate of the tile.
     * @param y is the y coordintate of the tile.
     */
    public void initTileGrid(Tile[][] tileMap, int x, int y) {

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

                Util.ifFirstSelection(root, progressBar);
                Util.updateCircle(progressBar);

                if (Util.isLastSelection(progressBar)) {
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
     * This is the first initialization of the ammo grid, which defines the effect of mouse click.
     * for every single ammo card.
     *
     * @param tileMap is the tile map.
     * @param x is the x coordinate of the tile.
     * @param y is the y coordintate of the tile.
     */
    public void initAmmoGrid(Tile[][] tileMap, int x, int y) {

        if (tileMap[x][y] != null) {
            if (!tileMap[x][y].isSpawnTile()) {
                // gets the i-th anchorpane containing the imageview
                AnchorPane anchorPane = (AnchorPane) ((HBox) ammoGrid.getChildren().get(Util.convertToIndex(x, y))).getChildren().get(0);
                ImageView iv = (ImageView) anchorPane.getChildren().get(0);
                String id = mainApp.getGame().getBoard().getTileMap()[x][y].getAmmoCrate().getName();
                //TODO separate the image loading form the setup, no need to iterate the mouse click behavior
                iv.setImage(new NamedImage(Constants.AMMO_PATH + id + ".png", Constants.AMMO_PATH));
                iv.setVisible(true);

                iv.setOnMouseClicked(event -> {
                    anchorPane.setDisable(true);
                    anchorPane.setOpacity(0.6);
                    BorderPane root = (BorderPane) mainApp.getPrimaryStage().getScene().getRoot();

                    Util.ifFirstSelection(root, progressBar);
                    Util.updateCircle(progressBar);

                    //iv.setStyle("");
                    /*ammoGrid.getChildren().stream()
                            .map(n -> (AnchorPane) ((HBox) n).getChildren().get(0))
                            .filter(ap -> ap != anchorPane)
                            .filter(ap -> ap.getChildren().get(0).isVisible())
                            .forEach(ap -> ap.setStyle(""));

                     */

                    NamedImage image = (NamedImage) iv.getImage();
                    handleAmmoCrateSelected(image.getName());
                });
            }
        }
    }

    /**
     *
     *
     * @param tileMap
     * @param x
     * @param y
     */
    public void initPlayerGrid(Tile[][] tileMap, int x, int y) {

        BorderPane root = (BorderPane) mainApp.getPrimaryStage().getScene().getRoot();

        playerGrid.setDisable(true);
        if (tileMap[x][y] != null) {
            VBox vbox = (VBox) playerGrid.getChildren().get(Util.convertToIndex(x, y));

            vbox.getChildren().stream()
                    .map(n -> (HBox) n)
                    .filter(row -> !row.getChildren().isEmpty())
                    .forEach(row -> row.getChildren().stream()
                            .map(n -> (Circle) n)
                            .forEach(c ->
                                c.setOnMouseClicked(event -> {
                                    c.setDisable(true);
                                    c.setOpacity(0.6);

                                    Util.ifFirstSelection(root, progressBar);
                                    Util.updateCircle(progressBar);

                                    if (Util.isLastSelection(progressBar)) {
                                        playerGrid.setDisable(true);
                                    }

                                    handlePlayerSelected(c.getFill().toString());
                                }))
                    );
        }
    }

    /**
     * Sets the mouse click behavior on weapon crates' cards.
     *
     */
    public void initWeaponCrates() {

        BorderPane root = (BorderPane) mainApp.getPrimaryStage().getScene().getRoot();

        weaponCrateList.stream()
                .forEach(wc -> {
                    wc.setDisable(true);
                    wc.getChildren().stream()
                            .map(n -> (ImageView) n)
                            .forEach(w ->
                                    mainApp.getGameBoardController().setCardSelectionBehavior(w, wc, Constants.GRAB)
                            );
                });
    }

    /**
     * Shows the weapon crates.
     *
     */
    public void showWeaponCrates() {
        Tile[][] tileMap = mainApp.getGame().getBoard().getTileMap();
        IntStream.range(0, tileMap[0].length)
                .forEach(y -> IntStream.range(0, tileMap.length)
                        .filter(x -> tileMap[x][y] != null)
                        .filter(x -> tileMap[x][y].isSpawnTile())
                        .forEach(x -> {
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
                                            iv.setImage(new NamedImage(Constants.WEAPON_PATH+weaponID+".png", Constants.WEAPON_PATH));
                                        });
                            }
                        })
                );
    }

    /**
     * Shows the kill shot track.
     *
     */
    public void showKillShotTrack() {
        KillShotTrack kt = mainApp.getGame().getKillshotTrack();

        int offset = killShotTrackPane.getChildren().size() - kt.getKillsForFrenzy();

        IntStream.range(offset, killShotTrackPane.getChildren().size())
                .forEach(i -> {
                    StackPane sp = (StackPane) killShotTrackPane.getChildren().get(i);
                    ImageView iv = (ImageView) sp.getChildren().get(0);
                    Label points = (Label) sp.getChildren().get(1);

                    EnumMap<PlayerColor, Integer> colorIntegerEnumMap = kt.getDeathTrack().get(i-offset);
                    if (i < kt.getKillCounter() + offset) { //shows player tokens
                        Optional<PlayerColor> optPc= Arrays.stream(PlayerColor.values())
                                .filter(colorIntegerEnumMap::containsKey)
                                .findFirst();
                        if (optPc.isPresent()) {
                            iv.setImage(Util.getPlayerToken(optPc.get()));
                            points.setText(colorIntegerEnumMap.get(optPc.get()).toString());
                        }
                    }
                    else { //shows skulls and frenzy pane
                        if (kt.getKillCounter() + offset == killShotTrackPane.getChildren().size()-1) {

                            int j = 0;
                            for (PlayerColor pc : PlayerColor.values()) {
                                sp = (StackPane) frenzyPane.getChildren().get(j);
                                iv = (ImageView) sp.getChildren().get(0);
                                points = (Label) sp.getChildren().get(1);
                                iv.setImage(Util.getPlayerToken(pc));
                                if (colorIntegerEnumMap.get(pc) != null) {
                                    points.setText(colorIntegerEnumMap.get(pc).toString());
                                }
                                else {
                                    points.setText("0");
                                }
                                j++;
                            }
                        }else {
                            iv.setImage(new Image(Constants.SKULL_PATH));
                        }
                    }
                });
    }

    /**
     * Shows the players on map.
     *
     */
    public void showPlayers() {

        mainApp.getGame().getPlayerList().stream()
                .forEach(p -> {
                    try {
                        int[] coords = mainApp.getGame().getBoard().getTilePosition(p.getCharacterState().getTile());
                        VBox vbox = (VBox) playerGrid.getChildren().get(Util.convertToIndex(coords[0], coords[1]));
                        vbox.setDisable(false);

                        HBox firstRow = (HBox) vbox.getChildren().get(1);
                        HBox secondRow = (HBox) vbox.getChildren().get(2);

                        firstRow.setDisable(false);
                        secondRow.setDisable(false);

                        boolean isSecondRow = firstRow.getChildren().stream()
                                .allMatch(Node::isVisible);
                        if (!isSecondRow) {
                            addPlayerCircle(firstRow, p);
                        }
                        else {
                            addPlayerCircle(secondRow, p);
                        }
                    } catch (TileNotFoundException e) {
                        logger.warning(e.toString());
                    }
                });
    }

    /**
     * Adds player in a row of the player grid.
     *
     * @param row is the horizontal box containing the circles that represent players.
     * @param player is the player to be added to the row
     */
    public void addPlayerCircle(HBox row, Player player) {
        Optional<Node> optNode = row.getChildren().stream()
                .filter(n -> !n.isVisible())
                .findFirst();
        optNode.ifPresent(node -> {
            if (!node.getStyleClass().isEmpty()) {
                node.getStyleClass().remove(0);
            }
            node.setVisible(true);
            node.setDisable(false);
            ((Circle) node).setFill(Paint.valueOf(player.getColor().getColor()));
        });
    }

    /**
     * Handles the selection of a button from tile grid.
     *
     * @param x is the x coordinate in the grid.
     * @param y is the y coordinate in the grid.
     */
    public void handleTileSelected(int x, int y) {
        System.out.println("Tile selected: " + x + "," + y);
        mainApp.getGameBoardController().addInput(Constants.TILE, String.valueOf(Util.convertToIndex(x,y)));
    }

    /**
     * Handles the selection of a weapon card from a weapon crate.
     *
     */
    public void handleWeaponInCrateSelected(String id) {
        System.out.println("Weapon selected: " + id);
        mainApp.getGameBoardController().addInput(Constants.GRAB, id);
    }

    /**
     * Handles the selection of an ammo card from ammo tile grid.
     *
     */
    public void handleAmmoCrateSelected(String id) {
        System.out.println("Ammocrate selected: "+ id);
        mainApp.getGameBoardController().addInput(Constants.GRAB, id);
    }

    /**
     * Handles the selection of a button from player grid.
     *
     * @param color
     */
    public void handlePlayerSelected(String color) {
        System.out.println("Selected player: " + color);
        String id = mainApp.getGame().getPlayerList().stream()
                .filter(p -> Paint.valueOf(color).equals(Paint.valueOf(p.getColor().getColor())))
                .collect(Collectors.toList()).get(0).getId();
        System.out.println(color + " " + id);
        mainApp.getGameBoardController().addInput(Constants.PLAYER, id);
    }

    /**
     * Disables and resets all grids.
     */
    public void resetGrids() {
        resetTileGridStyle();
        resetAmmoGridStyle();
        resetPlayerGridStyle();
        showPlayers();
        tileGrid.setVisible(false);
        ammoGrid.setDisable(true);
        playerGrid.setDisable(true);
        resetWeaponCratesStyle();

    }

    /**
     * Resets the tile grid's buttons to the player's color.
     *
     */
    public void resetTileGridStyle() {
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
     * Resets the ammo grid's style.
     *
     */
    public void resetAmmoGridStyle() {
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
     * Resets the player grid's style.
     *
     */
    public void resetPlayerGridStyle() {
        playerGrid.getChildren().stream()
                .map(n -> (VBox) n)
                .forEach(vBox -> vBox.getChildren().stream()
                        .map(n -> (HBox) n)
                        .filter(hbox -> !hbox.getChildren().isEmpty())
                        .forEach(hBox -> hBox.getChildren().stream()
                                .map(n -> (Circle) n)
                                .filter(Node::isVisible)
                                .forEach(c -> {
                                    c.setVisible(false);
                                    c.setOpacity(1.0);
                                    if (!c.getStyleClass().isEmpty()) {
                                        c.getStyleClass().remove(0);
                                    }
                                })
                        )
                );
    }

    /**
     * Resets the weapons grids' style.
     *
     */
    public void resetWeaponCratesStyle() {
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
     *
     * @return tile grid.
     */
    public GridPane getTileGrid() {
        return tileGrid;
    }

    /**
     * Getter fot the progress bar that contains the circles representing the number
     * of players to be selected.
     *
     * @return the progress bar.
     */
    public GridPane getProgressBar() {
        return progressBar;
    }
}
