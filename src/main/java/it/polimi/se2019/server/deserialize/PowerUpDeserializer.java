package it.polimi.se2019.server.deserialize;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.games.Deck;
import it.polimi.se2019.server.games.player.AmmoColor;
import it.polimi.se2019.util.DeserializerConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PowerUpDeserializer implements RandomDeserializer {

    private static final Logger logger = Logger.getLogger(PowerUpDeserializer.class.getName());

    @Override
    public Deck deserialize(JsonObject json, DynamicDeserializerFactory deserializerFactory) throws ClassNotFoundException {
        if (json.isJsonNull()) return null;

        List<PowerUp> powerUpList = new ArrayList<>();
        List<ActionUnit> actions = null;

        JsonArray jsonPowerUpArray = json.getAsJsonArray(DeserializerConstants.POWERUPDECK);
        ActionsDeserializer actionDeserializer = (ActionsDeserializer) deserializerFactory.getDeserializer(DeserializerConstants.ACTIONS);

        for(JsonElement ammoCrateElement : jsonPowerUpArray) {
            JsonObject jsonPowerup = ammoCrateElement.getAsJsonObject();
            String name = jsonPowerup.get(DeserializerConstants.NAME).getAsString();
            int amount = jsonPowerup.get(DeserializerConstants.AMOUNT).getAsInt();
            String colorString = jsonPowerup.get(DeserializerConstants.COLOR).toString();
            Gson gson = new Gson();
            AmmoColor ammoColor = gson.fromJson(colorString, AmmoColor.class);
            PowerUp powerUp = null;

            try {
                actions = actionDeserializer.deserialize(jsonPowerup, deserializerFactory);
            } catch (ClassNotFoundException e) {
                logger.warning("Class not found.");
                throw e;
            }

            for (int i = 0; i< amount; i++) {
                powerUp = new PowerUp(actions, name, ammoColor);
                powerUpList.add(powerUp);
            }
        }

        return new Deck(powerUpList);
    }
}
