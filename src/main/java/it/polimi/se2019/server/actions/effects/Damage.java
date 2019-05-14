package it.polimi.se2019.server.actions.effects;

public abstract class Damage implements Effect {

    protected Integer amount;

    protected Damage(Integer amount) {
        this.amount = amount;
    }

}
