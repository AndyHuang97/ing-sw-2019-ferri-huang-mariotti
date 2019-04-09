package it.polimi.SE2019.server.deserialize;

import com.google.gson.JsonDeserializer;

import java.util.function.Supplier;

public class WeaponDeserializerSupplier implements Supplier<RandomDeserializer> {
    @Override
    public WeaponDeserializer get() {
        return new WeaponDeserializer();
    }
}
