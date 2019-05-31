package it.polimi.se2019.client.gui;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.client.util.Util;
import it.polimi.se2019.server.games.player.AmmoColor;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class PlayerBoardController {

    private static final Logger logger = Logger.getLogger(PlayerBoardController.class.getName());
    private MainApp mainApp;
    private PlayerColor playerColor;

    @FXML
    private AnchorPane main;
    @FXML
    private GridPane damageBar;
    @FXML
    private GridPane markerBar;
    @FXML
    private GridPane markerLabel;
    @FXML
    private AnchorPane skullBar;
    @FXML
    private AnchorPane actionTile;
    @FXML
    private Label blueAmmo;
    @FXML
    private Label redAmmo;
    @FXML
    private Label yellowAmmo;
    @FXML
    private Label nameLabel;

    /**
     * NamedImage extends the base Image class by adding a string type member
     * variable to store the name of the image.
     */
    private class NamedImage extends Image {

        private String name;

        public NamedImage(String url) {
            super(url);
            name = url.split(Constants.TOKEN_PATH)[1].split(".png")[0];
        }
        public String getName() {
            return name;
        }
    }

    /**
     * The player board initializer.
     */
    @FXML
    private void initialize() {
        // need to wait initialization of other parameters.
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
     * Initializes the marker bar.
     * @param color is the color of the client.
     */
    public void initMarkerPane(PlayerColor color) {

        int i = 0;
        for (PlayerColor pc : PlayerColor.values()) {
            if (pc != color) {
                ImageView iv = (ImageView) markerBar.getChildren().get(i);
                iv.setImage(getPlayerToken(pc));
                i++;
            }
        }
    }

    /**
     * Initializes the main player board.
     * @param player is the owner of this board.
     */
    @FXML
    public void showPlayerBoard(Player player) {
        ImageView iv = (ImageView) main.getChildren().get(0);

        iv.setImage(getPlayerBoardImage(player.getColor(), Util.getCorrectPlayerBoardMode(player)));

        if (Util.getCorrectPlayerBoardMode(player).equalsIgnoreCase(Constants.FRENZY)) {
            damageBar.setPrefWidth(315);
            damageBar.setLayoutX(47);
        }
    }


    /**
     * Shows the action tile
     * @param player is the owner of this action tile.
     */
    public void showActionTile(Player player) {
        ImageView iv = (ImageView) actionTile.getChildren().get(0);
        String gameMode = mainApp.getGame().isFrenzy() ? Constants.FRENZY : Constants.NORMAL;
        iv.setImage(new Image(Constants.ACTION_TILE+player.getColor()+gameMode+".png"));
    }

    /**
     * Shows the player's damage bar.
     */
    public void showDamageBar(Player player) {

        List<PlayerColor> damageBar = player.getCharacterState().getDamageBar();

        IntStream.range(0, damageBar.size())
                .forEach(i -> {
                    ImageView iv = (ImageView) this.damageBar.getChildren().get(i);
                    Image token = getPlayerToken(damageBar.get(i));
                    iv.setImage(token);
                });
    }

    /**
     * Shows the player's marker bar.
     */
    public void showMarkerBar(Player player) {

        Map<PlayerColor, Integer> markerbar = player.getCharacterState().getMarkerBar();

        int i = 0;
        for (PlayerColor pc : PlayerColor.values()) {
            if (pc != player.getColor()) {
                Label label = (Label) markerLabel.getChildren().get(i);
                label.setText(markerbar.get(pc).toString());
                i++;
            }
        }
    }

    /**
     * Shows the player's skull bar.
     */
    public void showSkullBar(Player player) {

        GridPane gridPane = (GridPane) skullBar.getChildren().get(0);

        try {
            if (Util.getCorrectPlayerBoardMode(player).equalsIgnoreCase(Constants.FRENZY)) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/SkullBarFrenzy.fxml"));
                AnchorPane pane = loader.load();

                gridPane = (GridPane) pane.getChildren().get(0);
                skullBar.getChildren().remove(0);
                skullBar.getChildren().add(gridPane);

            }
        } catch (IOException e) {
            logger.warning(e.toString());
        }

        showSkulls(player,gridPane);
    }

    /**
     * This is the actual method that sets the image in the image view.
     * @param player is the owner of the board
     * @param gridPane is the grid pane on which to add skulls.
     */
    public void showSkulls(Player player, GridPane gridPane) {
        IntStream.range(0, player.getCharacterState().getDeaths())
                .forEach(death -> {
                    Optional<ImageView> iv = gridPane.getChildren().stream()
                            .map(n -> (ImageView) n)
                            .filter(i -> i.getImage() == null)
                            .findFirst();
                    if (iv.isPresent()) {
                        iv.get().setImage(new Image(Constants.SKULL_PATH));
                    }
                });
    }

    /**
     * Resets the damage bar.
     */
    @FXML
    public void handleResetDamage() {

        for (Node n : damageBar.getChildren()) {
            ((ImageView) n).setImage(null);
        }
    }

    /**
     * Resets the marker bar.
     */
    @FXML
    public void handleResetMarker() {
        markerLabel.getChildren().stream()
                .forEach(n -> ((Label) n).setText("x0"));
    }

    /**
     * Adds the buttons to the action tile according to player's color and
     * game's current mode.
     * @param playerColor is the color of the player.
     * @param mode is the game's current mode.
     */
    public void addActionTileButtons(PlayerColor playerColor, String mode) {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/ActionTile" + mode + ".fxml"));
            AnchorPane buttonedPane = loader.load();
            ActionTileController atController = loader.getController();
            atController.setMainApp(mainApp);
            atController.init();

            ImageView iv = (ImageView) buttonedPane.getChildren().get(0);
            iv.setImage(new Image(Constants.ACTION_TILE +playerColor.getColor()+"_"+mode+".png"));
            actionTile.getChildren().add(buttonedPane);
        } catch (IOException e) {
            e.printStackTrace();
            logger.warning("Could not find resource.");
        }
    }

    /**
     * Swaps the main image and sets up the new skull bar for frenzy mode
     * @param playerColor is the player's color
     */
    public void handleFrenzyKill(PlayerColor playerColor) {
        ImageView iv = (ImageView) main.getChildren().get(0);
        iv.setImage(getPlayerBoardImage(playerColor, Constants.FRENZY));

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/SkullBarFrenzy.fxml"));
            AnchorPane pane = loader.load();

            skullBar.getChildren().remove(0);
            skullBar.getChildren().add(pane.getChildren().get(0));

        } catch (IOException e) {
            logger.warning("Could not find resource.");
        }

    }

    /**
     * Gets the correct color token to add in damage and/or marker bar.
     * @param color is the player color.
     * @return an image of the player color token.
     */
    public Image getPlayerToken(PlayerColor color) {
        String path = Constants.TOKEN_PATH + color.getColor().toLowerCase() + ".png";
        return new NamedImage(path);
    }

    /**
     * Returns the player board of a certain color.
     *
     */
    public Image getPlayerBoardImage(PlayerColor playerColor, String mode) {
        String path = Constants.BOARD_PATH + playerColor.getColor() + mode + ".png";
        return new Image(path);
    }

    public void setPlayerColor(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    public PlayerColor getPlayerColor() {
        return playerColor;
    }
}
