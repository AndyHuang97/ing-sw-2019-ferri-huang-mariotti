package it.polimi.se2019.server.playeractions;

import java.util.ArrayList;
import java.util.List;

/**
 * Container class, represents a collection of PlayerActions
 *
 * @author Andrea Huang
 */
public class CompositeAction {

    private List<PlayerAction> action = new ArrayList<>();

    /**
     * This constructor accepts an array of PlayerAction and builds a CompositeAction object.
     *
     * @param baseActions array of PlayerActions
     */
    public CompositeAction(PlayerAction... baseActions) {
        for (PlayerAction playerAction : baseActions) {
            action.add(playerAction);
        }
    }

    /**
     * This constructor accepts a List of PlayerAction and builds a CompositeAction object.
     *
     * @param action list of PlayerAction that will be added to the new CompositeAction object
     */
    public CompositeAction(List<PlayerAction> action) {
        this.action = action;
    }

    /**
     * Get all the PlayerAction objects contained in the CompositeAction as List.
     *
     * @return PlayerAction objects contained in the CompositeAction as list
     */
    public List<PlayerAction> getAction() {
        return action;
    }

}
