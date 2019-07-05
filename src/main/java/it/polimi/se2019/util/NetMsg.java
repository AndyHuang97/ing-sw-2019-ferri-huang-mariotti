package it.polimi.se2019.util;

/**
 * The NetMsg interface is the interface for all kinds of network messages.
 *
 * @author andreahuang
 */
public interface NetMsg {
    String serialize();
    NetMsg deserialize(String message);
}
