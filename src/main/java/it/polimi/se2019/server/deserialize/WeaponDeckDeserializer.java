package it.polimi.se2019.server.deserialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.games.Deck;

import java.util.ArrayList;
import java.util.List;

public class WeaponDeckDeserializer implements RandomDeserializer {
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
                throw e;
            }

            weaponList.add(weapon);
        }

        Deck<Weapon> deck = new Deck(weaponList);

        return deck;
    }
}
