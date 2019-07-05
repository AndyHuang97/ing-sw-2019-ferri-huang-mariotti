package it.polimi.se2019.server.deserialize;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Here based on a string we get the correct deserializer, its able to give back the deserializer.
 *
 * @author andreahuang
 *
 */
public class DynamicDeserializerFactory {
    private Map<String, Supplier<? extends RandomDeserializer>> registeredSupplier = new HashMap<>();

    /**
     * This is to load a new deserializer inside the possible deserializers
     *
     * @param type the deserializer name
     * @param supplier the deserializer itself
     *
     */
    public void registerDeserializer(String type, Supplier<? extends RandomDeserializer> supplier) {
        registeredSupplier.put(type, supplier);
    }

    /**
     * This is to retrieve the deserializer from the possible deserializers
     *
     * @param type the deserializer name
     * @return the deserializer
     *
     */
    public RandomDeserializer getDeserializer(String type) {
        Supplier<? extends RandomDeserializer> supplier = registeredSupplier.get(type);
        return supplier.get();
    }
}