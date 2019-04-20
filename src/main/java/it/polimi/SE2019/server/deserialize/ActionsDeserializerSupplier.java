package it.polimi.SE2019.server.deserialize;

import java.util.function.Supplier;

public class ActionsDeserializerSupplier implements Supplier<RandomDeserializer> {
    @Override
    public ActionsDeserializer get() {
        return new ActionsDeserializer();
    }
}
