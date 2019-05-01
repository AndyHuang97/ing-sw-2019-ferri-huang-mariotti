package it.polimi.se2019.server.deserialize;

import java.util.function.Supplier;

public class BoardDeserializerSupplier implements Supplier<RandomDeserializer> {
    @Override
    public BoardDeserializer get() { return new BoardDeserializer(); }
}
