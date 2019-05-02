package it.polimi.se2019.server.deserialize;

import com.google.gson.JsonObject;

// TODO: refactor name
public interface RandomDeserializer<T> {
    T deserialize(JsonObject json, DynamicDeserializerFactory deserializerFactory) throws ClassNotFoundException;
}
