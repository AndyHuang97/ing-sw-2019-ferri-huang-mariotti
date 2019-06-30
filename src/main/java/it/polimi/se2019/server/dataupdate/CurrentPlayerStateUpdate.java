package it.polimi.se2019.server.dataupdate;

import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.LocalModel;

public class CurrentPlayerStateUpdate implements StateUpdate {
    private Player newCurrentPlayer;

    public CurrentPlayerStateUpdate(Player newCurrentPlayer) {
        this.newCurrentPlayer = newCurrentPlayer;
    }

    @Override
    public void updateData(LocalModel model) {
        model.getGame().setCurrentPlayer(newCurrentPlayer);
    }
}
