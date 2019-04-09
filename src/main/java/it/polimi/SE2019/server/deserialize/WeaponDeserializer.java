package it.polimi.SE2019.server.deserialize;

import com.google.gson.*;
import it.polimi.SE2019.server.actions.ActionUnit;
import it.polimi.SE2019.server.cards.weapons.Weapon;

import java.util.ArrayList;

public class WeaponDeserializer implements RandomDeserializer<Weapon> {
    @Override
    public Weapon deserialize(JsonObject json, DynamicDeserializerFactory deserializerFactory) throws JsonParseException {
        if (json.isJsonNull()) return null;

        String name = json.get("name").getAsString();
        // actions contains a valid json
        JsonObject actionsJson = json.get("actions").getAsJsonObject();
        // deserialize actions json
        ActionsDeserializer actionDeserializer = (ActionsDeserializer) deserializerFactory.getDeserializer("actions");
        ArrayList<ActionUnit> actions = actionDeserializer.deserialize(actionsJson, deserializerFactory);


        return new Weapon(name, actions);
    }
}

// { "name" : "gun", "actions" : [ "name"