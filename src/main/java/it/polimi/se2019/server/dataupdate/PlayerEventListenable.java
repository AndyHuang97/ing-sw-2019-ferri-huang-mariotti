package it.polimi.se2019.server.dataupdate;

import it.polimi.se2019.server.games.PlayerDeath;

import java.util.ArrayList;
import java.util.List;

public abstract class PlayerEventListenable {
    private List<PlayerEventListener> listeners = new ArrayList<>();

    public void register(PlayerEventListener listener){
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void deregister(PlayerEventListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    protected void notifyCharacterStateUpdate(CharacterStateUpdate update) {
        synchronized (listeners) {
            for (PlayerEventListener listener : listeners) {
                listener.onCharacterStateUpdate(update);
            }
        }
    }

    protected void notifyPlayerDeath(PlayerDeath playerDeath) {
        synchronized (listeners) {
            for (PlayerEventListener listener : listeners) {
                listener.onPlayerDeath(playerDeath);
            }
        }
    }

}