package it.polimi.se2019.server.deserialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.polimi.se2019.server.cards.ammocrate.AmmoCrate;
import it.polimi.se2019.server.games.board.NormalTile;
import it.polimi.se2019.server.games.board.SpawnTile;
import it.polimi.se2019.server.games.board.Tile;

import java.util.ArrayList;
import java.util.UUID;

public class TileDeserializer implements RandomDeserializer {
    @Override
    public Tile deserialize(JsonObject json, DynamicDeserializerFactory deserializerFactory) throws ClassNotFoundException {
        if (json.isJsonNull()) return null;

        String name = json.get("type").getAsString();

        Gson gson = new GsonBuilder().create();
        Tile tile = null;

        if (!name.equals("NoTile")) {

            String params = json.get("params").toString();
            tile = gson.fromJson(params, Tile.class);
            JsonArray coords = json.get("pos").getAsJsonArray();
            int x = coords.get(0).getAsInt();
            int y = coords.get(1).getAsInt();
            tile.setId(String.valueOf(x+y*4));

            // TODO: read tile name from config
            if (name.equals("NormalTile")) {
                // TODO: maybe edit data so that NoTitle has a param field
                tile.setSpawnTile(false);
                tile.setWeaponCrate(new ArrayList<>());
            } else if (name.equals("SpawnTile")) {
                tile.setSpawnTile(true);
                tile.setWeaponCrate(new ArrayList<>());
            }
            else throw new ClassNotFoundException();
        }


        return tile;
    }
}
