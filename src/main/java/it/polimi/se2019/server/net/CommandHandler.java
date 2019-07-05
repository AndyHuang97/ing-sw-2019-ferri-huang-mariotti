package it.polimi.se2019.server.net;
import it.polimi.se2019.server.ServerApp;
import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.cards.ammocrate.AmmoCrate;
import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.controller.ControllerState;
import it.polimi.se2019.server.controller.WaitingForMainActions;
import it.polimi.se2019.server.dataupdate.StateUpdate;
import it.polimi.se2019.server.exceptions.PlayerNotFoundException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.GameManager;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.AmmoColor;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.net.socket.SocketServer;
import it.polimi.se2019.server.playeractions.PlayerAction;
import it.polimi.se2019.server.users.UserData;
import it.polimi.se2019.util.*;
import it.polimi.se2019.util.Observable;
import it.polimi.se2019.util.Observer;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * The CommandHandler act as a virtual view.
 * The command handler is in charge to wrap the network, it provides methods when we receive a message and to send a message
 *
 * @author FF
 *
 */
public class CommandHandler extends Observable<Request> implements Observer<Response> {

    private static final Logger logger = Logger.getLogger(CommandHandler.class.getName());

    List<StateUpdate> updateBuffer = new ArrayList<>();

    private boolean socketTrueRmiFalse;
    private SocketServer.ClientHandler socketClientHandler;
    private RmiClientInterface rmiClientWorker;
    private Boolean echo;
    private Boolean firstEcho = true;

    /**
     * Just a constructor
     *
     */
    public CommandHandler() {
    }

    /**
     * The constructor used by the socket
     *
     * @param clientHandler the send to client via socket
     *
     */
    public CommandHandler(SocketServer.ClientHandler clientHandler) {
        this.socketTrueRmiFalse = true;
        this.socketClientHandler = clientHandler;
    }

    /**
     * The constructor used by the rmi
     *
     * @param clientWorker the rmi client stub
     *
     */
    public CommandHandler(RmiClientInterface clientWorker) {
        this.socketTrueRmiFalse = false;
        this.rmiClientWorker = clientWorker;
    }

    /**
     * Not found exception, could be thrown when we convert a reference message to a real object message
     *
     */
    public class TargetableNotFoundException extends RuntimeException {
        public TargetableNotFoundException(String errorMessage) {
            super(errorMessage);
        }
    }

    /**
     * This method is used to convert an id to the object reference it refers. It needs to cycle through all the possible
     * objects of the game to find it. Il looks for players, tiles, ammocrates, powerup or weapons.
     *
     * @param netMessage the unconverted message
     * @param game the game
     * @return the converted message
     * @throws TargetableNotFoundException if it does not find a target
     *
     */
    public InternalMessage convertNetMessage(NetMessage netMessage, Game game) throws TargetableNotFoundException {
        Map<String, List<Targetable>> newCommands = new HashMap<>();
        netMessage.getCommands().forEach((key, values) -> {
            List<Targetable> newValues = new ArrayList<>(); // moved it inside the for each, must be a new reference each time
            values.forEach((value) -> {
                Logger.getGlobal().info("Looking for matching object to: "+value);
                int oldSize = newValues.size();
                Player playerTarget = game.getPlayerList().stream().filter(player -> player.getId().equals(value)).findAny().orElse(null);
                if (playerTarget != null) {
                    newValues.add(playerTarget);
                    return;
                }
                Tile tileTarget = game.getBoard().getTileList().stream()
                        .filter(Objects::nonNull)
                        .filter(tile -> tile.getId().equals(value)).findAny().orElse(null);
                if (tileTarget != null) {
                    newValues.add(tileTarget);
                    return;
                }
                AmmoCrate ammoCrateTarget = game.getBoard().getTileList().stream()
                        .filter(Objects::nonNull)
                        .filter(tile -> !tile.isSpawnTile())
                        .map(tile -> tile.getAmmoCrate())
                        .filter(Objects::nonNull)
                        .filter(ammoCrate -> ammoCrate.getId().equals(value)).findAny().orElse(null);
                if (ammoCrateTarget != null) {
                    newValues.add(ammoCrateTarget);
                    return;
                }
                PowerUp powerUpTarget = game.getCurrentPlayer().getCharacterState().getPowerUpBag().stream()
                        .filter(powerUp -> powerUp.getId().equals(value)).findAny().orElse(null);
                if (powerUpTarget != null) {
                    newValues.add(powerUpTarget);
                    return;
                }
                // tries to get the weapon from the player's hand
                Weapon weaponTarget = game.getCurrentPlayer().getCharacterState().getWeaponBag().stream()
                        .filter(weapon -> weapon.getId().equals(value)).findAny().orElse(null);
                if (weaponTarget != null) {
                    newValues.add(weaponTarget);
                    return;
                } else { // tries to get the weapon from a spwantile
                    weaponTarget = game.getBoard().getTileList().stream()
                            .filter(Objects::nonNull)
                            .filter(Tile::isSpawnTile)
                            .map(Tile::getWeaponCrate)
                            .map(weapons -> weapons.stream()
                                    .filter(Objects::nonNull)
                                    .filter(weapon -> weapon.getId().equals(value))
                                    .findAny().orElse(null))
                            .filter(Objects::nonNull)
                            .findAny().orElse(null);
                    if (weaponTarget != null) {
                        newValues.add(weaponTarget);
                        return;
                    }
                }
                // assuming that a weapon always precedes the action unit
                try {
                    if (!newValues.isEmpty()) {
                        ActionUnit actionUnitTarget = ((Weapon) newValues.get(0)).getActionUnitList().stream()
                                .filter(actionUnit -> actionUnit.getId().equals(value)).findAny().orElse(null);
                        if (actionUnitTarget != null) {
                            newValues.add(actionUnitTarget);
                            return;
                        } else {
                            actionUnitTarget = ((Weapon) newValues.get(0)).getOptionalEffectList().stream()
                                    .filter(actionUnit -> actionUnit.getId().equals(value)).findAny().orElse(null);
                            if (actionUnitTarget != null) {
                                newValues.add(actionUnitTarget);
                                return;
                            }
                        }
                    }
                } catch (ClassCastException e) {
                    //got to next value
                }
                //converts the KeyOrder
                PlayerAction playerActionTarget = PlayerAction.getAllPossibleActions().stream()
                        .filter(playerAction -> playerAction.getId().equals(value)).findAny().orElse(null);
                if (playerActionTarget != null) {
                    newValues.add(playerActionTarget);
                    return;
                }
                //converts the ammo color
                try {
                    AmmoColor ammoColorTarget = AmmoColor.valueOf(value);
                    newValues.add(ammoColorTarget);
                    return;
                }catch (IllegalArgumentException e) {

                }
                //did not find any corresponding value
                Logger.getGlobal().info(newCommands.toString());
                if (oldSize == newValues.size()) {
                    throw new TargetableNotFoundException("Cannot find a targetable with id: " + value);
                }
            });
            newCommands.put(key, newValues);
        });
        Logger.getGlobal().info(newCommands.toString());
        return new InternalMessage(newCommands);
    }

    /**
     * This handles the call from the client, in particular if a client is trying to connect (or reconnect) it puts him into the
     * waitinglist or reconnects him to the game. If a client is not connecting it could be a pong (that is managed here) or
     * a normale request that is processed by the controller
     *
     * @param request the request
     *
     */
    public synchronized void handle(Request request) {
        // log request
        NetMessage message = request.getNetMessage();
        String nickname = request.getNickname();
        if (message.getCommands().containsKey("pong")) {
            echo = true;
            firstEcho = true;
        } else if (message.getCommands().containsKey("connect")) {
            try {
                if (ServerApp.gameManager.isUserInGameList(nickname)) {
                    Game currentGame = ServerApp.gameManager.retrieveGame(nickname);
                    if (!currentGame.getPlayerByNickname(nickname).getCharacterState().isConnected()) {
                        logger.info("User " + nickname + " reconnected");
                        ServerApp.gameManager.startPingDaemon(nickname, this);
                        currentGame.register(this);
                        ServerApp.gameManager.getPlayerCommandHandlerMap().put(nickname, this);
                        this.register(ServerApp.controller);
                        currentGame.getPlayerByNickname(nickname).setActive(true);
                        try {
                            update(new Response(ServerApp.gameManager.retrieveGame(nickname), true, "welcome back"));
                        } catch (CommunicationError e) {
                            logger.info(e.getMessage());
                        }
                        //TODO turn handling on reconnection
                        if (currentGame.getActivePlayerList().size()<=3) { // if the game is still in set up state
                            if (!currentGame.getCurrentPlayer().getActive()) { // if not active give control to a new player
                                currentGame.setCurrentPlayer(currentGame.getPlayerByNickname(nickname));
                            }
                            if (currentGame.getActivePlayerList().size()==3) {
                                ControllerState newControllerState = new WaitingForMainActions();
                                ServerApp.controller.setControllerStateForGame(currentGame, newControllerState);
                                Logger.getGlobal().info("Sending "+newControllerState.getClass().getSimpleName()+" to "+currentGame.getCurrentPlayer().getUserData().getNickname());
                                ServerApp.controller.requestUpdate(currentGame);
                                newControllerState.sendSelectionMessage(ServerApp.gameManager.getPlayerCommandHandlerMap().get(currentGame.getCurrentPlayer().getUserData().getNickname()));
                            }
                        }
                    } else {
                        logger.info("User " + nickname + " already connected");
                    }
                } else if (!ServerApp.gameManager.isUserInWaitingList(nickname)) {
                    ServerApp.gameManager.addUserToWaitingList(new UserData(nickname), this);
                    ServerApp.gameManager.getMapPreference().add(message.getCommands().get("connect").get(0));
                } else {
                    logger.info("User " + nickname + " tried to join queue multiple times");
                }
            } catch (GameManager.GameNotFoundException | GameManager.AlreadyPlayingException | PlayerNotFoundException e) {
                logger.info(e.getMessage());
            }
        } else {
            try {
                Game game = ServerApp.gameManager.retrieveGame(nickname);
                request.setInternalMessage(convertNetMessage(message, game));
                Logger.getGlobal().info("Notifying request to the controller: " + request.getNickname());
                request.setCommandHandler(this);
                notify(request);
            } catch (GameManager.GameNotFoundException | TargetableNotFoundException e) {
                logger.info(e.getMessage());
            }
        }
    }

    /**
     * A method used to notify the controller, similar to the normal handle, but more stripped
     *
     * @param request the request
     *
     */
    public synchronized void handleLocalRequest(Request request) {
        request.setCommandHandler(this);
        notify(request);
    }

    /**
     * This is where you send a model update to a client, the model notify and this sends to the clients. It is buffered
     * in here to allow a better network management.
     *
     * @param response the response
     * @throws it.polimi.se2019.util.Observer.CommunicationError if the network has a problem
     *
     */
    @Override
    public synchronized void update(Response response) throws CommunicationError {
        /**
         * Response on move done
         */

        // buffered update
        if (response.getUpdateData() != null) {
            List<StateUpdate> stateUpdateList = response.getUpdateData();

            for (StateUpdate stateUpdate : stateUpdateList) {
                addUpdateToBuffer(stateUpdate);
            }
        } else {
            sendResponse(response);
        }

    }

    /**
     * The buffer adder
     *
     * @param stateUpdate the update
     *
     */
    public void addUpdateToBuffer(StateUpdate stateUpdate) {
        updateBuffer.add(stateUpdate);
    }

    /**
     * The buffer rest
     **
     */
    public void resetBuffer() {
        updateBuffer = new ArrayList<>();
    }

    /**
     * The buffer sender
     * @throws it.polimi.se2019.util.Observer.CommunicationError if the network has a problem
     *
     */
    public void sendBuffer() throws CommunicationError {
        if (!updateBuffer.isEmpty()) {
            Response response = new Response(updateBuffer);
            resetBuffer();

            sendResponse(response);
        }
    }

    /**
     * This is where we communicate to the client, we send a message to it. The rmi implementation uses threads to make
     * it non blocking. Disconnections are handled using the pinger daemon.
     *
     * @param response the response
     * @throws it.polimi.se2019.util.Observer.CommunicationError if the network has a problem
     *
     */
    public void sendResponse(Response response) throws CommunicationError {
        try {
            if (this.socketTrueRmiFalse) {
                socketClientHandler.send(response.serialize());
            } else {
                new Thread(() -> {
                    try {
                        rmiClientWorker.send(response.serialize());
                    } catch (Exception e) {
                        // do nothing
                    }
                }).start();
            }
            Thread.sleep(50);
        } catch (Exception e) {
            throw new CommunicationError(e.getMessage());
        }
    }

    /**
     * Just the ping sender and processing, sends and waits for response
     * @throws it.polimi.se2019.util.Observer.CommunicationError if the network has a problem
     */
    public void ping() throws CommunicationError {
        sendResponse(new Response(null, false, "ping"));
        long startTime = System.currentTimeMillis();
        echo = false;
        while (!echo && (System.currentTimeMillis() - startTime) < 1000 ) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                logger.info(ex.getMessage());
            }
        }
        if (!echo) {
            if (firstEcho) firstEcho = false;
            else throw new CommunicationError("Ping Timeout!");
        }
    }
}