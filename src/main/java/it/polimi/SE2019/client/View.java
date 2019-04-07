package it.polimi.SE2019.client;

import it.polimi.SE2019.server.actions.Action;
import it.polimi.SE2019.server.actions.ActionUnit;
import it.polimi.SE2019.util.Observable;
import it.polimi.SE2019.util.Observer;

import java.util.ArrayList;

public abstract class View extends Observable implements Observer {

    public ArrayList<Action> getPossibleAction() {
        return null;
    }

    public ArrayList<ActionUnit> getPosibleAction(Action action) {
        return null;
    }

    @Override
    public void update(Object message) {

    }
}
