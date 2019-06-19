package it.polimi.se2019.server.dataupdate;

import it.polimi.se2019.client.View;
import it.polimi.se2019.client.gui.GUIView;
import it.polimi.se2019.server.exceptions.PlayerNotFoundException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.server.users.UserData;
import it.polimi.se2019.util.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CharacterStateUpdateTest {
    private static final String PLAYERNICK = "testPlayer";

    private Player player;
    private View view;

    @Before
    public void setUp() {
        // Initialize the view
        view = new GUIView();

        // Initialize Game and Player
        Game game = new Game();

        UserData userData = new UserData(PLAYERNICK);
        String id = PLAYERNICK;
        CharacterState characterState = new CharacterState();

        player = new Player(PLAYERNICK, false, userData, characterState, PlayerColor.BLUE);

        List<Player> playerList = new ArrayList<>();
        playerList.add(player);

        game.setPlayerList(playerList);

        view.getModel().setGame(game);
    }

    @Test
    public void testCharacterStateUpdate() throws PlayerNotFoundException {
        // Build a Response for a CharacterState update and send it to the view
        CharacterState updatedCharacterState = new CharacterState();
        updatedCharacterState.setScore(42);

        CharacterStateUpdate stateUpdate = new CharacterStateUpdate(updatedCharacterState);
        stateUpdate.setPlayer(player);

        List<StateUpdate> updateList = new ArrayList<>();
        updateList.add(stateUpdate);

        Response response = new Response(updateList);

        view.update(response);

        // Check if the CharacterState of the view has been updated
        Integer score = view.getModel().getGame().getPlayerByNickname(PLAYERNICK).getCharacterState().getScore();

        Assert.assertEquals((Integer) 42, score);
    }
}
