package it.polimi.se2019.server.deserialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.cards.ammocrate.AmmoCrate;
import it.polimi.se2019.server.games.Deck;

import java.util.ArrayList;
import java.util.List;

public class AmmoCrateDeserializer implements RandomDeserializer {

    @Override
    public Deck deserialize(JsonObject json, DynamicDeserializerFactory deserializerFactory) throws ClassNotFoundException {

        if (json.isJsonNull()) return null;

        List<AmmoCrate> ammoCrateList = new ArrayList<>();
        List<ActionUnit> actions = null;

        JsonArray jsonAmmoCrateArray = json.getAsJsonArray("ammocrateDeck");
        ActionsDeserializer actionDeserializer = (ActionsDeserializer) deserializerFactory.getDeserializer("actions");

        for(JsonElement ammoCrateElement : jsonAmmoCrateArray) {
            JsonObject jsonAmmoCrate = ammoCrateElement.getAsJsonObject();
            AmmoCrate ammoCrate = null;

            try {
                actions = actionDeserializer.deserialize(jsonAmmoCrate, deserializerFactory);
            } catch (ClassNotFoundException e) {
                throw e;
            }

            ammoCrate = new AmmoCrate(actions);
            ammoCrateList.add(ammoCrate);
        }

        Deck deck = new Deck(ammoCrateList);

        return deck;
    }
}
