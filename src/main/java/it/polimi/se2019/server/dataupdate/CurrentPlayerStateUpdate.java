package it.polimi.se2019.server.dataupdate;

import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.LocalModel;

/**
 *  Concrete implementation of the StateUpdate interface, this class should be sent to the
 *  views via Response to update their local currentPlayer.
 *
 * @author Andrea Huang
 */
public class CurrentPlayerStateUpdate implements StateUpdate {
    private Player newCurrentPlayer;

    /**
     * Builds a new AmmoCrateUpdate message. This message contains all the data that the view needs to update his
     * local model.
     *
     * @param newCurrentPlayer reference to the new value of current player
     */
    public CurrentPlayerStateUpdate(Player newCurrentPlayer) {
        this.newCurrentPlayer = newCurrentPlayer;
    }

    /**
     * This method must be run by the views (implements LocalModel) to update their local copy of the model.
     *
     * @param model view's local copy of the model that needs to be updated
     */
    @Override
    public void updateData(LocalModel model) {
        model.getGame().setCurrentPlayer(newCurrentPlayer);
    }
}
