package it.polimi.se2019.server.deserialize;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.se2019.server.actions.effects.Effect;

import java.util.logging.Logger;

public class EffectDeserializer implements RandomDeserializer<Effect> {

    private static final Logger logger = Logger.getLogger(EffectDeserializer.class.getName());

    // TODO: merge EffectDeserializer and ConditionDeserializer in LeafDeserializer
    @Override
    public Effect deserialize(JsonObject json, DynamicDeserializerFactory deserializerFactory) throws ClassNotFoundException {
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
        return (Effect) gson.fromJson(params, classType);

        }
}
