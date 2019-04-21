package it.polimi.se2019.server.deserialize;

import java.util.function.Supplier;

public class ActionUnitDeserializerSupplier implements Supplier<RandomDeserializer> {
    @Override
    public ActionUnitDeserializer get() {
        return new ActionUnitDeserializer();
    }
}
