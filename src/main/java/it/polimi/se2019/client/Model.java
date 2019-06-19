package it.polimi.se2019.client;

import it.polimi.se2019.server.exceptions.PlayerNotFoundException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.LocalModel;

public class Model implements LocalModel {

    private Game game;

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
    @Override
    public Game getGame() {
        return game;
    }
}
