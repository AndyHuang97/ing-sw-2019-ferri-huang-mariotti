package it.polimi.se2019.util;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.KillShotTrack;
import it.polimi.se2019.server.games.board.Board;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;

public interface LocalModel {
    void setCharacterState(CharacterState characterState, Player player);
    void setGame(Game game);
    void setKillShotTrack(KillShotTrack killShotTrack);

    Board getBoard();
    Game getGame();
}