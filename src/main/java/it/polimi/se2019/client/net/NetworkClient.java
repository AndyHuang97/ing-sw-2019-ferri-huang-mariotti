package it.polimi.se2019.client.net;

import it.polimi.se2019.client.View;
import it.polimi.se2019.util.Request;

/**
 * The network interface, has the methods that needs to be implemented
 *
 * @author FF
 *
 */
public interface NetworkClient {
    void send(Request request);
    void start(View view);
}
