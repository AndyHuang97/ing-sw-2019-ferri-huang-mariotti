package it.polimi.SE2019.server.deserialize;

import it.polimi.SE2019.server.actions.conditions.Condition;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ConditionSelector {
    private Map<String, Class<? extends Condition>> registeredClasses = new HashMap<>();

    public void registerCondition(String type, Class<? extends Condition> classType) {
        registeredClasses.put(type, classType);
    }

    public Class<? extends Condition> getCondition(String type) {
        return registeredClasses.get(type);
    }
}