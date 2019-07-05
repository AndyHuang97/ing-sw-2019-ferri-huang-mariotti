package it.polimi.se2019.server.dataupdate;

import it.polimi.se2019.server.games.KillShotTrack;
import it.polimi.se2019.util.LocalModel;

/**
 *  Concrete implementation of the StateUpdate interface, this class should be sent to the
 *  views via Response to update their local currentPlayer.
 *
 * @author Andrea Huang
 */
public class KillShotTrackUpdate implements StateUpdate {
    KillShotTrack killShotTrack;

    /**
     * Builds a new KillShotTrackUpdate message. This message contains all the data that the view needs to update his
     * local model.
     *
     * @param killShotTrack reference to the new value of current player
     */
    public KillShotTrackUpdate(KillShotTrack killShotTrack) {
        this.killShotTrack = killShotTrack;
    }

    /**
     * This method must be run by the views (implements LocalModel) to update their local copy of the model.
     *
     * @param model view's local copy of the model that needs to be updated
     */
    @Override
    public void updateData(LocalModel model) {
        model.setKillShotTrack(this.killShotTrack);
    }
}
