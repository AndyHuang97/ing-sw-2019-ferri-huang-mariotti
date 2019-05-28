package it.polimi.se2019.client.gui;

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

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

public class PlayerBoardController {

    private static final Logger logger = Logger.getLogger(PlayerBoardController.class.getName());
    private static final String TOKEN_PATH = "/images/tokens/";
    private static final String SKULL_PATH = "/images/redSkull.png";
    private static final String PNG = ".png";
    private static final String BOARD_PATH = "/images/playerBoards/PlayerBoard_";
    private static final String NORMAL = "_Normal";
    private static final String FRENZY = "_Frenzy";
    private MainApp mainApp;
    private PlayerColor playerColor;

    @FXML
    private AnchorPane main;
    @FXML
    private TextField pColor;
    @FXML
    private TextField damageAmount;
    @FXML
    private GridPane damagePane;
    @FXML
    private GridPane markerToken;
    @FXML
    private GridPane markerLabel;
    @FXML
    private AnchorPane skullPane;
    @FXML
    private AnchorPane actionTile;

    /**
     * NamedImage extends the base Image class by adding a string type member
     * variable to store the name of the image.
     */
    private class NamedImage extends Image {

        private String name;

        public NamedImage(String url) {
            super(url);
            name = url.split(TOKEN_PATH)[1].split(PNG)[0];
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
                ImageView iv = (ImageView) markerToken.getChildren().get(i);
                iv.setImage(getPlayerToken(pc));
                i++;
            }
        }
    }

    /**
     * Initializes the main player board.
     * @param playerColor is the player's color.
     */
    @FXML
    public void initPlayerBoard(PlayerColor playerColor) {
        ImageView iv = (ImageView) main.getChildren().get(0);
        iv.setImage(getPlayerBoardImage(playerColor, NORMAL));
    }

    /**
     * Adds damage from attacker player to damage bar.
     */
    @FXML
    public void handleDamage() {

        String color = getpColor();
        int amount = getDamageAmount();

        int i = 0;
        try {
            for (Node n : damagePane.getChildren()) {

                if (((ImageView) n).getImage() == null && i < amount) {
                    Image token = getPlayerToken(PlayerColor.valueOf(color.toUpperCase()));
                    ((ImageView) n).setImage(token);

                    i++;
                }
            }
        } catch (Exception e) {
            logger.warning("Invalid Color.");
        }
    }

    /**
     * Adds markers of attacker color.
     */
    @FXML
    public void handleMarker() {

        String color = getpColor();
        int amount = getDamageAmount();

        Optional<ImageView> node = markerToken.getChildren().stream()
                .map(n -> (ImageView) n)
                .filter(i -> ((NamedImage)i.getImage()).getName().equalsIgnoreCase(color))
                .findAny();

        if (node.isPresent()) {
            ImageView iv = node.get();
            int index = markerToken.getChildren().indexOf(iv);
            Label label = (Label) markerLabel.getChildren().get(index);
            int base = Integer.parseInt(label.getText().split("x")[1]);
            int updated = base + amount;
            label.setText("x" + updated);
        }
    }

    /**
     * Adds skulls to the skull bar.
     */
    @FXML
    public void handleSkull() {
        GridPane gridPane = (GridPane) skullPane.getChildren().get(0);
        Optional<ImageView> iv = gridPane.getChildren().stream()
                .map(n -> (ImageView) n)
                .filter(i -> i.getImage() == null)
                .findFirst();

        if (iv.isPresent()) {
            iv.get().setImage(new Image(SKULL_PATH));
        }
    }

    /**
     * Resets the damage bar.
     */
    @FXML
    public void handleResetDamage() {

        for (Node n : damagePane.getChildren()) {
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
            atController.initParams();

            ImageView iv = (ImageView) buttonedPane.getChildren().get(0);
            iv.setImage(new Image("/images/playerBoards/ActionTile_"+mainApp.getPlayerColor().getColor()+"_"+mode+".png"));
            actionTile.getChildren().add(buttonedPane);
        } catch (IOException e) {
            logger.warning("Could not find resource.");
        }
    }

    /**
     * Swaps the main image and sets up the new skull bar for frenzy mode
     * @param playerColor is the players' color
     */
    public void handleFrenzyKill(PlayerColor playerColor) {
        ImageView iv = (ImageView) main.getChildren().get(0);
        iv.setImage(getPlayerBoardImage(playerColor, FRENZY));

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/SkullBarFrenzy.fxml"));
            AnchorPane pane = loader.load();

            skullPane.getChildren().remove(0);
            skullPane.getChildren().add(pane.getChildren().get(0));

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
        String path = TOKEN_PATH + color.getColor().toLowerCase() + PNG;
        return new NamedImage(path);
    }

    /**
     * Gets attacker's color.
     * @return attacker's color.
     */
    public String getpColor() {
        return pColor.getText();
    }

    /**
     * Gets the number of tokens to add to marker/damage bar.
     * @return
     */
    public int getDamageAmount() {
        return Integer.parseInt(damageAmount.getText());
    }

    /**
     * Returns the player board of a certain color.
     *
     */
    public Image getPlayerBoardImage(PlayerColor playerColor, String mode) {
        String path = BOARD_PATH + playerColor.getColor() + mode + PNG;
        return new Image(path);
    }

    public void setPlayerColor(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    public PlayerColor getPlayerColor() {
        return playerColor;
    }
}
