package it.polimi.se2019.server.dataupdate;

import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.util.LocalModel;

/**
 * Concrete implementation of the StateUpdate interface, this class should be sent to the
 * views via Response to update their local CharacterState.
 *
 * @author Andrea Huang
 */
public class CharacterStateUpdate implements StateUpdate {

    CharacterState characterState;

    /**
     * Builds a new CharacterStateUpdate message. This message contains all the data that the view needs to update his
     * local model.
     *
     * @param characterState reference to the update CharacterState object
     */
    public CharacterStateUpdate(CharacterState characterState) {
        this.characterState = characterState;
    }

    /**
     * This method must be run by the views (implements LocalModel) to update their local copy of the model.
     *
     * @param model view's local copy of the model that needs to be updated
     */
    @Override
    public void updateData(LocalModel model) {
        model.setCharacterState(this.characterState);
    }
}
