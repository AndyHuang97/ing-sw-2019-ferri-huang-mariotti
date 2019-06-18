package it.polimi.se2019.client;

import it.polimi.se2019.server.actions.Action;
import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.dataupdate.StateUpdate;
import it.polimi.se2019.server.exceptions.PlayerNotFoundException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.LocalModel;
import it.polimi.se2019.util.Observable;
import it.polimi.se2019.util.Observer;
import it.polimi.se2019.util.Response;

import java.util.List;

public abstract class View extends Observable implements Observer<Response>, LocalModel {
    private Game game;

    public List<Action> getPossibleAction() {
        return null;
    }

    public List<ActionUnit> getPossibleAction(Action action) {
        return null;
    }

    @Override
    public void update(Response response) {
        List<StateUpdate> updateList = response.getUpdateData();

        for (StateUpdate stateUpdate : updateList) {
            stateUpdate.updateData(this);
        }
    }

    public abstract void getInput();

    public abstract void sendInput();

    @Override
    public void setCharacterState(CharacterState characterState, Player player) {
        String playerNickname = player.getUserData().getNickname();

        Player localPlayer;
        try {
            localPlayer = game.getPlayerByNickname(playerNickname);
            localPlayer.setCharacterState(characterState);
        } catch (PlayerNotFoundException e) {
            // Throw appropriate exception
        }
    }

    @Override
    public void setGame(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }
}
