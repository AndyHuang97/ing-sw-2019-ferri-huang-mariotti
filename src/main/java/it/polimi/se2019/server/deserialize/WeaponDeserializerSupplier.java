package it.polimi.se2019.server.deserialize;

import java.util.function.Supplier;

public class WeaponDeserializerSupplier implements Supplier<RandomDeserializer> {
    @Override
    public WeaponDeserializer get() {
        return new WeaponDeserializer();
    }
}
