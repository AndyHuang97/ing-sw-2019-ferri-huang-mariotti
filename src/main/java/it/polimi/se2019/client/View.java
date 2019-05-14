package it.polimi.se2019.client;

import it.polimi.se2019.server.actions.Action;
import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.util.Observable;
import it.polimi.se2019.util.Observer;

import java.util.List;

public abstract class View extends Observable implements Observer {

    public List<Action> getPossibleAction() {
        return null;
    }

    public List<ActionUnit> getPossibleAction(Action action) {
        return null;
    }

    @Override
    public void update(Object message) {

    }

    public abstract void getInput();

    public abstract void sendInput();
}
