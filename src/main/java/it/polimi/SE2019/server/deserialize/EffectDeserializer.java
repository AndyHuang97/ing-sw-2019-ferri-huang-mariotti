package it.polimi.SE2019.server.deserialize;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.SE2019.server.actions.effects.Effect;

public class EffectDeserializer implements RandomDeserializer<Effect> {
    @Override
    public Effect deserialize(JsonObject json, DynamicDeserializerFactory deserializerFactory) throws ClassNotFoundException {
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
        return (Effect) gson.fromJson(params, classType);

        }
}
