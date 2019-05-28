package it.polimi.se2019.util;

import com.google.gson.Gson;
import it.polimi.se2019.server.net.CommandHandler;

import java.io.Serializable;

/**
 * Request objects are used by the View to send data to the Controller
 */

public class Request implements Serializable, NetMsg {

    private Message message;
    private String nickname;
    private CommandHandler commandHandler;

    public Request(Message message, String nickname) {
        this.message = message;
        this.nickname = nickname;
    }

    public Message getMessage() {
        return this.message;
    }

    public void setMessage(Message message) {
        this.message = message;
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
        return gson.fromJson(msg, Request.class);
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    public void setCommandHandler(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }
}
