package it.polimi.se2019.server.games;

import java.io.Serializable;

/**
 * This interface is implemented by every object that should be passed as argument to the various PlayerAction
 * and more in general by any object that should be selected by the View and sent to the Controller.
 *
 * @author Rodolfo Mariotti
 */
public interface Targetable extends Serializable {
    /**
     * Gets an unique ID for the current object.
     *
     * @return unique ID of the this object
     */
    String getId();
}
