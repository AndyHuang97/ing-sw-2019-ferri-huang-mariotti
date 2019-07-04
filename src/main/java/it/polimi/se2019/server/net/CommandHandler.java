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
 * The virtual view receives messages from the client via network, then it parses the message
 * and notifies the Controller.
 */
public class CommandHandler extends Observable<Request> implements Observer<Response> {

    private static final Logger logger = Logger.getLogger(CommandHandler.class.getName());

    List<StateUpdate> updateBuffer = new ArrayList<>();

    private boolean socketTrueRmiFalse;
    private SocketServer.ClientHandler socketClientHandler;
    private RmiClientInterface rmiClientWorker;
    private Boolean echo;
    private Boolean firstEcho = true;


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
                        if (!currentGame.getCurrentPlayer().getActive()) {
                            currentGame.setCurrentPlayer(currentGame.getPlayerByNickname(nickname));
                            ControllerState newControllerState = new WaitingForMainActions();
                            ServerApp.controller.setControllerStateForGame(currentGame, newControllerState);
                            newControllerState.sendSelectionMessage(this);
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
//                handleLocalRequest(request);

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
    }

    @Override
    public synchronized void update(Response response) throws CommunicationError {
        //System.out.println("update works");
        /**
         * Response on move done
         */
        //showMessage(response.serialize());

        /*
        String serializedResponse = response.serialize();
        if (!serializedResponse.equals("{\"success\":false,\"message\":\"ping\"}")){
            System.out.println(serializedResponse);
        }
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

    public void addUpdateToBuffer(StateUpdate stateUpdate) {
        updateBuffer.add(stateUpdate);
    }

    public void resetBuffer() {
        updateBuffer = new ArrayList<>();
    }

    public void sendBuffer() throws CommunicationError {
        //System.out.println("SENDIG THE UPDATEBUFFER!!!!!!!!!!!!!!!!!!!!");

        if (updateBuffer.size() != 0) {
            Response response = new Response(updateBuffer);
            resetBuffer();

            sendResponse(response);
        }
    }

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
        } catch (Exception e) {
            throw new CommunicationError(e.getMessage());
        }
    }

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