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

    @Override
    public void update(Response response) {
        List<StateUpdate> updateList = response.getUpdateData();

        for (StateUpdate stateUpdate : updateList) {
            stateUpdate.updateData(model);
        }
    }

    /**
     * The connect method initializes a connection to the server sending its nickname.
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
     * The sendInput method sends an input message to the server.
     */
    public void sendInput() {
        System.out.println(">>> Sending: " + getPlayerInput());
        networkClient.send(new Request(new NetMessage(playerInput), nickname));
        getPlayerInput().clear();
    }

    /**
     * The askInput method runs a runnable function from the input requested list.
     */
    public abstract void askInput();

    /**
     * The showMessage method shows a message received as response from the server.
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

    public LocalModel getModel() {
        return model;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    public Map<String, List<String>> getPlayerInput() {
        return playerInput;
    }

    public List<Runnable> getInputRequested() {
        return inputRequested;
    }

    public boolean isCliTrueGuiFalse() { return cliTrueGuiFalse; }

    public void setCliTrueGuiFalse(boolean cliTrueGuiFalse) { this.cliTrueGuiFalse = cliTrueGuiFalse; }
}
