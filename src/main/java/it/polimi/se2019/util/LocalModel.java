package it.polimi.se2019.util;

import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.KillShotTrack;
import it.polimi.se2019.server.games.board.Board;
import it.polimi.se2019.server.games.player.CharacterState;

/**
 * The local model interface is used to build the model storage on top of it
 *
 * @author AH
 *
 */
public interface LocalModel {
    /**
     * Setter of the new character state
     *
     * @param characterState the new characterstate
     *
     */
    void setCharacterState(CharacterState characterState);

    /**
     * Setter of the new game
     *
     * @param game the game to be set
     *
     */
    void setGame(Game game);

    /**
     * Setter of the new killshottracker
     *
     * @param killShotTrack the kst
     *
     */
    void setKillShotTrack(KillShotTrack killShotTrack);

    /**
     * Setter of the new player weapon
     *
     * @param weaponToUpdate the weapon
     *
     */
    void updatePlayerWeapon(Weapon weaponToUpdate);

    /**
     * Getter of the board
     *
     */
    Board getBoard();

    /**
     * getter of the game
     *
     */
    Game getGame();
}