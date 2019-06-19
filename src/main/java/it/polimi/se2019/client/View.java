package it.polimi.se2019.client;

import it.polimi.se2019.client.net.RmiClient;
import it.polimi.se2019.client.net.SocketClient;
import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.dataupdate.StateUpdate;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class View extends Observable implements Observer<Response> {

    private LocalModel model;
    private String nickname;
    private PlayerColor playerColor;

    private Map<String, List<String>> playerInput = new HashMap<>();
    private List<Runnable> inputRequested = new ArrayList<>();

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

    public void connect(String nickname, String ip, String connectionType) {

        switch (connectionType) {
            case Constants.RMI:
                // connect via rmi
                RmiClient rmiClient = new RmiClient(nickname, ip);
                rmiClient.start(this);
                Map<String, List<String>> rmiPayload = new HashMap<>();
                rmiPayload.put("connect", new ArrayList<>());
                rmiClient.send(new Request(new NetMessage(rmiPayload), nickname));
                break;
            case Constants.SOCKET:
                // connect via socket
                SocketClient socketClient = new SocketClient(nickname, ip);
                socketClient.start(this);
                // starting thread that redraws stuffs
                Map<String, List<String>> socketPayload = new HashMap<>();
                socketPayload.put("connect", new ArrayList<>());
                socketClient.send(new Request(new NetMessage(socketPayload), nickname));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + connectionType);
        }
    }

    /**
     * The sendInput method sends an input message to the server.
     */
    public void sendInput() {
        //TODO send input via network
        // ...
        System.out.println(">>> Sending: " + getPlayerInput());
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
}
