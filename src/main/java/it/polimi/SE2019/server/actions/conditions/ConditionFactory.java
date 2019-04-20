package it.polimi.SE2019.server.actions.conditions;

import java.util.HashMap;

public class ConditionFactory {
    private HashMap<String, Class<? extends Condition>> availableConditions = new HashMap<>();

    public void addType(String type, Class<? extends Condition> conditionClass) {
        this.availableConditions.put(type, conditionClass);
    }

    public void getConditionClass(String type) {
        this.availableConditions.get(type);
    }
}
