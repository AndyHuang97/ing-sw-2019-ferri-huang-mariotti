package it.polimi.se2019.client.gui;

import it.polimi.se2019.server.games.player.PlayerColor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class PlayerBoardController {

    private static final Logger logger = Logger.getLogger(PlayerBoardController.class.getName());
    private static final String TOKEN_PATH = "/images/tokens/";
    private static final String SKULL_PATH = "/images/redSkull.png";
    private static final String PNG = ".png";
    private static final String ACTION_PATH = "/images/playerBoards/ActionTile_";
    private static final String NORMAL = "_Normal";
    private static final String FRENZY = "_Frenzy";

    @FXML
    private TextField playerColor;
    @FXML
    private TextField damageAmount;
    @FXML
    private GridPane damagePane;
    @FXML
    private GridPane markerToken;
    @FXML
    private GridPane markerLabel;
    @FXML
    private GridPane skullPane;
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

    private MainApp mainApp;

    /**
     * The player board initializer.
     */
    @FXML
    private void initialize() {

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
     * Adds damage from attacker player to damage bar.
     */
    @FXML
    public void handleDamage() {

        String color = getPlayerColor();
        int amount = getDamageAmount();
        //logger.info(color + " - " + amount);

        int i = 0;
        try {
            for (Node n : damagePane.getChildren()) {
                //logger.info(i + "," + amount);

                if (((ImageView) n).getImage() == null && i < amount) {
                    //logger.info("EMPTY IMAGE");

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

        String color = getPlayerColor();
        int amount = getDamageAmount();
        //logger.info(color + " - " + amount);

        Optional<ImageView> node = markerToken.getChildren().stream()
                .map(n -> (ImageView) n)
                .filter(i -> ((NamedImage)i.getImage()).getName().equalsIgnoreCase(color))
                .findAny();

        if (node.isPresent()) {
            //logger.info("IS PRESENT");
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

        Optional<ImageView> iv = skullPane.getChildren().stream()
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
     * Swaps the action tile with the frenzy mode's tile when in frenzy mode.
     */
    @FXML
    public void handleActionTileFrenzy() {

    }

    public void addActionTileButtons(PlayerColor playerColor, String mode) {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/ActionTile" + mode + ".fxml"));
            AnchorPane buttonedPane = loader.load();

            actionTile.getChildren().add(buttonedPane);
        } catch (IOException e) {
            e.printStackTrace();
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
    public String getPlayerColor() {
        return playerColor.getText();
    }

    /**
     * Gets the number of tokens to add to marker/damage bar.
     * @return
     */
    public int getDamageAmount() {
        return Integer.parseInt(damageAmount.getText());
    }

}
