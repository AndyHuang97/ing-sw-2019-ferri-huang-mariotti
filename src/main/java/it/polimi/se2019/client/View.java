package it.polimi.se2019.client;

import it.polimi.se2019.server.actions.Action;
import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.util.Observable;
import it.polimi.se2019.util.Observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class View extends Observable implements Observer {

    public ArrayList<Action> getPossibleAction() {
        return null;
    }

    public ArrayList<ActionUnit> getPossibleAction(Action action) {
        return null;
    }

    @Override
    public void update(Object message) {

    }
}
