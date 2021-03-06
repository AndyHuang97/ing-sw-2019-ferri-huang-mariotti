package it.polimi.se2019.server.deserialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.se2019.server.games.board.Board;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.util.DeserializerConstants;

import java.util.logging.Logger;

public class BoardDeserializer implements RandomDeserializer {

    private static final Logger logger = Logger.getLogger(BoardDeserializer.class.getName());

    @Override
    public Board deserialize(JsonObject json, DynamicDeserializerFactory deserializerFactory) throws ClassNotFoundException {
        if (json.isJsonNull()) return null;

        String id = json.get(DeserializerConstants.ID).getAsString();
        JsonArray jsonTileArray = json.getAsJsonArray(DeserializerConstants.TILES);

        // TODO: hardcoded array size
        Tile[][] tileMap = new Tile[4][3];
        TileDeserializer tileDeserializer = (TileDeserializer) deserializerFactory.getDeserializer(DeserializerConstants.TILE);

        for (JsonElement tileElement : jsonTileArray) {
            JsonObject jsonTile = tileElement.getAsJsonObject();

            Tile tile = null;

            try {
                tile = tileDeserializer.deserialize(jsonTile, deserializerFactory);
            } catch (ClassNotFoundException e) {
                logger.warning("Class not found.");
                throw e;
            }

            // read position and add the tile to the array
            JsonObject jsonParams = jsonTile.get(DeserializerConstants.PARAMS).getAsJsonObject();
            int xCoord = jsonParams.get(DeserializerConstants.XPOSITION).getAsInt();
            int yCoord = jsonParams.get(DeserializerConstants.YPOSITION).getAsInt();

            tileMap[xCoord][yCoord] = tile;
        }

        return new Board(id, tileMap);
    }
}
