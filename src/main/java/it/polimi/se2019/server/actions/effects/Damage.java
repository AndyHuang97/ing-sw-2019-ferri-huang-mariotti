package it.polimi.se2019.server.actions.effects;

public abstract class Damage implements Effect {

    protected Integer amount;
    protected String actionUnitName;

    protected Damage(Integer amount, String actionUnitName) {
        this.amount = amount;
        this.actionUnitName = actionUnitName;
    }

}
