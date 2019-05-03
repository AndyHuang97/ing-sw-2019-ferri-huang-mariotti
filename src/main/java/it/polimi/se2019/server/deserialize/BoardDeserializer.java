package it.polimi.se2019.server.deserialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.se2019.server.games.board.Board;
import it.polimi.se2019.server.games.board.Tile;

public class BoardDeserializer implements RandomDeserializer {
    @Override
    public Board deserialize(JsonObject json, DynamicDeserializerFactory deserializerFactory) throws ClassNotFoundException {
        if (json.isJsonNull()) return null;
        
        JsonArray jsonTileArray = json.getAsJsonArray("tiles");

        // TODO: hardcoded array size
        Tile[][] tileMap = new Tile[12][12];
        // TODO: use LeafDeseializer (?), read deserializer name from confic
        TileDeserializer tileDeserializer = (TileDeserializer) deserializerFactory.getDeserializer("tile");

        for (JsonElement tileElement : jsonTileArray) {
            JsonObject jsonTile = tileElement.getAsJsonObject();

            Tile tile = null;

            try {
                tile = tileDeserializer.deserialize(jsonTile, deserializerFactory);
            } catch (ClassNotFoundException e) {
                throw e;
            }

            // read position and add the tile to the array
            // TODO: read config
            JsonArray position = jsonTile.getAsJsonArray("pos");
            int xCoord = position.get(0).getAsInt();
            int yCoord = position.get(1).getAsInt();

            tileMap[xCoord][yCoord] = tile;
        }

        // TODO: pad empty map spaces with void unlinked tiles, maybe flyweight pattern (?)

        Board board = new Board(tileMap);

        return board;
    }
}
