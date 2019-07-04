package it.polimi.se2019.client.gui;

import it.polimi.se2019.client.View;
import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.client.util.Util;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 * The PlayerBoardControler is the controller of the the player board. It provides methods to link a player's data to
 * its graphic counterpart.
 *
 * @author andreahuang
 */
public class PlayerBoardController {

    private static final Logger logger = Logger.getLogger(PlayerBoardController.class.getName());

    private View view;
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

    /**
     * The player board initializer.
     */
    @FXML
    private void initialize() {
        // need to wait initialization of other parameters.
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
     * Initializes the marker bar, it only loads the correct images.
     * @param color is the color of the client.
     */
    public void initMarkerPane(PlayerColor color) {

        int i = 0;
        for (PlayerColor pc : PlayerColor.values()) {
            if (pc != color) {
                ImageView iv = (ImageView) markerBar.getChildren().get(i);
                iv.setImage(Util.getPlayerToken(pc));
                i++;
            }
        }
    }

    /**
     * Initializes the main player board.
     * @param player is the owner of this board.
     */
    public void showPlayerBoard(Player player) {
        ImageView iv = (ImageView) main.getChildren().get(0);

        iv.setImage(getPlayerBoardImage(player.getColor(), Util.getCorrectPlayerBoardMode(player)));
        // in frenzy mode, the damage bar needs some resizing and repositioning
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
        String gameMode = view.getModel().getGame().isFrenzy() ? Constants.FRENZY : Constants.NORMAL;
        iv.setImage(new Image(Constants.ACTION_TILE+player.getColor().getColor()+gameMode+".png"));
    }

    /**
     * Shows the player's damage bar.
     */
    public void showDamageBar(Player player) {

        List<PlayerColor> damageBarModel = player.getCharacterState().getDamageBar();

        damageBar.getChildren().forEach(n -> n.setVisible(false));
        IntStream.range(0, damageBarModel.size())
                .forEach(i -> {
                    ImageView iv = (ImageView) this.damageBar.getChildren().get(i);
                    Image token = Util.getPlayerToken(damageBarModel.get(i));
                    iv.setImage(token);
                    iv.setVisible(true);
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
     *
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
     * Returns the player board of a certain color.
     *
     */
    public Image getPlayerBoardImage(PlayerColor playerColor, String mode) {
        String path = Constants.BOARD_PATH + playerColor.getColor() + mode + ".png";
        return new Image(path);
    }

    /**
     * Setter for playerColor.
     * @param playerColor the playerColor of this client.
     */
    public void setPlayerColor(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    /**
     * Getter for playerColor.
     * @return the playerColor of this client.
     */
    public PlayerColor getPlayerColor() {
        return playerColor;
    }
}
