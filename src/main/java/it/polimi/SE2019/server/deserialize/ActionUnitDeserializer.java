package it.polimi.SE2019.server.deserialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.SE2019.server.actions.ActionUnit;
import it.polimi.SE2019.server.actions.conditions.Condition;

import java.util.ArrayList;

public class ActionUnitDeserializer implements RandomDeserializer<ActionUnit> {
    @Override
    public ActionUnit deserialize(JsonObject json, DynamicDeserializerFactory deserializerFactory) {
        if (json.isJsonNull()) return null;

        String name = json.get("name").getAsString();
        JsonArray jsonConditionArray = json.get("conditions").getAsJsonArray();

        RandomDeserializer conditionDeserializer = (ConditionDeserializer) deserializerFactory.getDeserializer("conditions");

        ArrayList<Condition> conditionArrayList = new ArrayList<Condition>();

        for (JsonElement condition : jsonConditionArray) {
            JsonObject jsonCondition = condition.getAsJsonObject();
            Condition a = conditionDeserializer.deserialize(jsonCondition, deserializerFactory);

            conditionArrayList.add(a);
        }

        return new ActionUnit(name, conditionArrayList);

    }
}
