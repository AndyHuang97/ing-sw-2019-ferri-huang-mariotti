package it.polimi.se2019.server.deserialize;

import com.google.gson.JsonObject;
import it.polimi.se2019.server.games.board.Tile;

public class TileDeserializer implements RandomDeserializer {
    @Override
    public Tile deserialize(JsonObject json, DynamicDeserializerFactory deserializerFactory) throws ClassNotFoundException {
        if (json.isJsonNull()) return null;
        return null;
        


    }
}
