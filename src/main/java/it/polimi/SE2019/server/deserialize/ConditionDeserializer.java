package it.polimi.SE2019.server.deserialize;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.SE2019.server.actions.conditions.Condition;

public class ConditionDeserializer implements RandomDeserializer<Condition> {
    @Override
    public Condition deserialize(JsonObject json, DynamicDeserializerFactory deserializerFactory) {
        if (json.isJsonNull()) return null;

        String type = json.get("type").getAsString();
        String params = json.get("params").getAsString();

        Class classType = conditionSelector.getCondition(type);

        Gson gson = new Gson();
        return (Condition) gson.fromJson(params, classType);



    }
}
