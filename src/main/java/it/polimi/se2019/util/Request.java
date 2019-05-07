package it.polimi.se2019.util;

import com.google.gson.Gson;

import java.io.Serializable;

public class Request implements Serializable, NetMsg {

    private String message;
    private String nickname;

    public Request(String message, String nickname) {
        this.message = message;
        this.nickname = nickname;
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
        return gson.toJson(message);
    }

    @Override
    public NetMsg deserialize(String msg) {

        Gson gson = new Gson();
        return gson.fromJson(msg, Request.class);
    }
}
