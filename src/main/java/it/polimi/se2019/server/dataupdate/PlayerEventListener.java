package it.polimi.se2019.server.dataupdate;

import it.polimi.se2019.server.games.PlayerDeath;

/**
 * This interface implements the Observer pattern but it support multiple events
 */
public interface PlayerEventListener {
    void onCharacterStateUpdate(CharacterStateUpdate characterStateUpdate);
    void onPlayerDeath(PlayerDeath playerDeath);
}
