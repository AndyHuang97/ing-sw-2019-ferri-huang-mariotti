package it.polimi.se2019.util;

/**
 * Net msg class, this is different from the net message this one travels over the net directly
 *
 * @author AH
 *
 */
public interface NetMsg {

    /**
     * Serialize the content
     *
     * @return the serialized content
     *
     */
    String serialize();

    /**
     * Deserialize the string
     *
     * @param message the message
     * @return the netmsg
     *
     */
    NetMsg deserialize(String message);
}
