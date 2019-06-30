package it.polimi.se2019.server.deserialize;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.games.player.AmmoColor;
import it.polimi.se2019.util.DeserializerConstants;

import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

/*
TODO refactor WeaponDeserializer as EntityDeserializer?
 */
public class WeaponDeserializer implements RandomDeserializer<Weapon> {

    private static final Logger logger = Logger.getLogger(WeaponDeserializer.class.getName());

    @Override
    public Weapon deserialize(JsonObject json, DynamicDeserializerFactory deserializerFactory) throws ClassNotFoundException {
        if (json.isJsonNull()) return null;

        Gson gson = new Gson();
        Type listType = new TypeToken<List<AmmoColor>>(){}.getType();
        String name = json.get(DeserializerConstants.NAME).getAsString();
        List<AmmoColor> pickUpCost = gson.fromJson(json.getAsJsonArray(DeserializerConstants.PICKUPCOST), listType);
        List<AmmoColor> reloadCost = gson.fromJson(json.getAsJsonArray(DeserializerConstants.RELOADCOST), listType);
        ActionsDeserializer actionDeserializer = (ActionsDeserializer) deserializerFactory.getDeserializer(DeserializerConstants.ACTIONS);
        OptionalEffectDeserializer optionalEffectDeserializer =
                (OptionalEffectDeserializer) deserializerFactory.getDeserializer(DeserializerConstants.OPTIONALEFFECTS);

        List<ActionUnit> modeList = null;
        List<ActionUnit> optionalEffectList = null;

        try {
            modeList = actionDeserializer.deserialize(json, deserializerFactory);
            optionalEffectList = optionalEffectDeserializer.deserialize(json, deserializerFactory);
        } catch (ClassNotFoundException e) {
            logger.warning("Class not found.");
            throw e;
        }

        return new Weapon(modeList, name, pickUpCost, reloadCost, optionalEffectList);
    }
}