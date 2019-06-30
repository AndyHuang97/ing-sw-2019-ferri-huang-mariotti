package it.polimi.se2019.util;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.exceptions.IllegalPlayerActionException;
import it.polimi.se2019.server.exceptions.MessageParseException;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.GameManager;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.server.playerActions.CompositeAction;
import it.polimi.se2019.server.playerActions.MovePlayerAction;
import it.polimi.se2019.server.playerActions.PlayerAction;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class RequestParser {
    MessageParser messageParser = new MessageParser();
    List<PlayerAction> playerActionList;
    Player player;
    CommandHandler commandHandler;

    public void parse(Request request, Game game, Player player) throws GameManager.GameNotFoundException, MessageParseException, UnpackingException, IllegalPlayerActionException {

        playerActionList = messageParser.parse(request.getInternalMessage(), game, player);

        commandHandler = request.getCommandHandler();
    }

    public List<PlayerAction> getPlayerActionList() {
        return playerActionList;
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    public Player getPlayer() {
        return player;
    }

}
