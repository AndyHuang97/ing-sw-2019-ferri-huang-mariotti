package it.polimi.se2019.client.gui;

import it.polimi.se2019.server.games.player.PlayerColor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class GameBoardController {

    private static final Logger logger = Logger.getLogger(GameBoardController.class.getName());
    private static final String NORMAL = "_Normal";
    private static final String FRENZY = "_Frenzy";
    private static final String PNG = ".png";

    private MainApp mainApp;
    private List<PlayerBoardController> playerList;

    @FXML
    private VBox leftVbox;
    @FXML
    private AnchorPane map;
    @FXML
    private AnchorPane playerBoard;
    @FXML
    private VBox opponents;

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
     * Initializes the map.
     */
    public void initMap() {

        try {
            FXMLLoader mloader = new FXMLLoader();
            mloader.setLocation(getClass().getResource("/fxml/Map.fxml"));
            AnchorPane decoratedMap = (AnchorPane) mloader.load();
            MapController mController = mloader.getController();
            mController.setMainApp(mainApp);

            // removes the old anchor and adds the new one
            leftVbox.getChildren().remove(0);
            leftVbox.getChildren().add(0, decoratedMap);
            mController.handleMapLoading();
        } catch (IOException e) {
            logger.warning("Error loading map.");
        }

    }

    /**
     * Initializes the players' boards.
     */
    public void initPlayerBoards(PlayerColor playerColor) {

        AnchorPane targetPane;
        int i = 0;
        for (PlayerColor pc : PlayerColor.values()) {
            try {
                FXMLLoader playerLoader = new FXMLLoader();
                playerLoader.setLocation(getClass().getResource("/fxml/PlayerBoard.fxml"));
                // gets the decorated pane
                AnchorPane decoratedPane = playerLoader.load();

                PlayerBoardController playerController = playerLoader.getController();
                playerController.setMainApp(mainApp);
                playerController.setPlayerColor(pc);
                playerController.initMarkerPane(pc);

                if (pc != playerColor) {
                    // gets the anchorPane containing the imageview
                    AnchorPane box = (AnchorPane) opponents.getChildren().get(i);
                    targetPane = (AnchorPane) box.getChildren().get(1);

                    i++;
                }else {
                    targetPane = playerBoard;
                    playerController.addActionTileButtons(playerColor, NORMAL.split("_")[1]);
                }
                // removes the static image view and loads the decorated one
                targetPane.getChildren().remove(0);
                targetPane.getChildren().add(decoratedPane);
                playerController.initPlayerBoard(pc);

            }
            catch (IOException e) {
                logger.warning("Error loading player boards");
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
     * Getter for the left Vbox of the game board.
     * @return left Vbox of the game board.
     */
    public VBox getLeftVbox() {
        return leftVbox;
    }

    /**
     * Gets the list of the player boards' controller.
     * @return list of player boards' controller
     */
    public List<PlayerBoardController> getPlayerList() {
        return playerList;
    }

    public PlayerBoardController getPlayerBoard(PlayerColor playerColor) {
        Optional<PlayerBoardController> optional = getPlayerList().stream()
                .filter(pc -> pc.getPlayerColor() == playerColor)
                .findFirst();

        return optional.orElse(null);
    }
}
