package it.polimi.SE2019.server.deserialize;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class DynamicDeserializerFactory {
    private Map<String, Supplier<? extends RandomDeserializer>> registeredSupplier = new HashMap<>();

    public void registerDeserializer(String type, Supplier<? extends RandomDeserializer> supplier) {
        registeredSupplier.put(type, supplier);
    }

    public RandomDeserializer getDeserializer(String type) {
        Supplier<? extends RandomDeserializer> supplier = registeredSupplier.get(type);
        return supplier.get();
    }
}