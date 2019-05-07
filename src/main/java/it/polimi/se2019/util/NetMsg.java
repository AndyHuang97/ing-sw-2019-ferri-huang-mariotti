package it.polimi.se2019.util;

public interface NetMsg {

    String serialize();
    NetMsg deserialize(String msg);
}
