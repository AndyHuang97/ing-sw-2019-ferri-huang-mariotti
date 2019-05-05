package it.polimi.se2019.server.deserialize;

import java.util.function.Supplier;

public class TileDeserializerSupplier implements Supplier<RandomDeserializer> {
    @Override
    public TileDeserializer get() {
        return new TileDeserializer();
    }
}
