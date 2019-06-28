package it.polimi.se2019.server.deserialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.cards.ammocrate.AmmoCrate;
import it.polimi.se2019.server.games.Deck;
import it.polimi.se2019.util.DeserializerConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class AmmoCrateDeserializer implements RandomDeserializer {

    private static final Logger logger = Logger.getLogger(AmmoCrateDeserializer.class.getName());

    @Override
    public Deck deserialize(JsonObject json, DynamicDeserializerFactory deserializerFactory) throws ClassNotFoundException {

        if (json.isJsonNull()) return null;

        List<AmmoCrate> ammoCrateList = new ArrayList<>();
        List<ActionUnit> actions = null;

        JsonArray jsonAmmoCrateArray = json.getAsJsonArray(DeserializerConstants.AMMOCRATEDECK);
        ActionsDeserializer actionDeserializer = (ActionsDeserializer) deserializerFactory.getDeserializer(DeserializerConstants.ACTIONS);

        for(JsonElement ammoCrateElement : jsonAmmoCrateArray) {
            JsonObject jsonAmmoCrate = ammoCrateElement.getAsJsonObject();
            String name = jsonAmmoCrate.get(DeserializerConstants.NAME).getAsString();
            int amount = jsonAmmoCrate.get(DeserializerConstants.AMOUNT).getAsInt();
            AmmoCrate ammoCrate = null;

            try {
                actions = actionDeserializer.deserialize(jsonAmmoCrate, deserializerFactory);
            } catch (ClassNotFoundException e) {
                logger.warning("Class not found.");
                throw e;
            }

            for (int i = 0; i< amount; i++) {
                ammoCrate = new AmmoCrate(actions, name);
                ammoCrateList.add(ammoCrate);
            }
        }

        return new Deck(ammoCrateList);
    }
}
