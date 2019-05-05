package it.polimi.se2019.server.deserialize;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.games.player.AmmoColor;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/*
TODO refactor WeaponDeserializer as EntityDeserializer?
 */
public class WeaponDeserializer implements RandomDeserializer<Weapon> {
    @Override
    public Weapon deserialize(JsonObject json, DynamicDeserializerFactory deserializerFactory) throws ClassNotFoundException {
        if (json.isJsonNull()) return null;

        Gson gson = new Gson();
        Type listType = new TypeToken<List<AmmoColor>>(){}.getType();
        String name = json.get("name").getAsString();
        List<AmmoColor> pickUpCost = gson.fromJson(json.getAsJsonArray("pickUpCost"), listType);
        List<AmmoColor> reloadCost = gson.fromJson(json.getAsJsonArray("reloadCost"), listType);
        ActionsDeserializer actionDeserializer = (ActionsDeserializer) deserializerFactory.getDeserializer("actions");
        OptionalEffectDeserializer optionalEffectDeserializer =
                (OptionalEffectDeserializer) deserializerFactory.getDeserializer("optionaleffects");

        ArrayList<ActionUnit> modeList = null;
        ArrayList<ActionUnit> optionalEffectList = null;

        try {
            modeList = actionDeserializer.deserialize(json, deserializerFactory);
            optionalEffectList = optionalEffectDeserializer.deserialize(json, deserializerFactory);
        } catch (ClassNotFoundException e) {
            throw e;
        }

        return new Weapon(modeList, name, pickUpCost, reloadCost, optionalEffectList);
    }
}