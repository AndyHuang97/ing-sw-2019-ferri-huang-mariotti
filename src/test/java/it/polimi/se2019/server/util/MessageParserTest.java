package it.polimi.se2019.server.util;

import it.polimi.se2019.server.exceptions.MessageParseException;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.NormalTile;
import it.polimi.se2019.server.games.board.RoomColor;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.playerActions.MovePlayerAction;
import it.polimi.se2019.server.playerActions.PlayerAction;
import it.polimi.se2019.util.Message;
import it.polimi.se2019.util.MessageParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageParserTest {
    static final String className = "it.polimi.se2019.server.playerActions.MovePlayerAction";

    @Before
    public void setUp() {
    }

    @Test
    public void test() throws UnpackingException, MessageParseException {
        // init game and player
        List<Player> playerList = new ArrayList<>();
        Player player = new Player("testPlayer");
        playerList.add(player);

        Game game = new Game(playerList);

        // build a message
        List<Targetable> targetableList = new ArrayList<>();
        Tile tile = new NormalTile(RoomColor.BLUE, null, null);
        targetableList.add(tile);

        Map<String, List<Targetable>> command = new HashMap<>();
        command.put(className, targetableList);

        Message message = new Message(command);

        MessageParser messageParser = new MessageParser();

        List<PlayerAction> playerActionList = messageParser.parse(message, game, player);

        MovePlayerAction movePlayerAction = (MovePlayerAction) playerActionList.get(0);
        Tile parsedTile = movePlayerAction.getMoveList().get(0);

        Assert.assertSame(tile.getColor(), parsedTile.getColor());
    }

}