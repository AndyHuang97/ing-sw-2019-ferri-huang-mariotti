package it.polimi.se2019.client;

import it.polimi.se2019.client.net.NetworkClient;
import it.polimi.se2019.client.net.RmiClient;
import it.polimi.se2019.client.net.SocketClient;
import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.dataupdate.StateUpdate;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.util.*;
import it.polimi.se2019.util.Observable;
import it.polimi.se2019.util.Observer;

import java.util.*;
import java.util.logging.Logger;

/**
 * The View abstract class provides reception of response message from the server and also send request messages
 * to the server
 *
 * @author andreahuang
 */
public abstract class View extends Observable implements Observer<Response> {

    private LocalModel model;
    private String nickname;
    private PlayerColor playerColor;
    private NetworkClient networkClient;

    private Map<String, List<String>> playerInput = new HashMap<>();
    private List<Runnable> inputRequested = new ArrayList<>();

    private boolean cliTrueGuiFalse;

    public View() {
        model = new Model();
    }

    /**
     * The update method is called for the local model updates contained in the response message.
     *
     * @param response the response containing the updates of the model.
     */
    @Override
    public void update(Response response) {
        List<StateUpdate> updateList = response.getUpdateData();

        for (StateUpdate stateUpdate : updateList) {
            stateUpdate.updateData(model);
        }
    }

    /**
     * The connect method initializes a connection to the server sending its nickname and a map preference.
     *
     * @param nickname is the client's nickname.
     * @param ip is the server's ip address.
     * @param connectionType is the connection type, either RMI or socket.
     */
    public void connect(String nickname, String ip, String connectionType, String map) {

        switch (connectionType) {
            case Constants.RMI:
                // connect via rmi
                networkClient = new RmiClient(nickname, ip);
                break;
            case Constants.SOCKET:
                // connect via socket
                networkClient = new SocketClient(nickname, ip);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + connectionType);
        }

        networkClient.start(this);
        Map<String, List<String>> payload = new HashMap<>();
        payload.put("connect", Arrays.asList(map));
        networkClient.send(new Request(new NetMessage(payload), nickname));
    }

    /**
     * The sendInput method sends an input message to the server, only if the client is the current player of
     * the match.
     *
     */
    public void sendInput() {
        System.out.println(">>> Sending: " + getPlayerInput());
        if (model.getGame().getCurrentPlayer().getUserData().getNickname().equals(nickname)) {
//            System.out.println("WARNING: Commented send for gui local testing, go to View.sendInput and comment it");
            networkClient.send(new Request(new NetMessage(playerInput), nickname));
        } else {
            System.out.println("Wait for your turn!");
        }
        getPlayerInput().clear();
    }

    /**
     * The pong answers to a ping message
     *
     */
    public void pong() {
        Map<String, List<String>> payload = new HashMap<>();
        payload.put("pong", new ArrayList<>());
        networkClient.send(new Request(new NetMessage(payload), nickname));
    }

    /**
     * The askInput method runs a runnable function from the input requested list.
     */
    public abstract void askInput();

    /**
     * The showMessage method shows a message received as response from the server, it may execute some methods
     * for input selection.
     * @param message a response message containing info on the performed action.
     */
    public abstract void showMessage(String message);

    /**
     * The reportError method shows an error message from the server when an invalid action is performed.
     * @param error an error message conataining info about an action's violations.
     */
    public abstract  void reportError(String error);

    /**
     * The showGame method displays all necessary elements of the match to start playing.
     */
    public abstract void showGame();

    /**
     * The setGame method sets the game for the local model.
     * @param game is the game received from the server.
     */
    public void setGame(Game game) {
        model.setGame(game);
    }

    /**
     * Getter for model.
     *
     * @return the current model.
     */
    public LocalModel getModel() {
        return model;
    }

    /**
     * Getter for the client's nickname.
     *
     * @return the nickname of the player
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Setter for the client's nickname.
     *
     * @param nickname the new nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Getter for the client's playerColor.
     *
     * @return the client's playerColor.
     */
    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    /**
     * Setter for the client's playerColor.
     * @param playerColor the client's playerColor.
     */
    public void setPlayerColor(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    /**
     * Getter for playerInput.
     *
     * @return the playerInput.
     */
    public Map<String, List<String>> getPlayerInput() {
        return playerInput;
    }

    /**
     * Getter for inputRequested.
     *
     * @return the requested input.
     */
    public List<Runnable> getInputRequested() {
        return inputRequested;
    }

    /**
     * Checks whether the view is connected via cli or gui.
     *
     * @return true if cli, false if gui.
     */
    public boolean isCliTrueGuiFalse() { return cliTrueGuiFalse; }

    /**
     * Setter for the boolean that indicates the connection type.
     *
     * @param cliTrueGuiFalse the type of the connection.
     */
    public void setCliTrueGuiFalse(boolean cliTrueGuiFalse) { this.cliTrueGuiFalse = cliTrueGuiFalse; }
}
