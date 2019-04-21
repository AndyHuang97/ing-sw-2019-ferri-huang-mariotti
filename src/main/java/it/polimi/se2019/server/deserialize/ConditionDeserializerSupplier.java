package it.polimi.se2019.server.deserialize;

import java.util.function.Supplier;

public class ConditionDeserializerSupplier implements Supplier<RandomDeserializer> {
    @Override
    public ConditionDeserializer get() {
        return new ConditionDeserializer();
    }
}
