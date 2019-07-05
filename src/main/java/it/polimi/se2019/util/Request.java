package it.polimi.se2019.util;

import com.google.gson.Gson;
import it.polimi.se2019.server.net.CommandHandler;

import java.io.*;
import java.util.logging.Logger;

/**
 * Request objects are used by the View to send data to the Controller
 *
 * @author AH
 *
 */
public class Request implements Serializable, NetMsg {

    private InternalMessage internalMessage;
    private NetMessage netMessage;
    private String nickname;
    private CommandHandler commandHandler;
    private static ByteArrayOutputStream bos = new ByteArrayOutputStream();
    private static ObjectOutputStream out;

    static {
        try {
            out = new ObjectOutputStream(bos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Request(String nickname) {
        this.nickname = nickname;
    }

    public Request(NetMessage netMessage, String nickname) {
        this.netMessage = netMessage;
        this.nickname = nickname;
    }

    public Request(InternalMessage internalMessage, String nickname) {
        this.internalMessage = internalMessage;
        this.nickname = nickname;
    }

    public InternalMessage getInternalMessage() {
        return internalMessage;
    }

    public NetMessage getNetMessage() {
        return netMessage;
    }

    public void setInternalMessage(InternalMessage internalMessage) {
        this.internalMessage = internalMessage;
    }

    public void setNetMessage(NetMessage netMessage) {
        this.netMessage = netMessage;
    }

    public String getNickname() {
        return this.nickname;
    }


    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    public void setCommandHandler(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public NetMsg deserialize(String message) {
        Gson gson = new Gson();
        return gson.fromJson(message, Request.class);
    }
}
