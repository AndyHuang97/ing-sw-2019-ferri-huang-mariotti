package it.polimi.se2019.util;

import com.google.gson.Gson;

import java.io.Serializable;

public class Request implements Serializable, NetMsg {

    private String message;

    public Request(String message) {
        this.message = message;
    }

    @Override
    public String serialize() {
        Gson gson = new Gson();
        String  str = gson.toJson(message);

        System.out.println(str);
        return str;
    }

    @Override
    public NetMsg deserialize(String msg) {

        Gson gson = new Gson();
        return gson.fromJson(msg, Request.class);
    }
}
