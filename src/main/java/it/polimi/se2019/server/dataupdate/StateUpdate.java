package it.polimi.se2019.server.dataupdate;

import it.polimi.se2019.util.LocalModel;

/**
 * Interface for the update objects sent by the model to the view. The views will run the updateData() method
 * to update their local copy of the model.
 *
 * @author Rodolfo Mariotti
 */
public interface StateUpdate {
    /**
     * This method must be run by the views (implements LocalModel) to update their local copy of the model.
     *
     * @param model view's local copy of the model that needs to be updated
     */
    void updateData(LocalModel model);
}
