package it.polimi.se2019.client.gui;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.se2019.client.net.RmiClient;
import it.polimi.se2019.client.net.SocketClient;
import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.cards.ammocrate.AmmoCrate;
import it.polimi.se2019.server.deserialize.BoardDeserializer;
import it.polimi.se2019.server.deserialize.DynamicDeserializerFactory;
import it.polimi.se2019.server.deserialize.TileDeserializerSupplier;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.board.Board;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.server.users.UserData;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 * This is the main class of the GUI.
 */
public class MainApp extends Application {

    private static final Logger logger = Logger.getLogger(MainApp.class.getName());

    private Map<String, Integer> playerInput;
    private Game game;
    private PlayerColor playerColor;
    private int actionNumber;

    private Stage primaryStage;
    private BorderPane rootlayout;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        setPlayerColor(PlayerColor.GREEN);
        // TODO the game should be deserialized from the network, and should be already completely initialized
        initGame();

        actionNumber = 1;
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Adrenaline");

        showLogin();
        initRootLayout();
        showGameBoard();

        primaryStage.setResizable(false);
        primaryStage.setFullScreen(true);
        primaryStage.sizeToScene();
        primaryStage.show();

    }

    public void initRootLayout() {
        try{
            // Load root layout from fxml file
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/RootLayout.fxml"));
            rootlayout = (BorderPane) loader.load();

            // Set the scene containing the root layout
            Scene scene = new Scene(rootlayout);
            primaryStage.setScene(scene);

            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);

        } catch(IOException e) {
            logger.warning("Could not find resource.");
        }
    }

    public void showGameBoard() {

        try{
            FXMLLoader gbLoader = new FXMLLoader();
            gbLoader.setLocation(MainApp.class.getResource("/fxml/GameBoard.fxml"));
            AnchorPane gameBoard = (AnchorPane) gbLoader.load();
            GameBoardController gbController = gbLoader.getController();
            gbController.setMainApp(this);

            // Set the scene containing the root layout
            rootlayout.setCenter(gameBoard);

            // initialization of the map must precede the initialization of the player boards
            gbController.initMap();
            gbController.initPlayerBoards(playerColor);

        } catch(IOException e) {
            logger.warning("Could not find resource.");
        }
    }

    public void showLogin() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/Login.fxml"));
            AnchorPane login = loader.load();
            LoginController controller = loader.getController();
            controller.setMainApp(this);

            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            controller.setLoginStage(loginStage);

            Scene scene = new Scene(login);
            loginStage.setScene(scene);
            loginStage.showAndWait();

        } catch (IOException e) {
            logger.warning("Login window loading error.");
            e.printStackTrace();
        }
    }

    public void connect(String nickname, String ip, String connectionType) {
        switch (connectionType) {
            case Constants.RMI:
                // connect via rmi
                new RmiClient(nickname, ip);
                break;
            case Constants.SOCKET:
                // connect via socket
                new SocketClient(nickname, ip);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + connectionType);
        }
    }

    public void connectSocket(String ip, int port) {

    }

    public void connectRMI(String ip, int port) {

    }

    @FXML
    public void getInput(int x, int y) {

    }

    /**
     * Sends input via network.
     */
    public void sendInput(){

    }

    /**
     * Getter of the primary stage.
     * @return the primary stage.
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Getter of the player color.
     * @return the player color.
     */
    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    public String getBackgroundColor() {
        switch (playerColor) {
            case BLUE:
                return "teal";
            case YELLOW:
                return "yellow";
            case GREEN:
                return "green";
            case GREY:
                return "grey";
            case PURPLE:
                return "purple";
            default:
                return "";
        }
    }

    /**
     * Setter of the player color.
     * @param playerColor is the player's color.
     */
    public void setPlayerColor(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    public Map<String, Integer> getPlayerInput() {
        return playerInput;
    }

    public int getActionNumber() {
        return actionNumber;
    }

    public void setActionNumber(int actionNumber) {
        this.actionNumber = actionNumber;
    }

    public void initGame() {
        game = new Game();
        boardDeserialize();

        Player p1 = new Player(UUID.randomUUID().toString(), true, new UserData("A"), new CharacterState(), PlayerColor.GREEN);
        p1.getCharacterState().setTile(game.getBoard().getTile(0,0));
        Player p2 = new Player(UUID.randomUUID().toString(), true, new UserData("B"), new CharacterState(), PlayerColor.BLUE);
        p2.getCharacterState().setTile(game.getBoard().getTile(1,1));
        Player p3 = new Player(UUID.randomUUID().toString(), true, new UserData("C"), new CharacterState(), PlayerColor.YELLOW);
        p3.getCharacterState().setTile(game.getBoard().getTile(1,1));
        Player p4 = new Player(UUID.randomUUID().toString(), true, new UserData("D"), new CharacterState(), PlayerColor.GREY);
        p4.getCharacterState().setTile(game.getBoard().getTile(0,1));
        Player p5 = new Player(UUID.randomUUID().toString(), true, new UserData("E"), new CharacterState(), PlayerColor.PURPLE);
        p5.getCharacterState().setTile(game.getBoard().getTile(3,2));
        game.setPlayerList(Arrays.asList(p1,p2,p3,p4,p5));
        game.setCurrentPlayer(p1);

    }

    public void boardDeserialize() {
        DynamicDeserializerFactory factory = new DynamicDeserializerFactory();
        BoardDeserializer boardDeserializer = new BoardDeserializer();
        factory.registerDeserializer("tile", new TileDeserializerSupplier());

        String path = "src/main/resources/json/maps/map0.json";
        BufferedReader bufferedReader;

        Board board = null;

        try {
            bufferedReader = new BufferedReader(new FileReader(path));
            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(bufferedReader).getAsJsonObject();

            game.setBoard(boardDeserializer.deserialize(json, factory));

            Tile[][] tileMap = game.getBoard().getTileMap();
            IntStream.range(0, tileMap[0].length)
                    .forEach(y -> IntStream.range(0, tileMap.length)
                            .forEach(x -> {
                                if (tileMap[x][y] != null) {
                                    tileMap[x][y].setAmmoCrate(new AmmoCrate(null, "042"));
                                }
                            }));


            try {
                bufferedReader.close();
            } catch (IOException e) {
                logger.warning("Error on file close");
            }

        } catch (FileNotFoundException e) {
            logger.warning("File not found");
        } catch (ClassNotFoundException e) {
            logger.warning("Class not found");
        }
    }

    public Game getGame() {
        return game;
    }
}
