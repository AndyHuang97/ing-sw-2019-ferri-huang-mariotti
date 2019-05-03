package it.polimi.se2019.server.deserialize;

import java.util.function.Supplier;

public class AmmoCrateDeserializerSupplier implements Supplier<RandomDeserializer> {
    @Override
    public AmmoCrateDeserializer get() {
        return new AmmoCrateDeserializer();
    }
}
