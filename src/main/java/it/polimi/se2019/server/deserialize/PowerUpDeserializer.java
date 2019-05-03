package it.polimi.se2019.server.deserialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.games.Deck;

import java.util.ArrayList;
import java.util.List;

public class PowerUpDeserializer implements RandomDeserializer {
    @Override
    public Deck deserialize(JsonObject json, DynamicDeserializerFactory deserializerFactory) throws ClassNotFoundException {
        if (json.isJsonNull()) return null;

        List<PowerUp> powerUpList = new ArrayList<>();
        List<ActionUnit> actions = null;

        JsonArray jsonPowerUpArray = json.getAsJsonArray("powerupDeck");
        ActionsDeserializer actionDeserializer = (ActionsDeserializer) deserializerFactory.getDeserializer("actions");

        for(JsonElement ammoCrateElement : jsonPowerUpArray) {
            JsonObject jsonPowerup = ammoCrateElement.getAsJsonObject();
            PowerUp powerUp = null;

            try {
                actions = actionDeserializer.deserialize(jsonPowerup, deserializerFactory);
            } catch (ClassNotFoundException e) {
                throw e;
            }

            powerUp = new PowerUp(actions);
            powerUpList.add(powerUp);
        }

        Deck deck = new Deck(powerUpList);

        return deck;
    }
}
