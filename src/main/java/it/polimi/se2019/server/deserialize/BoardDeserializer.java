package it.polimi.se2019.server.deserialize;

import com.google.gson.JsonObject;
import it.polimi.se2019.server.games.board.Board;

public class BoardDeserializer implements RandomDeserializer {
    @Override
    public Board deserialize(JsonObject json, DynamicDeserializerFactory deserializerFactory) throws ClassNotFoundException {
        if (json.isJsonNull()) return null;

    }
}
