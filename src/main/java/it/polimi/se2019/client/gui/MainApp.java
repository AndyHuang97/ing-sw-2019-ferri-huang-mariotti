package it.polimi.se2019.client.gui;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.se2019.client.net.CommandHandler;
import it.polimi.se2019.client.net.RmiClient;
import it.polimi.se2019.client.net.SocketClient;
import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.cards.ammocrate.AmmoCrate;
import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.deserialize.BoardDeserializer;
import it.polimi.se2019.server.deserialize.DynamicDeserializerFactory;
import it.polimi.se2019.server.deserialize.TileDeserializerSupplier;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.KillShotTrack;
import it.polimi.se2019.server.games.board.Board;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.AmmoColor;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.server.users.UserData;
import it.polimi.se2019.util.Request;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 * This is the main class of the GUI.
 */
public class MainApp extends Application {

    private static final Logger logger = Logger.getLogger(MainApp.class.getName());

    private Map<String, List<String>> playerInput = new HashMap<>();
    private List<Runnable> inputRequested = new ArrayList<>();

    private Game game;
    private PlayerColor playerColor;
    private String nickname;
    private int actionNumber;

    private Stage primaryStage;
    private LoginController loginController;
    private GameBoardController gameBoardController;
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

        //showLogin();

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
            scene.getStylesheets().add("/css/root.css");
            primaryStage.setScene(scene);

            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);

        } catch(IOException e) {
            logger.warning(e.toString());
        }
    }

    public void showGameBoard() {

        try{
            FXMLLoader gbLoader = new FXMLLoader();
            gbLoader.setLocation(MainApp.class.getResource("/fxml/GameBoard.fxml"));
            AnchorPane gameBoard = (AnchorPane) gbLoader.load();
            GameBoardController gbController = gbLoader.getController();
            gbController.setMainApp(this);
            setGameBoardController(gbController);

            // Set the scene containing the root layout
            rootlayout.setCenter(gameBoard);

            // initialization of the map must precede the initialization of the player boards

            gbController.init(game.getPlayerByColor(playerColor));

        } catch(IOException e) {
            logger.warning(e.toString());
        }
    }

    public void showLogin() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/Login.fxml"));
            AnchorPane login = loader.load();
            LoginController controller = loader.getController();
            controller.setMainApp(this);
            this.setLoginController(controller);


            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            controller.setLoginStage(loginStage);

            Scene scene = new Scene(login);
            loginStage.setScene(scene);
            loginStage.initOwner(primaryStage);
            loginStage.showAndWait();

        } catch (IOException e) {
            logger.warning(e.toString());
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
                SocketClient client = new SocketClient(nickname, ip);
                client.start();
                // starting thread that redraws stuffs
                client.send(new Request(nickname).serialize());
                Platform.runLater(new CommandHandler(client.getIn(), this));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + connectionType);
        }
    }

    public LoginController getLoginController() {
        return loginController;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    public void connectSocket(String ip, int port) {

    }

    public void connectRMI(String ip, int port) {

    }

    public void getInput() {
        if (!getInputRequested().isEmpty()) {
            getInputRequested().remove(0).run();
        }
    }

    /**
     * Get the next type of input.
     */
    public void nextInput() {

    }

    public void addInput(String key, String id) {
        getPlayerInput().putIfAbsent(key, new ArrayList<>());
        getPlayerInput().get(key).add(id);
        System.out.println("Added: " + key + " " + id);
    }

    /**
     * Sends input via network.
     */
    public void sendInput(){
        //TODO send input via network
        // ...
        System.out.println(">>> Sending: " + getPlayerInput());
        getPlayerInput().clear();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    /**
     * Setter of the player color.
     * @param playerColor is the player's color.
     */
    public void setPlayerColor(PlayerColor playerColor) {
        this.playerColor = playerColor;
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

    public Map<String, List<String>> getPlayerInput() {
        return playerInput;
    }

    public void setPlayerInput(Map<String, List<String>> playerInput) {
        this.playerInput = playerInput;
    }

    public int getActionNumber() {
        return actionNumber;
    }

    public void setActionNumber(int actionNumber) {
        this.actionNumber = actionNumber;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void initGame() {
        game = new Game();
        game.setFrenzy(true);
        boardDeserialize();

        Player p1 = new Player(UUID.randomUUID().toString(), true, new UserData("Giorno"), new CharacterState(), PlayerColor.GREEN);
        p1.getCharacterState().setTile(game.getBoard().getTile(0,0));
        Player p2 = new Player(UUID.randomUUID().toString(), true, new UserData("Mista"), new CharacterState(), PlayerColor.BLUE);
        p2.getCharacterState().setTile(game.getBoard().getTile(0,0));
        Player p3 = new Player(UUID.randomUUID().toString(), true, new UserData("Narancia"), new CharacterState(), PlayerColor.YELLOW);
        p3.getCharacterState().setTile(game.getBoard().getTile(1,1));
        Player p4 = new Player(UUID.randomUUID().toString(), true, new UserData("Bucciarati"), new CharacterState(), PlayerColor.GREY);
        p4.getCharacterState().setTile(game.getBoard().getTile(2,0));
        Player p5 = new Player(UUID.randomUUID().toString(), true, new UserData("Abbacchio"), new CharacterState(), PlayerColor.PURPLE);
        p5.getCharacterState().setTile(game.getBoard().getTile(2,2));
        game.setPlayerList(Arrays.asList(p1,p2,p3,p4));
        game.setCurrentPlayer(p1);
        Weapon w1 = new Weapon(null, "0216", null
                , null, null);
        w1.setLoaded(true);
        Weapon w2 = new Weapon(null, "0217", null
                , null, null);
        w2.setLoaded(true);
        Weapon w3 = new Weapon(null, "0218", null
                , null, null);
        w3.setLoaded(true);
        Weapon w4 = new Weapon(null, "0219", null
                , null, null);
        w4.setLoaded(false);
        p1.getCharacterState().setWeapoonBag(Arrays.asList(w1,w2,w3));
        p2.getCharacterState().setWeapoonBag(Arrays.asList(w4,w2,w3));
        p3.getCharacterState().setWeapoonBag(Arrays.asList(w1,w4,w3));
        p4.getCharacterState().setWeapoonBag(Arrays.asList(w1,w2,w4));
        p5.getCharacterState().setWeapoonBag(Arrays.asList(w1,w2,w4));

        p1.getCharacterState().setPowerUpBag(Arrays.asList(
                new PowerUp(null, "026"),
                new PowerUp(null, "027"),
                new PowerUp(null, "028")));

        p1.getCharacterState().getDamageBar().addAll(Arrays.asList(PlayerColor.BLUE,PlayerColor.BLUE,PlayerColor.BLUE));
        p2.getCharacterState().getDamageBar().addAll(Arrays.asList(PlayerColor.YELLOW,PlayerColor.BLUE,PlayerColor.BLUE));
        p3.getCharacterState().getDamageBar().addAll(Arrays.asList(PlayerColor.BLUE,PlayerColor.YELLOW,PlayerColor.BLUE));
        p4.getCharacterState().getDamageBar().addAll(Arrays.asList(PlayerColor.BLUE,PlayerColor.BLUE,PlayerColor.YELLOW));
        p5.getCharacterState().getDamageBar().addAll(Arrays.asList(PlayerColor.BLUE,PlayerColor.GREEN,PlayerColor.BLUE));

        p1.getCharacterState().getMarkerBar().put(PlayerColor.BLUE, 3);
        p1.getCharacterState().getMarkerBar().put(PlayerColor.YELLOW, 2);
        p2.getCharacterState().getMarkerBar().put(PlayerColor.GREY, 3);
        p2.getCharacterState().getMarkerBar().put(PlayerColor.YELLOW, 2);
        p3.getCharacterState().getMarkerBar().put(PlayerColor.PURPLE, 1);
        p3.getCharacterState().getMarkerBar().put(PlayerColor.GREEN, 2);
        p4.getCharacterState().getMarkerBar().put(PlayerColor.BLUE, 3);
        p4.getCharacterState().getMarkerBar().put(PlayerColor.YELLOW, 2);
        p5.getCharacterState().getMarkerBar().put(PlayerColor.BLUE, 3);
        p5.getCharacterState().getMarkerBar().put(PlayerColor.GREY, 2);
        p5.getCharacterState().getMarkerBar().put(PlayerColor.YELLOW, 2);
        p5.getCharacterState().getMarkerBar().put(PlayerColor.GREEN, 1);

        p1.getCharacterState().setDeaths(1);
        p2.getCharacterState().setDeaths(5);
        p3.getCharacterState().setDeaths(3);
        p4.getCharacterState().setDeaths(4);
        p5.getCharacterState().setDeaths(5);

        p1.getCharacterState().setValueBar(CharacterState.NORMAL_VALUE_BAR);
        p2.getCharacterState().setValueBar(CharacterState.FRENZY_VALUE_BAR);
        p3.getCharacterState().setValueBar(CharacterState.NORMAL_VALUE_BAR);
        p4.getCharacterState().setValueBar(CharacterState.NORMAL_VALUE_BAR);
        p5.getCharacterState().setValueBar(CharacterState.NORMAL_VALUE_BAR);

        EnumMap<AmmoColor, Integer> ammoMap = new EnumMap<>(AmmoColor.class);
        ammoMap.putIfAbsent(AmmoColor.BLUE, 3);
        ammoMap.putIfAbsent(AmmoColor.RED, 2);
        ammoMap.putIfAbsent(AmmoColor.YELLOW, 3);

        p1.getCharacterState().setAmmoBag(ammoMap);
        p2.getCharacterState().setAmmoBag(ammoMap);
        p3.getCharacterState().setAmmoBag(ammoMap);
        p4.getCharacterState().setAmmoBag(ammoMap);
        p5.getCharacterState().setAmmoBag(ammoMap);

        p1.getCharacterState().setScore(6);
        p2.getCharacterState().setScore(2);
        p3.getCharacterState().setScore(3);
        p4.getCharacterState().setScore(4);
        p5.getCharacterState().setScore(5);

        KillShotTrack kt = new KillShotTrack(game.getPlayerList());
        kt.addDeath(p1, false);
        kt.addDeath(p2, true);
        kt.addDeath(p3, true);
        kt.addDeath(p4, true);
        kt.addDeath(p5, true);
        kt.addDeath(p1, false);
        kt.addDeath(p2, true);
        kt.addDeath(p3, true);
        kt.addDeath(p4, true);
        kt.addDeath(p5, true);
        game.setKillshotTrack(kt);

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
                                    if (!tileMap[x][y].isSpawnTile()) {
                                        tileMap[x][y].setAmmoCrate(new AmmoCrate(null, "042"));
                                    }
                                    else {
                                        tileMap[x][y].setWeaponCrate(
                                                Arrays.asList(
                                                        new Weapon(null, "026", null
                                                                , null, null),
                                                        new Weapon(null, "027", null
                                                                , null, null),
                                                        new Weapon(null, "028", null
                                                                , null, null)));
                                    }
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


    public List<Runnable> getInputRequested() {
        return inputRequested;
    }

    public void setInputRequested(List<Runnable> inputRequested) {
        this.inputRequested = inputRequested;
    }

    public GameBoardController getGameBoardController() {
        return gameBoardController;
    }

    public void setGameBoardController(GameBoardController gameBoardController) {
        this.gameBoardController = gameBoardController;
    }
}
