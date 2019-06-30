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
import java.util.stream.IntStream;

public class CLIView extends View {
    private static final Logger logger = Logger.getLogger(CLIView.class.getName());
    CLIUtil utils = new CLIUtil();
    Weapon weaponInUse;
    Boolean usedBasicEffect;
    List<PowerUp> powerUpsInUse;


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
                    List<ActionUnit> actionUnits;
                    String question;
                    shootList.add(weaponInUse.getName());
                    if (usedBasicEffect) {
                        question = "Which optional effect do you want to use";
                        actionUnits = weaponInUse.getOptionalEffectList();
                    } else {
                        question = "Which basic effect do you want to use";
                        actionUnits = weaponInUse.getActionUnitList();
                    }
                    String selectedActionUnit = utils.askUserInput(question, actionUnits.stream().map(au -> au.getName()).collect(Collectors.toList()), true);
                    if (selectedActionUnit.equals(Constants.NOP)) {
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
                        shootPlayers.put(p.getUserData().getNickname(), p.getId());
                    });
                    for (int i = 0; i < actionUnit.getNumPlayerTargets(); i++) {
                        String selectedPlayerTarget = utils.askUserInput("Select a player #" + i + " to target", new ArrayList<>(shootPlayers.keySet()), true);
                        if (selectedPlayerTarget.equals(Constants.NOP)) {
                            sendNOP();
                            return;
                        }
                        shootList.add(shootPlayers.get(selectedPlayerTarget));
                    }
                    for (int i = 0; i < actionUnit.getNumTileTargets(); i++) {
                        String selectedTileTarget = utils.askUserInput("Select tile #" + i + " to target", Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"), false);
                        if (selectedTileTarget.equals(Constants.NOP)) {
                            sendNOP();
                            return;
                        }
                        shootList.add(selectedTileTarget);
                    }
                    getPlayerInput().put(Constants.KEY_ORDER, Arrays.asList(Constants.SHOOT));
                    sendInput();
                    break;
                case Constants.MAIN_ACTION:
                    Map<String, List<String>> possibileActions = new HashMap<>();
                    if (getModel().getGame().isFrenzy()) {
                        if (currentPlayer.getCharacterState().isBeforeFrenzyActivator()) {
                            possibileActions.put(">↺\uD83D\uDF8B", Arrays.asList(Constants.MOVE, Constants.RELOAD, Constants.SHOOT_WEAPON));
                            possibileActions.put(">>>>", Arrays.asList(Constants.MOVE));
                            possibileActions.put(">>✋", Arrays.asList(Constants.MOVE, Constants.GRAB));
                        } else {
                            possibileActions.put(">>↺\uD83D\uDF8B", Arrays.asList(Constants.MOVE, Constants.RELOAD, Constants.SHOOT_WEAPON));
                            possibileActions.put(">>>✋", Arrays.asList(Constants.MOVE, Constants.GRAB));
                        }
                    } else {
                        possibileActions.put(">>>", Arrays.asList(Constants.MOVE));
                        possibileActions.put(">✋", Arrays.asList(Constants.MOVE, Constants.GRAB));
                        possibileActions.put("\uD83D\uDF8B", Arrays.asList(Constants.SHOOT_WEAPON));
                        if (currentPlayer.getCharacterState().getDeaths() > 2)
                            possibileActions.put(">>✋", Arrays.asList(Constants.MOVE, Constants.GRAB));
                        if (currentPlayer.getCharacterState().getDeaths() > 5)
                            possibileActions.put(">\uD83D\uDF8B", Arrays.asList(Constants.MOVE, Constants.SHOOT_WEAPON));
                    }
                    String actionInput = utils.askUserInput("Pick an action", new ArrayList<>(possibileActions.keySet()), true);
                    if (actionInput.equals(Constants.NOP)) {
                        sendNOP();
                        return;
                    }
                    List<String> doneActions = new ArrayList<>();
                    possibileActions.get(actionInput).forEach(action -> {
                        switch (action) {
                            case Constants.MOVE:
                                String selectedMoveTile = utils.askUserInput("Pick a tile or n not to move", Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "n"), false);
                                if (selectedMoveTile.equals(Constants.NOP)) {
                                    sendNOP();
                                    return;
                                }
                                if (selectedMoveTile.equals("n")) break;
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
                                weaponInUse = weapons.get(selectedShootWeapon);
                                break;
                            case Constants.GRAB:
                                String selectedGrabMode = utils.askUserInput("Do you want to grab a Weapon or a Crate or n not to grab", Arrays.asList("Weapon", "Crate", "n"), true);
                                if (selectedGrabMode.equals(Constants.NOP)) {
                                    sendNOP();
                                    return;
                                }
                                if (selectedGrabMode.equals("n")) break;
                                Tile[][] tileMap = getModel().getGame().getBoard().getTileMap();
                                List<String> grabWeapons = new ArrayList<>();
                                Map<String, String> grabCrates = new HashMap<>();
                                IntStream.range(0, tileMap[0].length).forEach(y -> IntStream.range(0, tileMap.length).forEach(x -> {
                                    if (tileMap[x][y] != null) {
                                        if (!tileMap[x][y].isSpawnTile()) {
                                            grabCrates.put(String.valueOf(Util.convertToIndex(x, y)), tileMap[x][y].getAmmoCrate().getName());
                                        } else {
                                            tileMap[x][y].getWeaponCrate().forEach(w -> grabWeapons.add(w.getName()));
                                        }
                                    }
                                }));
                                doneActions.add(Constants.GRAB);
                                if (selectedGrabMode.equals("Weapon")) {
                                    List<String> grabSwapWeapons = new ArrayList<>();
                                    String selectedWeaponGrab = utils.askUserInput("Choose a Weapon to grab", grabWeapons, true);
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
                                    getPlayerInput().put(Constants.GRAB, grabSwapWeapons);
                                } else if (selectedGrabMode.equals("Crate")) {
                                    String selectedCrateGrab = utils.askUserInput("Choose the Tile where the Crate is", new ArrayList<>(grabCrates.keySet()), true);
                                    if (selectedCrateGrab.equals(Constants.NOP)) {
                                        sendNOP();
                                        return;
                                    }
                                    getPlayerInput().put(Constants.GRAB, Arrays.asList(grabCrates.get(selectedCrateGrab)));
                                }
                                break;
                        }
                    });
                    getPlayerInput().put(Constants.KEY_ORDER, doneActions);
                    sendInput();
                    break;
                case Constants.RESPAWN:
                    String powerUpName = utils.askUserInput("Choose a Power Up to respawn", currentPlayer.getCharacterState().getPowerUpBag().stream().map(u -> u.getName()).collect(Collectors.toList()), true);
                    if (powerUpName.equals(Constants.NOP)) {
                        sendNOP();
                        return;
                    }
                    getPlayerInput().put(Constants.KEY_ORDER, Arrays.asList(Constants.RESPAWN));
                    getPlayerInput().put(Constants.RESPAWN, Arrays.asList(powerUpName));
                    sendInput();
                    break;
                case Constants.TAGBACK_GRENADE:
                    // TODO: Implement
                    break;
                case Constants.TARGETING_SCOPE:
                    List<String> targetingScope = new ArrayList<>();
                    Map<String, String> targetingScopeTargets = new HashMap<>();
                    for (int i = 0; i < 12; i++) {
                        targetingScopeTargets.put("Tile " + i, String.valueOf(i));
                    }
                    getModel().getGame().getPlayerList().forEach(p -> {
                        targetingScopeTargets.put("User " + p.getUserData().getNickname(), p.getId());
                    });
                    powerUpsInUse.forEach(up -> {
                        targetingScope.add(up.getName());
                        String selectedGenericTarget = utils.askUserInput("Select a user or tile to target with the " + up.getName() + " powerup", new ArrayList<>(targetingScopeTargets.keySet()), true);
                        if (selectedGenericTarget.equals(Constants.NOP)) {
                            sendNOP();
                            return;
                        }
                        targetingScope.add(targetingScopeTargets.get(selectedGenericTarget));
                        String selectedAmmoColor = utils.askUserInput("Select an ammo color to use with with the " + up.getName() + " powerup", Arrays.asList(AmmoColor.BLUE.getColor(), AmmoColor.RED.getColor(), AmmoColor.YELLOW.getColor()), true);
                        if (selectedGenericTarget.equals(Constants.NOP)) {
                            sendNOP();
                            return;
                        }
                        targetingScope.add(selectedAmmoColor);
                    });
                    getPlayerInput().put(Constants.KEY_ORDER, Arrays.asList(Constants.TARGETING_SCOPE));
                    getPlayerInput().put(Constants.TARGETING_SCOPE, targetingScope);
                    sendInput();
                    break;
            }
        } catch (PlayerNotFoundException ex) {
            logger.info(ex.getMessage());
            sendNOP();
        }
    }

    @Override
    public void reportError(String error) {

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
