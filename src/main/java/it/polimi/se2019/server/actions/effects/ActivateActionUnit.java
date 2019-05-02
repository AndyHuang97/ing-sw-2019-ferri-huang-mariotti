package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.actions.ActionUnit;

public class ActivateActionUnit implements Effect {

    private ActionUnit actionUnit;

    public ActivateActionUnit(ActionUnit actionUnit) {
        this.actionUnit = actionUnit;
    }

    @Override
    public void run() {

    }
}
