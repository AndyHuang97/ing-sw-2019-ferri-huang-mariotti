package it.polimi.se2019.server.deserialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.util.DeserializerConstants;

import java.util.ArrayList;

public class TileDeserializer implements RandomDeserializer {
    @Override
    public Tile deserialize(JsonObject json, DynamicDeserializerFactory deserializerFactory) throws ClassNotFoundException {
        if (json.isJsonNull()) return null;

        String name = json.get(DeserializerConstants.TYPE).getAsString();

        Gson gson = new GsonBuilder().create();
        Tile tile = null;

        if (!name.equals(DeserializerConstants.NOTILE)) {
            JsonElement jsonParams = json.get(DeserializerConstants.PARAMS);
            String params = jsonParams.toString();
            //JsonArray coords = json.get(DeserializerConstants.POS).getAsJsonArray();
            //int x = coords.get(0).getAsInt();
            //int y = coords.get(1).getAsInt();
            int x = jsonParams.getAsJsonObject().get(DeserializerConstants.XPOSITION).getAsInt();
            int y = jsonParams.getAsJsonObject().get(DeserializerConstants.YPOSITION).getAsInt();

            tile = gson.fromJson(params, Tile.class);
            tile.setId(String.valueOf(x+y*4));


            if (name.equals(DeserializerConstants.NORMALTILE)) {
                tile.setSpawnTile(false);
                tile.setWeaponCrate(new ArrayList<>());
            } else if (name.equals(DeserializerConstants.SPAWNTILE)) {
                tile.setSpawnTile(true);
                tile.setWeaponCrate(new ArrayList<>());
            }
            else throw new ClassNotFoundException();
        }


        return tile;
    }
}
