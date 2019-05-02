package it.polimi.se2019.server.config;

public class Config implements DeserializerConfig {
    String actionsDeserializerName;
    String actionUnitDeserializerName;
    String boardDeserializerName;
    String conditionDeserializerName;
    String effectDeserializerName;
    String tileDeserializerName;
    String weaponDeserializerName;

    public void read(String filename) {
        // TODO: read configuration from file
    }


    @Override
    public String getActionsDeserializerName() {
        return actionsDeserializerName;
    }

    @Override
    public String getActionUnitDeserializerName() {
        return actionUnitDeserializerName;
    }

    @Override
    public String getBoardDeserializerName() {
        return boardDeserializerName;
    }

    @Override
    public String getConditionDeserializerName() {
        return conditionDeserializerName;
    }

    @Override
    public String getEffectDeserializerName() {
        return effectDeserializerName;
    }

    @Override
    public String getTileDeserializerName() {
        return tileDeserializerName;
    }

    @Override
    public String getWeaponDeserializerName() {
        return weaponDeserializerName;
    }
}
