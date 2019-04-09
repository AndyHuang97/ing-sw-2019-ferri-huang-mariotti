package it.polimi.SE2019.server.deserialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.SE2019.server.actions.ActionUnit;
import it.polimi.SE2019.server.actions.conditions.Condition;
import it.polimi.SE2019.server.actions.effects.Effect;

import java.util.ArrayList;

public class ActionUnitDeserializer implements RandomDeserializer<ActionUnit> {
    @Override
    public ActionUnit deserialize(JsonObject json, DynamicDeserializerFactory deserializerFactory) throws ClassNotFoundException {
        if (json.isJsonNull()) return null;

        String name = json.get("name").getAsString();
        JsonArray jsonEffectArray = json.get("effects").getAsJsonArray();
        JsonArray jsonConditionArray = json.get("conditions").getAsJsonArray();

        ConditionDeserializer conditionDeserializer = (ConditionDeserializer) deserializerFactory.getDeserializer("conditions");
        EffectDeserializer effectDeserializer = (EffectDeserializer) deserializerFactory.getDeserializer("effects");

        ArrayList<Effect> effectArrayList = new ArrayList<Effect>();
        ArrayList<Condition> conditionArrayList = new ArrayList<Condition>();

        for (JsonElement effect : jsonEffectArray) {
            JsonObject jsonEffect = effect.getAsJsonObject();

            Effect effectObject = null;

            try {
                effectObject = effectDeserializer.deserialize(jsonEffect, deserializerFactory);
            } catch (ClassNotFoundException e) {
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
                throw e;
            }

            conditionArrayList.add(conditionObject);
        }

        return new ActionUnit(name, effectArrayList, conditionArrayList);
    }
}
