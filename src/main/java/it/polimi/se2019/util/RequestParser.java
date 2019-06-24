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

    public void parse(Request request, GameManager gameManager) throws GameManager.GameNotFoundException, MessageParseException, UnpackingException, IllegalPlayerActionException {
        String nickname = request.getNickname();
        Game game = gameManager.retrieveGame(nickname);

        Optional<Player> optPlayer = game.getPlayerList().stream()
                .filter(p -> p.getUserData().getNickname().equals(nickname))
                .findFirst();

        if (optPlayer.isPresent()) {
            player = optPlayer.get();
            System.out.println(player);
        }

        playerActionList = messageParser.parse(request.getInternalMessage(), game, player);
        checkPlayerActionAvailability(playerActionList,game,player);

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

    public void checkPlayerActionAvailability(List<PlayerAction> playerActionList, Game game, Player player) throws IllegalPlayerActionException {
        List<CompositeAction> possibleActions = player.getCharacterState().getPossibleActions(game.isFrenzy());
        boolean result = possibleActions.stream()
                .filter(composite ->
                        composite.getAction().stream()
                        .map(Targetable::getId)
                        .collect(Collectors.toList())
                        .containsAll
                                (playerActionList.stream()
                                .map(Targetable::getId)
                                .collect(Collectors.toList())))
                .anyMatch(composite -> {
                    Supplier<Stream<PlayerAction>> supplier = () ->
                            composite.getAction().stream()
                                    .filter(possiblePlayerAction -> possiblePlayerAction.getId().equals(Constants.MOVE));
                    if (supplier.get().count()==0) {
                        return true;
                    }
                    boolean res = supplier.get()
                            .anyMatch(possiblePlayerAction -> {
                                MovePlayerAction mpa = playerActionList.stream()
                                        .filter(playerAction -> playerAction.getId().equals(Constants.MOVE))
                                        .map(pa -> (MovePlayerAction) pa)
                                        .findFirst().orElseThrow(IllegalStateException::new);
                                System.out.println("possible:" + possiblePlayerAction.getAmount());
                                System.out.println("distance:" + game.getBoard().getTileTree()
                                        .distance(mpa.getPlayer().getCharacterState().getTile(), mpa.getMoveList().get(0)));
                                if (possiblePlayerAction.getAmount() <
                                        game.getBoard().getTileTree()
                                                .distance(mpa.getPlayer().getCharacterState().getTile(), mpa.getMoveList().get(0))) {
                                    return false;
                                }
                                return true;
                            });
                    System.out.println(res);
                    return res;
                }
        );
        if (!result) {
            throw new IllegalPlayerActionException();
        }
    }
}
