package it.polimi.se2019.server.deserialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.actions.conditions.Condition;
import it.polimi.se2019.server.actions.effects.Effect;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ActionUnitDeserializer implements RandomDeserializer<ActionUnit> {

    private static final Logger logger = Logger.getLogger(ActionUnitDeserializer.class.getName());

    @Override
    public ActionUnit deserialize(JsonObject json, DynamicDeserializerFactory deserializerFactory) throws ClassNotFoundException {
        if (json.isJsonNull()) return null;

        String name = json.get("name").getAsString();
        boolean available = json.get("available").getAsBoolean();
        JsonArray jsonEffectArray = json.get("effectList").getAsJsonArray();
        JsonArray jsonConditionArray = json.get("conditionList").getAsJsonArray();

        ConditionDeserializer conditionDeserializer = (ConditionDeserializer) deserializerFactory.getDeserializer("conditions");
        EffectDeserializer effectDeserializer = (EffectDeserializer) deserializerFactory.getDeserializer("effects");

        List<Effect> effectArrayList = new ArrayList<>();
        List<Condition> conditionArrayList = new ArrayList<>();

        for (JsonElement effect : jsonEffectArray) {
            JsonObject jsonEffect = effect.getAsJsonObject();

            Effect effectObject = null;

            try {
                effectObject = effectDeserializer.deserialize(jsonEffect, deserializerFactory);
            } catch (ClassNotFoundException e) {
                logger.warning("Class not found.");
                throw e;
            }

            effectArrayList.add(effectObject);
        }

        for (JsonElement condition : jsonConditionArray) {
            JsonObject jsonCondition = condition.getAsJsonObject();

            Condition conditionObject = null;
            try {
                conditionObject = conditionDeserializer.deserialize(jsonCondition, deserializerFactory);
            } catch (ClassNotFoundException e) {
                logger.warning("Class not found.");
                throw e;
            }

            conditionArrayList.add(conditionObject);
        }

        return new ActionUnit(available, name, effectArrayList, conditionArrayList);
    }
}
