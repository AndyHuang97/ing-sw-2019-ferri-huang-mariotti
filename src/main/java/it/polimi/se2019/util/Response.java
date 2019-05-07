package it.polimi.se2019.util;

import com.google.gson.Gson;
import it.polimi.se2019.server.games.Game;

import java.io.Serializable;

public class Response implements Serializable, NetMsg {

    private Game game;
    private boolean success;
    private String message;
    private String nickname;

    public Response(Game game, boolean success, String message, String nickname) {
        this.game = game;
        this.success = success;
        this.message = message;
        this.nickname = nickname;
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

    public String getNickname() {
        return this.nickname;
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
