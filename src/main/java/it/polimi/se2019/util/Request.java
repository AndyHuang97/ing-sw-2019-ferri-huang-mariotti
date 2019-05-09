package it.polimi.se2019.util;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Request objects are used by the View to send data to the Controller
 */

public class Request implements Serializable, NetMsg {

    private Message message;
    private String nickname;

    public Request(Message message, String nickname) {
        this.message = message;
        this.nickname = nickname;
    }

    public Message getMessage() {
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
