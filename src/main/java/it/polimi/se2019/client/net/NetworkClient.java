package it.polimi.se2019.client.net;

import it.polimi.se2019.client.View;
import it.polimi.se2019.util.Request;

public interface NetworkClient {
    void send(Request request);
    void start(View view);
}
