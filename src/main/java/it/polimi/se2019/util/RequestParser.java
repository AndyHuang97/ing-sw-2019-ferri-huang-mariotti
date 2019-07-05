package it.polimi.se2019.util;

import it.polimi.se2019.server.exceptions.MessageParseException;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.server.playeractions.PlayerAction;

import java.util.List;

/**
 * The RequestParser class is used to parse a request message from the server, by parsing the internal message
 * of a request. The internal message is a conversion of a string value to a matching object on the server.
 *
 * @author andreahuang
 *
 */
public class RequestParser {
    MessageParser messageParser = new MessageParser();
    List<PlayerAction> playerActionList;
    Player player;
    CommandHandler commandHandler;

    /**
     * The parse method pass the request message to the message parser and saves the command handler of the request.
     *
     * @param request the request coming from the client.
     * @param game the game on which to build the message.
     * @param player the player on which to build the message.
     * @throws MessageParseException if parsing of the request failed.
     * @throws UnpackingException if an unpacking failed.
     */
    public void parse(Request request, Game game, Player player) throws MessageParseException, UnpackingException {

        playerActionList = messageParser.parse(request.getInternalMessage(), game, player);

        commandHandler = request.getCommandHandler();
    }

    /**
     * Getter for the playerActionList.
     *
     * @return list of player actions.
     */
    public List<PlayerAction> getPlayerActionList() {
        return playerActionList;
    }

    /**
     * Getter for the request's commandHandler.
     *
     * @return the request's commandHandler
     */
    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    /**
     * Getter for the request player.
     *
     * @return the request player.
     */
    public Player getPlayer() {
        return player;
    }

}
