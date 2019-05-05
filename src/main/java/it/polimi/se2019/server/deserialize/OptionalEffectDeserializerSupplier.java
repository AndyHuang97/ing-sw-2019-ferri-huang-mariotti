package it.polimi.se2019.server.deserialize;

import java.util.function.Supplier;

public class OptionalEffectDeserializerSupplier implements Supplier<RandomDeserializer> {
    @Override
    public RandomDeserializer get() {
        return new OptionalEffectDeserializer();
    }
}
