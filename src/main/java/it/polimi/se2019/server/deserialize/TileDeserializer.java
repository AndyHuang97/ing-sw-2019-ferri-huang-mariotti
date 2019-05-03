package it.polimi.se2019.server.deserialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import it.polimi.se2019.server.games.board.NormalTile;
import it.polimi.se2019.server.games.board.SpawnTile;
import it.polimi.se2019.server.games.board.Tile;

public class TileDeserializer implements RandomDeserializer {
    @Override
    public Tile deserialize(JsonObject json, DynamicDeserializerFactory deserializerFactory) throws ClassNotFoundException {
        if (json.isJsonNull()) return null;

        String name = json.get("type").getAsString();


        Gson gson = new GsonBuilder().create();
        Tile tile = null;

        // TODO: read tile name form config
        if (name.equals("NormalTile")) {
            // TODO: maybe edit data so that NoTitle has a param field
            String params = json.get("params").toString();
            tile = gson.fromJson(params, NormalTile.class);
        }

        else if (name.equals("SpawnTile")) {
            String params = json.get("params").toString();
            tile = gson.fromJson(params, SpawnTile.class);
        }

        else if (name.equals("NoTile")) {
            tile = null;
        }

        else throw new ClassNotFoundException();

        return tile;
    }
}
