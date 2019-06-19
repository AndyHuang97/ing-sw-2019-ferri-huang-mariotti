package it.polimi.se2019.client;

import it.polimi.se2019.server.actions.Action;
import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.dataupdate.StateUpdate;
import it.polimi.se2019.util.LocalModel;
import it.polimi.se2019.util.Observable;
import it.polimi.se2019.util.Observer;
import it.polimi.se2019.util.Response;

import java.util.List;

public abstract class View extends Observable implements Observer<Response> {

    private LocalModel model;

    public View() {
        model = new Model();
    }

    public List<Action> getPossibleAction() {
        return null;
    }

    public List<ActionUnit> getPossibleAction(Action action) {
        return null;
    }

    @Override
    public void update(Response response) {
        List<StateUpdate> updateList = response.getUpdateData();

        for (StateUpdate stateUpdate : updateList) {
            stateUpdate.updateData(model);
        }
    }

    /**
     * The getInput method gets the input from the player
     */
    public abstract void getInput();

    /**
     * The sendInput method sends an input to the
     */
    public abstract void sendInput();

    public LocalModel getModel() {
        return model;
    }


    /**
     * The showMessage method shows a message received as response from the server
     * @param message a response message containing info on the performed action
     */
    public abstract void showMessage(String message);

    /**
     * The reportError methods shows an error message from the server when an invalid action is performed
     * @param error an error message conataining info about an action's violations
     */
    public abstract  void reportError(String error);

}
