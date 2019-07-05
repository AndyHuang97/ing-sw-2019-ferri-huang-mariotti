package it.polimi.se2019.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.se2019.server.dataupdate.*;
import it.polimi.se2019.server.games.Game;

import java.io.Serializable;
import java.util.List;

/**
 * The Response class is the network object that is sent from server to client for any kind of notification and
 * communitation. It can contain a game for match initialization or a list of updates. It always carries a String
 * message, which can be used by the server to send view selection messages.
 *
 * @author andreahuang
 */
public class Response implements Serializable, NetMsg {

    private Game game;
    private boolean success;
    private String message;

    private List<StateUpdate> updateData;

    /**
     * Default constructor. It sets up the parameters for match initialization or message selection
     * for the String message.
     *
     * @param game the initialized game, if present
     * @param success is boolean parameter used to call the showMessage method or the reportError method
     *                on the client.
     * @param message is selection message when success is true, an error message otherwise.
     */
    public Response(Game game, boolean success, String message) {
        this.game = game;
        this.success = success;
        this.message = message;
    }

    /**
     * Constructor for update responses.
     *
     * @param updateData is a list of buffered StateUpdate to be sent to the client.
     */
    public Response(List<StateUpdate> updateData) {
        this.success = true;
        this.message =  "Model update";
        this.updateData = updateData;
    }

    /**
     * Getter for game, when a game initialization is sent.
     *
     * @return the initialized game.
     */
    public Game getGame() {
        return this.game;
    }

    /**
     * Getter for success parameter.
     *
     * @return the success parameter used on the client for showMessage and reportError differentiation.
     */
    public boolean getSuccess() {
        return this.success;
    }

    /**
     * Getter for the string message.
     *
     * @return the string message, either for showMessage or reportError on the client.
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Getter for updateData.
     *
     * @return the list of updates to be performed on the client.
     */
    public List<StateUpdate> getUpdateData() {
        return updateData;
    }

    /**
     * This is the serialization method used to send a server message via network
     *
     * @return
     */
    @Override
    public String serialize() {
       Gson gson = adapterGson();
        return gson.toJson(this, Response.class);
    }

    /**
     * This is the deserialization method used to deserialize the message coming from the server.
     *
     * @param msg the serialized message coming from the server.
     * @return the deserialized Response object.
     */
    @Override
    public NetMsg deserialize(String msg) {

        Gson gson = adapterGson();

        return gson.fromJson(msg, Response.class);
    }

    private Gson adapterGson() {
        RuntimeTypeAdapterFactory<StateUpdate> stateUpdateAdapterFactory = RuntimeTypeAdapterFactory.of(StateUpdate.class, "type")
                .registerSubtype(AmmoCrateUpdate.class, "AmmoCrateUpdate")
                .registerSubtype(WeaponCrateUpdate.class, "WeaponCrateUpdate")
                .registerSubtype(CharacterStateUpdate.class, "CharacterStateUpdate")
                .registerSubtype(CurrentPlayerStateUpdate.class, "CurrentPlayerStateUpdate")
                .registerSubtype(CurrentWeaponUpdate.class, "CurrentWeaponUnpdate")
                .registerSubtype(KillShotTrackUpdate.class, "KillShotTrackUpdate")
                .registerSubtype(WeaponStateUpdate.class, "WeaponStateUpdate");

        return new GsonBuilder().registerTypeAdapterFactory(stateUpdateAdapterFactory).create();
    }
}
