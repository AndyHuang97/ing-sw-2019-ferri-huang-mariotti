package it.polimi.se2019.server.deserialize;

import com.google.gson.*;
import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.cards.weapons.Weapon;

import java.util.ArrayList;

/*
TODO refactor WeaponDeserializer as EntityDeserializer?
 */
public class WeaponDeserializer implements RandomDeserializer<Weapon> {
    @Override
    public Weapon deserialize(JsonObject json, DynamicDeserializerFactory deserializerFactory) throws ClassNotFoundException {
        if (json.isJsonNull()) return null;

        String name = json.get("name").getAsString();
        ActionsDeserializer actionDeserializer = (ActionsDeserializer) deserializerFactory.getDeserializer("actions");
        ArrayList<ActionUnit> actions = null;
        try {
            actions = actionDeserializer.deserialize(json, deserializerFactory);
        } catch (ClassNotFoundException e) {
            throw e;
        }

        return new Weapon(name, actions);
    }
}