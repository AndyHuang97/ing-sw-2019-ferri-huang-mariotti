package it.polimi.se2019.server.net;
import it.polimi.se2019.server.ServerApp;
import it.polimi.se2019.server.exceptions.PlayerNotFoundException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.GameManager;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.net.socket.SocketServer;
import it.polimi.se2019.server.users.UserData;
import it.polimi.se2019.util.*;
import it.polimi.se2019.util.Observable;
import it.polimi.se2019.util.Observer;

import java.util.*;
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
        netMessage.getCommands().forEach((key, values) -> {
            List<Targetable> newValues = new ArrayList<>(); // moved it inside the for each, must be a new reference each time
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
        if (message.getCommands().containsKey("connect")) {
            try {
                if (ServerApp.gameManager.isUserInGameList(nickname)) {
                    if (!ServerApp.gameManager.retrieveGame(nickname).getPlayerByNickname(nickname).getCharacterState().isConnected()) {
                        logger.info("User " + nickname + " reconnected");
                        try {
                            update(new Response(ServerApp.gameManager.retrieveGame(nickname), true, "welcome back"));
                        } catch (CommunicationError e) {
                            logger.info(e.getMessage());
                        }
                    } else {
                        logger.info("User " + nickname + " already connected");
                    }
                } else if (!ServerApp.gameManager.isUserInWaitingList(nickname)) {
                    ServerApp.gameManager.addUserToWaitingList(new UserData(nickname), this);
                } else {
                    logger.info("User " + nickname + " tried to join queue multiple times");
                }
            } catch (GameManager.GameNotFoundException | GameManager.AlreadyPlayingException | PlayerNotFoundException e) {
                logger.info(e.getMessage());
            }
        } else if (message.getCommands().containsKey("pong")) {
            // do nothing?
        } else {
            try {
                Game game = ServerApp.gameManager.retrieveGame(nickname);
                request.setInternalMessage(convertNetMessage(message, game));
                // TODO: process command

            } catch (GameManager.GameNotFoundException | TargetableNotFoundException e) {
                logger.info(e.getMessage());
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
    public synchronized void update(Response response) throws CommunicationError {
        //System.out.println("update works");
        /**
         * Response on move done
         */
        //showMessage(response.serialize());
        try {
            if (this.socketTrueRmiFalse) {
                socketClientHandler.send(response.serialize());
                if (response.getMessage().equals("ping")) {

                }
            } else {
                rmiClientWorker.send(response.serialize());
            }
        } catch (Exception e) {
            throw new CommunicationError(e.getMessage());
        }
    }

    public void reportError(ErrorResponse errorResponse) {
        /**
         * print error message
         */
    }
}