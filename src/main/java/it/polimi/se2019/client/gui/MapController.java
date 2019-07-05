package it.polimi.se2019.client.gui;

import it.polimi.se2019.client.View;
import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.client.util.NamedImage;
import it.polimi.se2019.client.util.Util;
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

/**
 * The MapController class contains all the elements of the map(ammo crates, weapon crates, players, and kill shot track).
 * It provides methods to initialize the map's graphic elements' behaviour, methods to show the graphic elements, and
 * methods to reset the graphic elements' style.
 *
 * @author andreahuang
 */
public class MapController {

    private static final Logger logger = Logger.getLogger(MapController.class.getName());
    private static double ANCHOR = 0.0;

    private View view;
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
    private GridPane shootTileGrid;
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
    private GridPane progressBar;

    /**
     * The map initializer that is called wen the Map.fxml file is loaded.
     * It adds the container of weaponcrates in one list for better easier processing.
     *
     */
    @FXML
    public void initialize() {
        // need to wait initialization of other parameters
        weaponCrateList = Arrays.asList(blueWeapons,redWeapons,yellowWeapons);
    }

    /**
     * The setView method is called by the main application to set itself and give access to the model for linking.
     *
     * @param view is the view that interacts with the server.
     *
     */
    public void setView(View view) {
        this.view = view;
    }

    /**
     * The handleMapLoading method is used for the loading of a map. It initializes the clickable tiles based on
     * the map's id, the ammocrate cards, and the players.
     *
     */
    public void handleMapLoading() {

        // recognizes the board from its id.
        Logger.getGlobal().info(view.getModel().getGame().getBoard().toString());
        Logger.getGlobal().info(Constants.MAP_IMAGE + view.getModel().getGame().getBoard().getId() + ".png");
        mapImage.setImage(new Image(Constants.MAP_IMAGE + view.getModel().getGame().getBoard().getId() + ".png"));

        try {
            // here the number of elements of the json array and the dimension of the grid pane
            // is the same
            Tile[][] tileMap = view.getModel().getGame().getBoard().getTileMap();
            IntStream.range(0, tileMap[0].length)
                    .forEach(y -> IntStream.range(0, tileMap.length)
                            .forEach(x -> {
                                initTileGrid(tileMap, x, y);
                                initAmmoGrid(tileMap, x, y);
                                initPlayerGrid(tileMap, x, y);
                            }));

            resetTileGridStyle(tileGrid);
            resetTileGridStyle(shootTileGrid);
            tileGrid.setVisible(false);
            ammoGrid.setDisable(true);

        } catch (IllegalArgumentException e) {
            logger.warning(e.toString());
            e.printStackTrace();
        }
    }


    /**
     * Adds a button to every tile of the map. There are two tile grids, one relative to the move,
     * and the other to the shoot action. The differentiation is needed for the effects of the weapons.
     *
     * @param tileMap is the tile map.
     * @param x is the x coordinate of the tile.
     * @param y is the y coordintate of the tile.
     *
     */
    public void initTileGrid(Tile[][] tileMap, int x, int y) {

        if (tileMap[x][y] != null) {
            AnchorPane anchorPane = (AnchorPane) tileGrid.getChildren().get(Util.convertToIndex(x,y));
            AnchorPane shootAncorPane = (AnchorPane) shootTileGrid.getChildren().get(Util.convertToIndex(x,y));
            Button button = new Button("");
            Button shootButton = new Button("");

            setButtonTile(tileGrid, button, () -> handleTileSelected(Util.convertToIndex(x,y)));
            setButtonTile(shootTileGrid, shootButton, () -> handleShootTileSelected(Util.convertToIndex(x,y)));

            anchorPane.getChildren().add(button);
            shootAncorPane.getChildren().add(shootButton);

        }
    }

    /**
     * The setButtonTile method sets the behaviour of the mouse click event of each button from a tile.
     * It provides a Runnable handleAction to support different kinds of input handling.
     *
     * @param gridPane is the grid pane that contains button.
     * @param button is the button which the mouse click event is set on.
     * @param handleAction is a runnable that builds the input for the mouse click event.
     */
    public void setButtonTile(GridPane gridPane, Button button, Runnable handleAction) {
        button.setOpacity(0.4);
        AnchorPane.setTopAnchor(button, ANCHOR);
        AnchorPane.setRightAnchor(button, ANCHOR);
        AnchorPane.setBottomAnchor(button, ANCHOR);
        AnchorPane.setLeftAnchor(button, ANCHOR);

        button.setOnAction(event -> {
            button.setStyle("");
            button.setDisable(true);

            BorderPane root = (BorderPane) ((GUIView) view).getPrimaryStage().getScene().getRoot();
            Button confirmButton = (Button) root.lookup("#confirmButton");
            Util.ifFirstSelection(confirmButton, progressBar);
            Util.updateCircle(progressBar);

            if (Util.isLastSelection(progressBar)) {
                // if its the last element that needs to be selected it disables visibility of all other
                // buttons
                gridPane.getChildren().stream()
                        .map(n -> (AnchorPane) n)
                        .filter(ap -> !ap.getChildren().isEmpty())
                        .map(ap -> (Button) ap.getChildren().get(0))
                        .filter(b -> !b.isDisable())
                        .forEach(b -> b.setVisible(false));
            }

            handleAction.run();
        });
    }

    /**
     * The initAmmoGrid is the first initialization of the ammo grid, which defines the effect of mouse click.
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

//                ((GUIView)view).getGuiController().setCardSelectionBehavior(iv, anchorPane, Constants.GRAB);
            }
        }
    }

    /**
     * The showAmmoGrid method show the ammocrate on the map. It links the model's ammocrates with its graphic counterpart.
     *
     */
    public void showAmmoGrid() {
        view.getModel().getGame().getBoard().getTileList().stream().filter(Objects::nonNull).filter(tile -> !tile.isSpawnTile())
                .forEach(tile -> {
                    AnchorPane anchorPane = (AnchorPane) ((HBox) ammoGrid.getChildren().get(Integer.parseInt(tile.getId()))).getChildren().get(0);
                    ImageView iv = (ImageView) anchorPane.getChildren().get(0);
                    if (tile.getAmmoCrate() != null) {
                        String id = tile.getAmmoCrate().getName();
                        iv.setImage(new NamedImage(Constants.AMMO_PATH + id + ".png", Constants.AMMO_PATH));
                        iv.setVisible(true);
                        iv.setDisable(true);
                    } else {
                        iv.setVisible(false);
                    }
                });
    }

    /**
     * The initPlayerGrid method is called to set the behaviours of each circle that represents a player.
     *
     * @param tileMap is the tile map of the model.
     * @param x is the x coordinate.
     * @param y is the y coordinate.
     */
    public void initPlayerGrid(Tile[][] tileMap, int x, int y) {

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
                                    c.setOpacity(Constants.CLICKED_OPACITY);

                                    BorderPane root = (BorderPane) ((GUIView) view).getPrimaryStage().getScene().getRoot();
                                    Button confirmButton = (Button) root.lookup("#confirmButton");
                                    Util.ifFirstSelection(confirmButton, progressBar);
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
     * The showWeaponCrates method shows the weapon crates on the map. It links the model's weapons with its graphic counterpart.
     *
     */
    public void showWeaponCrates() {
        Tile[][] tileMap = view.getModel().getGame().getBoard().getTileMap();
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
                                actualGrid.getChildren().forEach(node -> node.setVisible(false));
                                IntStream.range(0, tileMap[x][y].getWeaponCrate().size())
                                        .forEach(i -> {
                                            ImageView iv = (ImageView) actualGrid.getChildren().get(i);
                                            if (tileMap[x][y].getWeaponCrate().get(i) != null) {
                                                String weaponID = tileMap[x][y].getWeaponCrate().get(i).getName();
                                                iv.setImage(new NamedImage(Constants.WEAPON_PATH + weaponID + ".png", Constants.WEAPON_PATH));
                                                iv.setVisible(true);
                                            } else {
                                                iv.setVisible(false);
                                            }
                                        });
                            }
                        })
                );
    }

    /**
     * The showKillShotTrack method shows the killShotTrack on the map. It links the model's killShotTrack with its graphic counterpart.
     *
     */
    public void showKillShotTrack() {
        KillShotTrack kt = view.getModel().getGame().getKillShotTrack();

        int offset = killShotTrackPane.getChildren().size() - kt.getKillsForFrenzy();
        Logger.getGlobal().info("\nKillshot track offset: " + offset + "\nKillshot track pane children size: " + killShotTrackPane.getChildren().size());

        IntStream.range(offset, killShotTrackPane.getChildren().size())
                .forEach(i -> {
                    StackPane sp = (StackPane) killShotTrackPane.getChildren().get(i);
                    ImageView iv = (ImageView) sp.getChildren().get(0);
                    Label points = (Label) sp.getChildren().get(1);

                    Map<PlayerColor, Integer> colorIntegerMap = kt.getDeathTrack().get(i-offset);
                    if (i < kt.getKillCounter() + offset) { //shows player tokens
                        Optional<PlayerColor> optPc= Arrays.stream(PlayerColor.values())
                                .filter(colorIntegerMap::containsKey)
                                .findFirst();
                        if (optPc.isPresent()) {
                            iv.setImage(Util.getPlayerToken(optPc.get()));
                            points.setText(colorIntegerMap.get(optPc.get()).toString());
                        }
                    }
                    else { //shows skulls and frenzy pane
                        Logger.getGlobal().info("\nkills+offset: " +kt.getKillCounter() + offset+
                                "\nsize-1: "+ (killShotTrackPane.getChildren().size()-1));
                        if (offset + kt.getKillCounter()  == killShotTrackPane.getChildren().size()) {

                            int j = 0;
                            for (PlayerColor pc : PlayerColor.values()) {
                                sp = (StackPane) frenzyPane.getChildren().get(j);
                                iv = (ImageView) sp.getChildren().get(0);
                                points = (Label) sp.getChildren().get(1);
                                iv.setImage(Util.getPlayerToken(pc));
                                if (colorIntegerMap.get(pc) != null) {
                                    points.setText(colorIntegerMap.get(pc).toString());
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
     * The showPlayers method shows the players on the map. It links the model's players' position with its
     * graphic counterpart.
     *
     */
    public void showPlayers() {

        resetPlayerGridStyle();
        view.getModel().getGame().getPlayerList().stream()
                .filter(player -> player.getCharacterState().getTile() != null)
                .forEach(p -> {
                    int[] coords = {p.getCharacterState().getTile().getxPosition(), p.getCharacterState().getTile().getyPosition()};
                    VBox vbox = (VBox) playerGrid.getChildren().get(Util.convertToIndex(coords[0], coords[1]));
                    Logger.getGlobal().info(p.getUserData().getNickname()+"-> x: "+coords[0]+"; y: "+coords[1]);
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
                });
    }

    /**
     * The addPlayerCircle method adds a player in a row of the player grid.
     *
     * @param row is the horizontal box containing the circles that represent players.
     * @param player is the player to be added to the row.
     */
    private void addPlayerCircle(HBox row, Player player) {
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
     * The handleTileSelected method handles the selection of a button from the normal tile grid.
     *
     * @param id is the id/position in the list of tiles.
     */
    public void handleTileSelected(int id) {
        System.out.println("Tile selected: " + id);
        int[] coords = Util.convertToCoords(id);
        Tile tile = view.getModel().getGame().getBoard().getTile(coords[0], coords[1]);
        ((GUIView) view).getGuiController().addInput(Constants.MOVE, tile.getId());
    }

    /**
     * The handleShootTileSelected method handles the selection of a button from the shoot tile grid.
     *
     * @param id is the id/position in the list of tiles.
     */
    public void handleShootTileSelected(int id) {
        System.out.println("Shoot Tile selected: " + id);
        int[] coords = Util.convertToCoords(id);
        Tile tile = view.getModel().getGame().getBoard().getTile(coords[0], coords[1]);
        ((GUIView) view).getGuiController().addInput(Constants.SHOOT, tile.getId());
    }

    /**
     * The handlePlayerSelected method handles the selection of a button from player grid.
     *
     * @param color is the color of the selected player to be sent as input.
     */
    public void handlePlayerSelected(String color) {
        String id = view.getModel().getGame().getPlayerList().stream()
                .filter(p -> Paint.valueOf(color).equals(Paint.valueOf(p.getColor().getColor())))
                .collect(Collectors.toList()).get(0).getId();
        ((GUIView) view).getGuiController().addInput(Constants.SHOOT, id);
    }

    /**
     * The resetGrids method disables and resets all grids. It calls some helpers to disable different kinds
     * of grid panes.
     *
     */
    public void resetGrids() {
        resetTileGridStyle(tileGrid);
        resetTileGridStyle(shootTileGrid);
        resetAmmoGridStyle();
        resetPlayerGridStyle();
        showPlayers();
        tileGrid.setVisible(false);
        shootTileGrid.setVisible(false);
        ammoGrid.setDisable(true);
        playerGrid.setDisable(true);
        resetWeaponCratesStyle();

    }

    /**
     * The resetTileGridStyle method resets the tile grid's buttons to the player's color.
     *
     */
    public void resetTileGridStyle(GridPane gridPane) {
        gridPane.getChildren().stream()
                .map(n -> (AnchorPane) n) // gets the anchorpane
                .filter(ap -> !ap.getChildren().isEmpty())
                .map(ap -> (Button) ap.getChildren().get(0))
                .forEach(b -> {
                    b.setStyle("-fx-background-color: "+((GUIView) view).getBackgroundColor());
                    b.setDisable(false);
                    b.setVisible(true);
                });
    }

    /**
     * The resetAmmoGridStyle method resets the ammo grid's style.
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
                    ap.getChildren().get(0).setOpacity(1.0);
                });
    }

    /**
     * The resetPlayerGridStyle method resets the player grid's style.
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
     * The resetWeaponCratesStyle resets the weapons grids' style.
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
                                n.setDisable(true);
                                n.setOpacity(1.0);
                                if(!n.getStyleClass().isEmpty()) {
                                    n.getStyleClass().remove(0);
                                }
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
     * Getter for the progress bar that contains the circles representing the number
     * of players to be selected.
     *
     * @return the progress bar.
     */
    public GridPane getProgressBar() {
        return progressBar;
    }

    /**
     * Getter for the weaponCrateList.
     *
     * @return the list of grid panes that contain the ammo crates.
     */

    public List<GridPane> getWeaponCrateList() {
        return weaponCrateList;
    }

    /**
     * Getter for the shooTileGrid.
     *
     * @return the grid pane for shooting selections.
     */
    public GridPane getShootTileGrid() {
        return shootTileGrid;
    }

    /**
     * Getter for the ammoGrid.
     *
     * @return the grid pane containing the ammo crate images.
     */
    public GridPane getAmmoGrid() {
        return ammoGrid;
    }

    /**
     * Getter for the playerGrid.
     *
     * @return the grid pane containing the player's circles.
     */
    public GridPane getPlayerGrid() {
        return playerGrid;
    }
}
