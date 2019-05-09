package it.polimi.se2019.server.deserialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.games.Deck;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class WeaponDeckDeserializer implements RandomDeserializer {

    private static final Logger logger = Logger.getLogger(WeaponDeckDeserializer.class.getName());

    @Override
    public Deck<Weapon> deserialize(JsonObject json, DynamicDeserializerFactory deserializerFactory) throws ClassNotFoundException {
        if (json.isJsonNull()) return null;

        JsonArray jsonWeaponArray = json.getAsJsonArray("weaponDeck");

        List<Weapon> weaponList = new ArrayList<>();
        WeaponDeserializer weaponDeserializer = (WeaponDeserializer) deserializerFactory.getDeserializer("weapon");

        for(JsonElement weaponElement : jsonWeaponArray) {
            JsonObject jsonWeapon = weaponElement.getAsJsonObject();

            Weapon weapon = null;

            try {
                weapon = weaponDeserializer.deserialize(jsonWeapon, deserializerFactory);
            } catch (ClassNotFoundException e) {
                logger.warning("Class not found.");
                throw e;
            }

            weaponList.add(weapon);
        }

        return (Deck<Weapon>) new Deck(weaponList);
    }
}
