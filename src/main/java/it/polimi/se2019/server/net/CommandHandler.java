package it.polimi.se2019.server.net;
import it.polimi.se2019.server.ServerApp;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.GameManager;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.net.socket.SocketServer;
import it.polimi.se2019.server.users.UserData;
import it.polimi.se2019.util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The CommandHandler act as a virtual view.
 * The virtual view receives messages from the client via network, then it parses the message
 * and notifies the Controller.
 */
public class CommandHandler extends Observable<Request> implements Observer<Response> {

    private static final Logger logger = Logger.getLogger(CommandHandler.class.getName());

    private boolean socketTrueRmiFalse;
    private SocketServer.ClientHandler socketClientHandler;
    private RmiClientInterface rmiClientWorker;


    // just for tests
    public CommandHandler() {
    }

    public CommandHandler(SocketServer.ClientHandler clientHandler) {
        this.socketTrueRmiFalse = true;
        this.socketClientHandler = clientHandler;
    }

    public CommandHandler(RmiClientInterface clientWorker) {
        this.socketTrueRmiFalse = false;
        this.rmiClientWorker = clientWorker;
    }

    public class TargetableNotFoundException extends RuntimeException {
        public TargetableNotFoundException(String errorMessage) {
            super(errorMessage);
        }
    }

    public InternalMessage convertNetMessage(NetMessage netMessage, Game game) throws TargetableNotFoundException {
        Map<String, List<Targetable>> newCommands = new HashMap<>();
        List<Targetable> newValues = new ArrayList<>();
        netMessage.getCommands().forEach((key, values) -> {
            values.forEach((value) -> {
                Player playerTarget = game.getPlayerList().stream().filter(player -> player.getId().equals(value)).findAny().orElse(null);
                if (playerTarget != null) {
                    newValues.add(playerTarget);
                    return;
                }
                Tile tileTarget = game.getBoard().getTileList().stream().filter(tile -> tile.getId().equals(value)).findAny().orElse(null);
                if (tileTarget != null) {
                    newValues.add(tileTarget);
                    return;
                }
                //TODO handle cards and others...
                throw new TargetableNotFoundException("Cannot find a targetable with id: " + value);
            });
            newCommands.put(key, newValues);
        });
        return new InternalMessage(newCommands);
    }

    public synchronized void handle(Request request) {
        // log request
        NetMessage message = request.getNetMessage();
        String nickname = request.getNickname();
        try {
            Game game = ServerApp.gameManager.retrieveGame(nickname);
            request.setInternalMessage(convertNetMessage(message, game));
        } catch (GameManager.GameNotFoundException | TargetableNotFoundException e1) {
            try {
                logger.info(nickname);
                ServerApp.gameManager.addUserToWaitingList(new UserData(nickname), this);
            } catch (GameManager.AlreadyPlayingException e2) {
                logger.info("User " + nickname + " tried to join multiple times");
            }
        }
    }

    public synchronized void handleLocalRequest(Request request) {
        request.setCommandHandler(this);

        // here it needs to parse the message!
        notify(request);

        // TODO update shouldn't be called from here, but from the Model with its notify, and it should receive a Response
        //update(new Response(new Game(), true, request.getNickname()));

        // TODO: refactor, some tests do not use ServerApp
        try {
            ServerApp.gameManager.dumpToFile();
        } catch (NullPointerException e) {
            logger.info("Tried to dump game to file but failed");
        }
    }

    @Override
    public void update(Response response) {
        //System.out.println("update works");
        /**
         * Response on move done
         */
        //showMessage(response.serialize());
        try {
            if (this.socketTrueRmiFalse) {
                socketClientHandler.send(response.serialize());
            } else {
                rmiClientWorker.send(response.serialize());
            }
        } catch (Exception e) {
            logger.info(e.getLocalizedMessage());
        }

    }

    public void reportError(ErrorResponse errorResponse) {
        /**
         * print error message
         */
    }
}