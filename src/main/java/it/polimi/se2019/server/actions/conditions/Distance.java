package it.polimi.se2019.server.actions.conditions;

public class Distance implements Condition {

    private Integer amount;

    public Distance(Integer amount) {
        this.amount = amount;
    }

    @Override
    public boolean check() {
        return false;
    }
}
