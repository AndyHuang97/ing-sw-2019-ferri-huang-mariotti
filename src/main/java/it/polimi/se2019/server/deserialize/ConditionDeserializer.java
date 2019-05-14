package it.polimi.se2019.server.deserialize;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.se2019.server.actions.conditions.Condition;

import java.util.logging.Logger;

public class ConditionDeserializer implements RandomDeserializer<Condition> {

    private static final Logger logger = Logger.getLogger(Condition.class.getName());

    @Override
    public Condition deserialize(JsonObject json, DynamicDeserializerFactory deserializerFactory) throws ClassNotFoundException {
        if (json.isJsonNull()) return null;

        String className = json.get("class_name").getAsString();
        String params = json.get("params").toString();

        // TODO: avoid reflection
        Class classType = null;

        try {
            classType = Class.forName(className);
        } catch (ClassNotFoundException e) {
            logger.warning("Exception: " + className);
            throw e;
        }

        Gson gson = new Gson();
        return (Condition) gson.fromJson(params, classType);

    }
}
