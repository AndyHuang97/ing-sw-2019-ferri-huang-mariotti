package it.polimi.se2019.server.deserialize;

import java.util.function.Supplier;

public class EffectDeserializerSupplier implements Supplier<RandomDeserializer> {
    @Override
    public EffectDeserializer get() {
        return new EffectDeserializer();
    }
}
