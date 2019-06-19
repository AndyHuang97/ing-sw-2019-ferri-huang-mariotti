package it.polimi.se2019.util;

import com.google.gson.Gson;
import it.polimi.se2019.server.dataupdate.StateUpdate;
import it.polimi.se2019.server.games.Game;

import java.io.Serializable;
import java.util.List;

public class Response implements Serializable, NetMsg {

    private Game game;
    private boolean success;
    private String message;

    private List<StateUpdate> updateData;

    public Response(Game game, boolean success, String message) {
        this.game = game;
        this.success = success;
        this.message = message;
    }

    public Response(List<StateUpdate> updateData) {
        this.updateData = updateData;
    }

    public Game getGame() {
        return this.game;
    }

    public boolean getSuccess() {
        return this.success;
    }

    public String getMessage() {
        return this.message;
    }

    public List<StateUpdate> getUpdateData() {
        return updateData;
    }

    @Override
    public String serialize() {

        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public NetMsg deserialize(String msg) {

        Gson gson = new Gson();
        return gson.fromJson(msg, Response.class);
    }
}
