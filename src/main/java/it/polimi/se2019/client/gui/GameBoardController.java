package it.polimi.se2019.client.gui;

import it.polimi.se2019.server.games.player.PlayerColor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class GameBoardController {

    private static final String IMAGE_PATH = "/images/playerBoards/PlayerBoard_";
    private static final String NORMAL = "_Normal";
    private static final String FRENZY = "_Frenzy";
    private static final String PNG = ".png";


    @FXML
    private AnchorPane map;
    @FXML
    private AnchorPane playerBoard;
    @FXML
    private VBox opponents;

    private MainApp mainApp;

    /**
     * The main game board initializer.
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
     * Initializes the players' boards.
     */
    public void initPlayerBoards(PlayerColor playerColor) {

        setUpPlayerBoard(playerColor);
        setUpOpponentsBoard(playerColor);

    }

    /**
     * Sets up the player's board according to his/her color.
     * @param playerColor the player's color.
     */
    public void setUpPlayerBoard(PlayerColor playerColor) {
        // loading player board
        try {
            FXMLLoader playerLoader = new FXMLLoader();
            playerLoader.setLocation(getClass().getResource("/fxml/PlayerBoard.fxml"));
            AnchorPane correctBoard = playerLoader.load();

            PlayerBoardController playerController = playerLoader.getController();
            playerController.initMarkerPane(playerColor);
            playerController.addActionTileButtons(playerColor, NORMAL.split("_")[1]);

            playerBoard.getChildren().add(correctBoard);
            ImageView playerIV = (ImageView) playerBoard.getChildren().get(0);
            playerIV.setImage(getPlayerBoardImage(playerColor, NORMAL));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets up the opponents' boards.
     * @param playerColor the player's(client) color.
     */
    public void setUpOpponentsBoard(PlayerColor playerColor) {
        // loading opponents' boards
        int i = 0;
        for (PlayerColor pc : PlayerColor.values()) {
            try {
                if (pc != playerColor) {
                    FXMLLoader opponentLoader = new FXMLLoader();
                    opponentLoader.setLocation(getClass().getResource("/fxml/PlayerBoard.fxml"));
                    // gets the anchorPane containing the imageview
                    AnchorPane box = (AnchorPane) opponents.getChildren().get(i);
                    AnchorPane opponentPane = (AnchorPane) box.getChildren().get(1);
                    // gets the decorated pane
                    AnchorPane correctPane = (AnchorPane) opponentLoader.load();

                    PlayerBoardController pbController = opponentLoader.getController();
                    pbController.initMarkerPane(pc);

                    opponentPane.getChildren().add(correctPane);
                    ImageView iv = (ImageView) opponentPane.getChildren().get(0);
                    iv.setImage(getPlayerBoardImage(pc, NORMAL));
                    i++;
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Getter for the player's board.
     * @return player board the player.
     */
    public AnchorPane getPlayerBoard() {
        return playerBoard;
    }

    /**
     * Setter for the player's board.
     * @param playerBoard is the new player's board.
     */
    public void setPlayerBoard(AnchorPane playerBoard) {
        this.playerBoard = playerBoard;
    }

    /**
     * Getter for the map of the game.
     * @return the map of the game.
     */
    public AnchorPane getMap() {
        return map;
    }

    /**
     * Setter for the map of the game.
     * @param map is the new map of the game.
     */
    public void setMap(AnchorPane map) {
        this.map = map;
    }

    /**
     * Returns the player board of a certain color.
     *
     */
    public Image getPlayerBoardImage(PlayerColor playerColor, String mode) {
        String path = IMAGE_PATH + playerColor.getColor() + mode + PNG;
        return new Image(path);
    }
}
