package it.polimi.se2019.server.deserialize;

import com.google.gson.JsonObject;

/**
 * This is the interface for all of the deserializers, the deserializer has a very tedious job of creating the objects as we need them,
 * This is necessary since gson cannot do this alone. We use a factory specific for each object we want to deserialize.
 *
 * @author andreahuang
 *
 */
public interface RandomDeserializer<T> {

    /**
     * This is where all the magic happens, given a json part (only the useful part of the json for the specific deserializer) the deserializer is able to create the required object.
     *
     * @author andreahuang
     *
     */
    T deserialize(JsonObject json, DynamicDeserializerFactory deserializerFactory) throws ClassNotFoundException;
}
