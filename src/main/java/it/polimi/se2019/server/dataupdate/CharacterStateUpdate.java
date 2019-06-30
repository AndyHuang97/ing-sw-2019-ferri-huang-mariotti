package it.polimi.se2019.server.dataupdate;

import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.util.LocalModel;

/**
 * Concrete implementation of the StateUpdate interface, this class should be sent to the
 * views via Response to update their local CharacterState.
 */
public class CharacterStateUpdate implements StateUpdate {

    CharacterState characterState;

    public CharacterStateUpdate(CharacterState characterState) {
        this.characterState = characterState;
    }

    @Override
    public void updateData(LocalModel model) {
        model.setCharacterState(this.characterState);
    }
}
