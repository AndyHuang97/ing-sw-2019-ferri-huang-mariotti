package it.polimi.se2019.server.net;
import it.polimi.se2019.server.ServerApp;
import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.cards.ammocrate.AmmoCrate;
import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.exceptions.PlayerNotFoundException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.GameManager;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.net.socket.SocketServer;
import it.polimi.se2019.server.playerActions.PlayerAction;
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
                if (newValues.size()>1) {
                    Logger.getGlobal().info(newValues.get(0).getId());
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
                //converts the KeyOrder
                PlayerAction playerActionTarget = PlayerAction.getAllPossibleActions().stream()
                        .filter(playerAction -> playerAction.getId().equals(value)).findAny().orElse(null);
                if (playerActionTarget != null) {
                    newValues.add(playerActionTarget);
                    return;
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
                // TODO: process command, notify(request)?
                Logger.getGlobal().info("Notifying request to the controller: " + request.getNickname());
                handleLocalRequest(request);

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