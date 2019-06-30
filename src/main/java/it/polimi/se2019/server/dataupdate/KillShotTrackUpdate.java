package it.polimi.se2019.server.dataupdate;

import it.polimi.se2019.server.games.KillShotTrack;
import it.polimi.se2019.util.LocalModel;

public class KillShotTrackUpdate implements StateUpdate {
    KillShotTrack killShotTrack;

    public KillShotTrackUpdate(KillShotTrack killShotTrack) {
        this.killShotTrack = killShotTrack;
    }

    @Override
    public void updateData(LocalModel model) {
        model.setKillShotTrack(this.killShotTrack);
    }
}
