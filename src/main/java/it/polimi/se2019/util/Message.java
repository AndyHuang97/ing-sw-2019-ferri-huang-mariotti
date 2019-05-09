package it.polimi.se2019.util;

import it.polimi.se2019.server.games.Targetable;

import java.util.List;

public class Message {
    private String messageType;
    private List<Targetable> params;

    public List getParams() {
        return params;
    }

    public String getMessageType() {
        return messageType;
    }

    @Override
    public String toString() {
        return messageType + params.toString();
    }
}