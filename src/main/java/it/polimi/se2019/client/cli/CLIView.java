package it.polimi.se2019.client.cli;

import it.polimi.se2019.client.View;
import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.client.util.Util;
import it.polimi.se2019.server.actions.Action;
import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.exceptions.PlayerNotFoundException;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.AmmoColor;
import it.polimi.se2019.server.games.player.Player;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CLIView extends View {
    private static final Logger logger = Logger.getLogger(CLIView.class.getName());
    CLIUtil utils = new CLIUtil();


    public CLIView() {
        this.setCliTrueGuiFalse(true);
    }

    @Override
    public void askInput() {

    }

    public void sendNOP() {
        getPlayerInput().put(Constants.NOP, new ArrayList<>());
        getPlayerInput().put(Constants.KEY_ORDER, Arrays.asList(Constants.NOP));
        sendInput();
    }



    @Override
    @SuppressWarnings("Duplicates")
    public void showMessage(String message) {
        try {
            Player currentPlayer = getModel().getGame().getPlayerByNickname(getNickname());
            final List<String> validTiles = getModel().getGame().getBoard().getTileList().stream().filter(Objects::nonNull).map(tile -> tile.getId()).collect(Collectors.toList());
            switch (message) {
                case Constants.RELOAD:
                    List<String> selectedReloadWeapons = new ArrayList<>();
                    List<String> validReloadWeapons = currentPlayer.getCharacterState().getWeaponBag().stream().filter(w -> !w.isLoaded()).map(w -> w.getName()).collect(Collectors.toList());
                    validReloadWeapons.forEach(wn -> {
                        String toBeReloaded = utils.askUserInput("Do you want to reload " + wn, Arrays.asList("Y", "N"), false);
                        if (toBeReloaded.equals(Constants.NOP)) {
                            sendNOP();
                            return;
                        }
                        if (toBeReloaded.equals("Y")) selectedReloadWeapons.add(wn);
                    });
                    getPlayerInput().put(Constants.RELOAD, selectedReloadWeapons);
                    getPlayerInput().put(Constants.KEY_ORDER, Arrays.asList(Constants.RELOAD));
                    sendInput();
                    break;
                case Constants.SHOOT:
                    List<String> shootList = new LinkedList<>();
                    Weapon currentWeapon = getModel().getGame().getCurrentWeapon();
                    shootList.add(currentWeapon.getName());
                    List<ActionUnit> actionUnits = currentWeapon.getActionUnitList().stream().filter(au -> !getModel().getGame().getCurrentActionUnitsList().contains(au)).collect(Collectors.toList());
                    actionUnits.addAll(currentWeapon.getOptionalEffectList().stream().filter(au -> !getModel().getGame().getCurrentActionUnitsList().contains(au)).collect(Collectors.toList()));
                    List<String> validActionUnits = actionUnits.stream().map(au -> au.getName()).collect(Collectors.toList());
                    validActionUnits.add("n");
                    String selectedActionUnit = utils.askUserInput("Which effect do you want to use", validActionUnits, true);
                    if (selectedActionUnit.equals(Constants.NOP) || selectedActionUnit.equals("n")) {
                        sendNOP();
                        return;
                    }
                    shootList.add(selectedActionUnit);
                    ActionUnit actionUnit;
                    try {
                        actionUnit = actionUnits.stream().filter(au -> au.getName().equals(selectedActionUnit)).findAny().orElseThrow(() -> new ClassNotFoundException("No action unit found"));
                    } catch (ClassNotFoundException ex) {
                        sendNOP();
                        return;
                    }
                    Map<String, String> shootPlayers = new HashMap<>();
                    getModel().getGame().getPlayerList().forEach(p -> {
                        if (!p.getId().equals(currentPlayer.getId())) shootPlayers.put(p.getUserData().getNickname(), p.getId());
                    });
                    List<String> validShootTiles = new ArrayList<>(validTiles);
                    if (actionUnit.isUnidirectional()) {
                        int x = currentPlayer.getCharacterState().getTile().getxPosition();
                        int y = currentPlayer.getCharacterState().getTile().getyPosition();
                        validShootTiles = new ArrayList<>(getModel().getGame().getBoard().getTileList().stream().filter(Objects::nonNull).filter(t -> (t.getxPosition() == x || t.getyPosition() == y) && !t.getId().equals(getModel().getGame().getBoard().getTile(x, y).getId())).map(t -> t.getId()).collect(Collectors.toList()));
                    }
                    int[] targetsSize = new int[] {actionUnit.getNumPlayerTargets(), actionUnit.getNumTileTargets()};
                    String[] messages = new String[] {"Select a player #%d to target", "Select tile #%d to target"};
                    List<String>[] answers = new List[] {new ArrayList<>(shootPlayers.keySet()), validShootTiles};
                    boolean[] type = new boolean[] {true, false};
                    shootPlayers.put("n", "n");
                    int start;
                    if (actionUnit.isPlayerSelectionFirst()) start = 0;
                    else start = -1;
                    for (int j = start; j < start + 2; j++) {
                        for (int i = 0; i < targetsSize[Math.abs(j)]; i++) {
                            if ((j != start || i != 0) && !answers[Math.abs(j)].contains("n")) {
                                answers[Math.abs(j)].add("n");
                            }
                            if (answers[Math.abs(j)].isEmpty()) {
                                sendNOP();
                                return;
                            }
                            String selectedTarget = utils.askUserInput(String.format(messages[Math.abs(j)], i), answers[Math.abs(j)], type[Math.abs(j)]);
                            if (selectedTarget.equals(Constants.NOP)) {
                                sendNOP();
                                return;
                            }
                            if (!selectedTarget.equals("n")) {
                                if (type[Math.abs(j)]) shootList.add(shootPlayers.get(selectedTarget));
                                else shootList.add(selectedTarget);
                            }
                        }
                    }
                    getPlayerInput().put(Constants.SHOOT, shootList);
                    getPlayerInput().put(Constants.KEY_ORDER, Arrays.asList(Constants.SHOOT));
                    sendInput();
                    break;
                case Constants.MAIN_ACTION:
                    Map<String, List<String>> possibleActions = new HashMap<>();
                    if (getModel().getGame().isFrenzy()) {
                        if (currentPlayer.getCharacterState().isBeforeFrenzyActivator()) {
                            possibleActions.put(">↺\uD83D\uDF8B", Arrays.asList(Constants.MOVE, Constants.RELOAD, Constants.SHOOT_WEAPON));
                            possibleActions.put(">>>>", Arrays.asList(Constants.MOVE));
                            possibleActions.put(">>✋", Arrays.asList(Constants.MOVE, Constants.GRAB));
                        } else {
                            possibleActions.put(">>↺\uD83D\uDF8B", Arrays.asList(Constants.MOVE, Constants.RELOAD, Constants.SHOOT_WEAPON));
                            possibleActions.put(">>>✋", Arrays.asList(Constants.MOVE, Constants.GRAB));
                        }
                    } else {
                        possibleActions.put(">>>", Arrays.asList(Constants.MOVE));
                        possibleActions.put(">✋", Arrays.asList(Constants.MOVE, Constants.GRAB));
                        possibleActions.put("\uD83D\uDF8B", Arrays.asList(Constants.SHOOT_WEAPON));
                        if (currentPlayer.getCharacterState().getDeaths() > 2)
                            possibleActions.put(">>✋", Arrays.asList(Constants.MOVE, Constants.GRAB));
                        if (currentPlayer.getCharacterState().getDeaths() > 5)
                            possibleActions.put(">\uD83D\uDF8B", Arrays.asList(Constants.MOVE, Constants.SHOOT_WEAPON));
                    }
                    if (currentPlayer.getCharacterState().getPowerUpBag().stream().anyMatch(up -> up.getName().contains("Newton") || up.getName().contains("Teleporter"))) possibleActions.put("PowerUp", Arrays.asList(Constants.POWERUP));
                    possibleActions.put("Skip", Arrays.asList(Constants.NOP));
                    String actionInput = utils.askUserInput("Pick an action", new ArrayList<>(possibleActions.keySet()), true);
                    if (actionInput.equals(Constants.NOP)) {
                        sendNOP();
                        return;
                    }
                    int[] virtualTileCoords = new int[] {currentPlayer.getCharacterState().getTile().getxPosition(), currentPlayer.getCharacterState().getTile().getyPosition()};
                    List<String> doneActions = new ArrayList<>();
                    possibleActions.get(actionInput).forEach(action -> {
                        switch (action) {
                            case Constants.MOVE:
                                List<String> validMoveTiles = new ArrayList<>(validTiles);
                                validMoveTiles.add("n");
                                String selectedMoveTile = utils.askUserInput("Pick a tile or n not to move", validMoveTiles, false);
                                if (selectedMoveTile.equals(Constants.NOP)) {
                                    sendNOP();
                                    return;
                                }
                                if (selectedMoveTile.equals("n")) break;
                                try {
                                    int[] coords = Util.convertToCoords(Integer.parseInt(selectedMoveTile));
                                    virtualTileCoords[0] = coords[0];
                                    virtualTileCoords[1] = coords[1];
                                } catch (Exception ex) {
                                    logger.info("invalid virtual tile");
                                }
                                doneActions.add(Constants.MOVE);
                                getPlayerInput().put(Constants.MOVE, Arrays.asList(selectedMoveTile));
                                break;
                            case Constants.RELOAD:
                                List<String> selectedReloadWeaponsMA = new ArrayList<>();
                                List<String> validReloadWeaponsMA = currentPlayer.getCharacterState().getWeaponBag().stream().filter(w -> !w.isLoaded()).map(w -> w.getName()).collect(Collectors.toList());
                                validReloadWeaponsMA.forEach(wn -> {
                                    String toBeReloaded = utils.askUserInput("Do you want to reload " + wn, Arrays.asList("Y", "N"), false);
                                    if (toBeReloaded.equals(Constants.NOP)) {
                                        sendNOP();
                                        return;
                                    }
                                    if (toBeReloaded.equals("Y")) selectedReloadWeaponsMA.add(wn);
                                });
                                if (selectedReloadWeaponsMA.isEmpty()) break;
                                doneActions.add(Constants.RELOAD);
                                getPlayerInput().put(Constants.RELOAD, selectedReloadWeaponsMA);
                                break;
                            case Constants.SHOOT_WEAPON:
                                Map<String, Weapon> weapons = new HashMap<>();
                                currentPlayer.getCharacterState().getWeaponBag().forEach(w -> {
                                    weapons.put(w.getName(), w);
                                });
                                List<String> validShootWeapons = currentPlayer.getCharacterState().getWeaponBag().stream().map(w -> w.getName()).collect(Collectors.toList());
                                validShootWeapons.add("n");
                                String selectedShootWeapon = utils.askUserInput("Pick a weapon to use or n not to use any", validShootWeapons, true);
                                if (selectedShootWeapon.equals(Constants.NOP)) {
                                    sendNOP();
                                    return;
                                }
                                if (selectedShootWeapon.equals("n")) break;
                                doneActions.add(Constants.SHOOT_WEAPON);
                                getPlayerInput().put(Constants.SHOOT_WEAPON, Arrays.asList(selectedShootWeapon));
                                break;
                            case Constants.GRAB:
                                Tile virtualTile = getModel().getGame().getBoard().getTile(virtualTileCoords[0], virtualTileCoords[1]);
                                if (!virtualTile.getWeaponCrate().isEmpty()) {
                                    List<String> grabSwapWeapons = new ArrayList<>();
                                    String selectedWeaponGrab = utils.askUserInput("Choose a Weapon to grab", virtualTile.getWeaponCrate().stream().map(w -> w.getName()).collect(Collectors.toList()), true);
                                    if (selectedWeaponGrab.equals(Constants.NOP)) {
                                        sendNOP();
                                        return;
                                    }
                                    grabSwapWeapons.add(selectedWeaponGrab);
                                    if (currentPlayer.getCharacterState().getWeaponBag().size() >= 3) {
                                        String selectedWeaponSwap = utils.askUserInput("Choose a Weapon to discard", currentPlayer.getCharacterState().getWeaponBag().stream().map(w -> w.getName()).collect(Collectors.toList()), true);
                                        if (selectedWeaponSwap.equals(Constants.NOP)) {
                                            sendNOP();
                                            return;
                                        }
                                        grabSwapWeapons.add(selectedWeaponSwap);
                                    }
                                    doneActions.add(Constants.GRAB);
                                    getPlayerInput().put(Constants.GRAB, grabSwapWeapons);
                                } else if (virtualTile.getAmmoCrate() != null) {
                                    doneActions.add(Constants.GRAB);
                                    getPlayerInput().put(Constants.GRAB, Arrays.asList(virtualTile.getAmmoCrate().getName()));
                                }
                                break;
                            case Constants.POWERUP:
                                Map<String, Player> newtonPlayers = new HashMap<>();
                                getModel().getGame().getPlayerList().forEach(p -> {
                                    if (p.getCharacterState().getTile() != null) {
                                        int x = p.getCharacterState().getTile().getxPosition();
                                        int y = p.getCharacterState().getTile().getyPosition();
                                        List<String> tmpValidNewtonTiles = new ArrayList<>(getModel().getGame().getBoard().getTileList().stream().filter(Objects::nonNull).filter(t -> (t.getxPosition() == x || t.getyPosition() == y) && !t.getId().equals(getModel().getGame().getBoard().getTile(x, y).getId())).map(t -> t.getId()).collect(Collectors.toList()));
                                        if (!p.getId().equals(currentPlayer.getId()) && !tmpValidNewtonTiles.isEmpty()) newtonPlayers.put(p.getUserData().getNickname(), p);
                                    }
                                });
                                List<String> validPowerUps = currentPlayer.getCharacterState().getPowerUpBag().stream().filter(up -> (up.getName().contains("Newton") && !newtonPlayers.isEmpty()) || up.getName().contains("Teleporter")).map(up -> up.getName()).collect(Collectors.toList());
                                String selectedPowerUp = utils.askUserInput("Choose the powerUp to use", validPowerUps, true);
                                if (selectedPowerUp.equals(Constants.NOP)) {
                                    sendNOP();
                                    return;
                                }
                                doneActions.add(Constants.POWERUP);
                                if (selectedPowerUp.contains("Newton")) {
                                    String selectedNewtonPlayer = utils.askUserInput("Select a player to newton", new ArrayList<>(newtonPlayers.keySet()), true);
                                    if (selectedNewtonPlayer.equals(Constants.NOP)) {
                                        sendNOP();
                                        return;
                                    }
                                    int x = newtonPlayers.get(selectedNewtonPlayer).getCharacterState().getTile().getxPosition();
                                    int y = newtonPlayers.get(selectedNewtonPlayer).getCharacterState().getTile().getyPosition();
                                    List<String> validNewtonTiles = new ArrayList<>(getModel().getGame().getBoard().getTileList().stream().filter(Objects::nonNull).filter(t -> (t.getxPosition() == x || t.getyPosition() == y) && !t.getId().equals(getModel().getGame().getBoard().getTile(x, y).getId())).map(t -> t.getId()).collect(Collectors.toList()));
                                    String selectedNewtonTile = utils.askUserInput("Pick a tile where to newton it", validNewtonTiles, false);
                                    if (selectedNewtonTile.equals(Constants.NOP)) {
                                        sendNOP();
                                        return;
                                    }
                                    getPlayerInput().put(Constants.POWERUP, Arrays.asList(selectedPowerUp, newtonPlayers.get(selectedNewtonPlayer).getId(), selectedNewtonTile));
                                } else if (selectedPowerUp.contains("Teleporter")) {
                                    String selectedTeleportTile = utils.askUserInput("Pick a tile where to teleport", new ArrayList<>(validTiles), false);
                                    if (selectedTeleportTile.equals(Constants.NOP)) {
                                        sendNOP();
                                        return;
                                    }
                                    getPlayerInput().put(Constants.POWERUP, Arrays.asList(selectedPowerUp, selectedTeleportTile));
                                }
                                break;
                        }
                    });
                    if (doneActions.isEmpty()) {
                        sendNOP();
                        return;
                    }
                    getPlayerInput().put(Constants.KEY_ORDER, doneActions);
                    sendInput();
                    break;
                case Constants.RESPAWN:
                    String powerUpName = utils.askUserInput("Choose a Power Up to respawn", currentPlayer.getCharacterState().getPowerUpBag().stream().map(u -> u.getName()).collect(Collectors.toList()), true);
                    if (powerUpName.equals(Constants.NOP)) {
                        utils.println("Exiting...");
                        System.exit(0);
                        return;
                    }
                    getPlayerInput().put(Constants.KEY_ORDER, Arrays.asList(Constants.RESPAWN));
                    getPlayerInput().put(Constants.RESPAWN, Arrays.asList(powerUpName));
                    sendInput();
                    break;
                case Constants.TAGBACK_GRENADE:
                    List<String> tagbackGrenadesToUse = new ArrayList<>();
                    currentPlayer.getCharacterState().getPowerUpBag().stream().filter(up -> up.getName().contains("TagbackGrenade")).forEach(up -> {
                        String useTagbackGrenade = utils.askUserInput("Do you want to use the " + up.getName() + " powerup", Arrays.asList("Y", "N"), false);
                        if (useTagbackGrenade.equals(Constants.NOP)) {
                            sendNOP();
                            return;
                        }
                        if (useTagbackGrenade.equals("Y")) tagbackGrenadesToUse.add(up.getName());
                    });
                    if (tagbackGrenadesToUse.isEmpty()) {
                        sendNOP();
                        return;
                    }
                    getPlayerInput().put(Constants.KEY_ORDER, Arrays.asList(Constants.POWERUP));
                    getPlayerInput().put(Constants.POWERUP, tagbackGrenadesToUse);
                    sendInput();
                    break;
                case Constants.TARGETING_SCOPE:
                    List<String> targetingScopesToUse = new ArrayList<>();
                    Map<String, String> targetingScopePlayers = new HashMap<>();
                    getModel().getGame().getPlayerList().forEach(p -> {
                        if (!p.getId().equals(currentPlayer.getId())) targetingScopePlayers.put(p.getUserData().getNickname(), p.getId());
                    });
                    List<String> targetingScopePlayersKeys = new ArrayList<>(targetingScopePlayers.keySet());
                    targetingScopePlayersKeys.add("n");
                    currentPlayer.getCharacterState().getPowerUpBag().stream().filter(up -> up.getName().contains("TargetingScope")).forEach(up -> {
                        String selectedTargetingScopePlayer = utils.askUserInput("Select a user to target with the " + up.getName() + " powerup or n not to use it", targetingScopePlayersKeys, true);
                        if (selectedTargetingScopePlayer.equals(Constants.NOP)) {
                            sendNOP();
                            return;
                        }
                        if (!selectedTargetingScopePlayer.equals("n")) {
                            targetingScopesToUse.add(up.getName());
                            String selectedAmmoColor = utils.askUserInput("Select an ammo color to use with with the " + up.getName() + " powerup", Arrays.asList(AmmoColor.BLUE.getColor(), AmmoColor.RED.getColor(), AmmoColor.YELLOW.getColor()), true);
                            if (selectedAmmoColor.equals(Constants.NOP)) {
                                sendNOP();
                                return;
                            }
                            targetingScopesToUse.add(selectedAmmoColor);
                            targetingScopesToUse.add(targetingScopePlayers.get(selectedTargetingScopePlayer));
                        }
                    });
                    if (targetingScopesToUse.isEmpty()) {
                        sendNOP();
                        return;
                    }
                    getPlayerInput().put(Constants.KEY_ORDER, Arrays.asList(Constants.POWERUP));
                    getPlayerInput().put(Constants.POWERUP, targetingScopesToUse);
                    sendInput();
                    break;
                case Constants.FINISHGAME:
                    utils.println("\n\nScoreboard:\n");
                    Comparator<Player> compareByScore = (Player p1, Player p2) -> p1.getCharacterState().getScore().compareTo( p2.getCharacterState().getScore());
                    getModel().getGame().getPlayerList().sort(compareByScore);
                    for (int i = 0; i < getModel().getGame().getPlayerList().size(); i++) {
                        Player p = getModel().getGame().getPlayerList().get(i);
                        utils.println(i + ". " + p.getUserData().getNickname() + " (" + p.getCharacterState().getScore() + "pts)");
                    }
                    utils.println("\nBye bye!");
                    System.exit(0);
                    break;
            }
        } catch (PlayerNotFoundException ex) {
            logger.info(ex.getMessage());
            sendNOP();
        }
    }

    @Override
    public void reportError(String error) {
        logger.info(error);
        utils.println(error);
    }

    @Override
    public void showGame() {
        CLIController controller = new CLIController();
        controller.setView(this);
        utils.println("\nCurrent Gameboard:\n");
        controller.handleMapLoading();
        controller.handleCharactersLoading();
    }

    public void showLogin() {
        utils.printBanner();
        String nickname = utils.askUserInput("Nickname");
        if (nickname.equals(Constants.NOP)) System.exit(0);
        this.setNickname(nickname);
        String host = utils.askUserInput("IP Address", "127.0.0.1");
        if (host.equals(Constants.NOP)) System.exit(0);
        String type = utils.askUserInput("Connection type", Arrays.asList("RMI", "SOCKET"), true);
        if (type.equals(Constants.NOP)) System.exit(0);
        String map = utils.askUserInput("Map", Arrays.asList("0", "1", "2", "3"), false);
        if (map.equals(Constants.NOP)) System.exit(0);
        this.connect(nickname, host, type, map);
        utils.println("Waiting for the server to start the game...this can take a while...");
        utils.hold();
    }

    public CLIUtil getUtils() { return utils; }
}
