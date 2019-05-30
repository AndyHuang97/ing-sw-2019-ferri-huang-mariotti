package it.polimi.se2019.util;

import it.polimi.se2019.server.exceptions.MessageParseException;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.GameManager;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.server.playerActions.PlayerAction;

import java.util.List;
import java.util.Optional;


public class RequestParser {
    MessageParser messageParser = new MessageParser();
    List<PlayerAction> playerActionList;
    Player player;
    CommandHandler commandHandler;

    public void parse(Request request, GameManager gameManager) throws GameManager.GameNotFoundException, MessageParseException, UnpackingException {
        String nickname = request.getNickname();
        Game game = gameManager.retrieveGame(nickname);

        System.out.println(game.getPlayerList());

        Optional<Player> optPlayer = game.getPlayerList().stream()
                .filter(p -> p.getUserData().getNickname().equals(nickname))
                .findFirst();

        if (optPlayer.isPresent()) {
            player = optPlayer.get();
            System.out.println(player);
        }

        playerActionList = messageParser.parse(request.getMessage(), game, player);

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
