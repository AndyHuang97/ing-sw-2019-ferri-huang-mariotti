package it.polimi.se2019.server.deserialize;

import java.util.function.Supplier;

public class PowerUpDeserializerSupplier implements Supplier<RandomDeserializer> {
    @Override
    public PowerUpDeserializer get() {
        return new PowerUpDeserializer();
    }
}
