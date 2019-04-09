package it.polimi.SE2019.server.deserialize;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.SE2019.server.actions.conditions.Condition;

public class ConditionDeserializer implements RandomDeserializer<Condition> {
    @Override
    public Condition deserialize(JsonObject json, DynamicDeserializerFactory deserializerFactory) throws ClassNotFoundException {
        if (json.isJsonNull()) return null;

        String name = json.get("name").getAsString();
        String className = json.get("class_name").getAsString();
        String params = json.get("params").toString();

        /*
        TODO: avoid reflection
         */
        Class classType = null;

        try {
            classType = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw e;
        }

        Gson gson = new Gson();
        return (Condition) gson.fromJson(params, classType);

    }
}
