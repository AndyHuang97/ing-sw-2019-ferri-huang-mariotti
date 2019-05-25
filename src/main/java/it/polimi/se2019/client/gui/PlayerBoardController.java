package it.polimi.se2019.client.gui;

import it.polimi.se2019.server.games.player.PlayerColor;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.util.Optional;
import java.util.logging.Logger;

public class PlayerBoardController {

    private static final Logger logger = Logger.getLogger(PlayerBoardController.class.getName());
    private static final String TOKEN_PATH = "/images/tokens/";
    private static final String PNG = ".png";
    private static final String SKULL_PATH = "/images/redSkull.png";

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

    @FXML
    private void initialize() {
        initMarkerPane(PlayerColor.GREY);
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
                .filter(i -> ((NamedImage)i.getImage()).getName().toLowerCase().equals(color.toLowerCase()))
                .findAny();

        if (node.isPresent()) {
            //logger.info("IS PRESENT");
            ImageView iv = node.get();
            int index = markerToken.getChildren().indexOf(iv);
            Label label = (Label) markerLabel.getChildren().get(index);
            int base = Integer.parseInt(label.getText().split("x")[1]);
            int updated = base + amount;
            label.setText("x" + String.valueOf(updated));
        }
    }

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

    public void resize(Node n) {

    }
}
