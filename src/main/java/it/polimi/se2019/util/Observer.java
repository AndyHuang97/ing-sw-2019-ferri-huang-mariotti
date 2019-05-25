package it.polimi.se2019.util;

import it.polimi.se2019.server.exceptions.MessageParseException;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.GameManager;

public interface Observer<T> {

    void update(T message) throws GameManager.GameNotFoundException, MessageParseException, UnpackingException;
}