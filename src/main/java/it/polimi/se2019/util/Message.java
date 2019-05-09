package it.polimi.se2019.util;

import java.util.List;

public class Message {
    private String messageType;
    private List params;

    public List getParams() {
        return params;
    }

    public String getMessageType() {
        return messageType;
    }
}