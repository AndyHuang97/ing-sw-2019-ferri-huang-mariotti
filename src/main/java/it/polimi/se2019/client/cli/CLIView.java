package it.polimi.se2019.client.cli;

import it.polimi.se2019.client.View;
import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.client.util.Util;
import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.exceptions.PlayerNotFoundException;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;

import java.util.*;
import java.util.concurrent.CompletionService;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CLIView extends View {
    private static final Logger logger = Logger.getLogger(CLIView.class.getName());
    CLIUtil utils = new CLIUtil();

    public CLIView() {
        this.setCliTrueGuiFalse(true);
    }

    @Override
    public void askInput() {

    }

    @Override
    public void showMessage(String message) {
        switch (message) {
            case Constants.RELOAD:
                try {
                    Player currentPlayer = getModel().getGame().getPlayerByNickname(getNickname());
                    // TODO: Implementation
                    getPlayerInput().put("Reload", new ArrayList<>());
                    getPlayerInput().put("keyOrder", Arrays.asList("Reload"));
                    sendInput();
                } catch (PlayerNotFoundException ex) {
                    logger.info(ex.getMessage());
                }
                break;
            case Constants.SHOOT:

                break;
            case Constants.MAIN_ACTION:
                try {
                    Player currentPlayer = getModel().getGame().getPlayerByNickname(getNickname());
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
                        if (currentPlayer.getCharacterState().getDeaths() > 2) possibileActions.put(">>✋", Arrays.asList(Constants.MOVE, Constants.GRAB));
                        if (currentPlayer.getCharacterState().getDeaths() > 5) possibileActions.put(">\uD83D\uDF8B", Arrays.asList(Constants.MOVE, Constants.SHOOT_WEAPON));
                    }
                    List<String> actions = possibileActions.get(utils.askUserInput("Pick an action", new ArrayList<>(possibileActions.keySet()), true));
                    actions.forEach(action -> {
                        switch (action) {
                            case Constants.MOVE:
                                String selectedMoveTile = utils.askUserInput("Pick a tile", Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "None"), false);
                                if (!selectedMoveTile.equals("None")) getPlayerInput().put(Constants.MOVE, Arrays.asList(selectedMoveTile));
                                break;
                            case Constants.RELOAD:
                                List<String> reloadWeapons = new ArrayList<>();
                                String selectedReloadWeapon;
                                do {
                                    List<String> validReloadWeapons = currentPlayer.getCharacterState().getWeaponBag().stream().map(w -> w.getName()).collect(Collectors.toList());
                                    validReloadWeapons.add("None");
                                    selectedReloadWeapon = utils.askUserInput("Pick a weapon to reload", validReloadWeapons, true);
                                    if (!selectedReloadWeapon.equals("None")) reloadWeapons.add(selectedReloadWeapon);
                                } while (!selectedReloadWeapon.equals("None"));
                                getPlayerInput().put(Constants.RELOAD, reloadWeapons);
                                break;
                            case Constants.SHOOT_WEAPON:
                                List<String> validShootWeapons = currentPlayer.getCharacterState().getWeaponBag().stream().map(w -> w.getName()).collect(Collectors.toList());
                                validShootWeapons.add("None");
                                String selectedShootWeapon = utils.askUserInput("Pick a weapon to use", validShootWeapons, true);
                                if (!selectedShootWeapon.equals("None")) getPlayerInput().put(Constants.SHOOT_WEAPON, Arrays.asList(selectedShootWeapon));
                                break;
                            case Constants.GRAB:
                                String selectedGrabMode = utils.askUserInput("Do you want to grab a Weapon or a Crate", Arrays.asList("Weapon", "Crate", "None"), true);
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
                                if (selectedGrabMode.equals("Weapon")) {
                                    getPlayerInput().put(Constants.GRAB, Arrays.asList(utils.askUserInput("Choose a Weapon to grab", grabWeapons, true)));
                                } else if (selectedGrabMode.equals("Crate")) {
                                    getPlayerInput().put(Constants.GRAB, Arrays.asList(grabCrates.get(utils.askUserInput("Choose the Tile where the Crate is", new ArrayList<>(grabCrates.keySet()), true))));
                                }
                                break;
                        }
                    });
                    getPlayerInput().put("keyOrder", actions);
                    sendInput();
                } catch (PlayerNotFoundException ex) {
                    logger.info(ex.getMessage());
                }
                break;
            case Constants.RESPAWN:
                try {
                    Player currentPlayer = getModel().getGame().getPlayerByNickname(getNickname());
                    String powerUpName = utils.askUserInput("Choose a Power Up to respawn", currentPlayer.getCharacterState().getPowerUpBag().stream().map(u -> u.getName()).collect(Collectors.toList()), true);
                    getPlayerInput().put("Respawn", Arrays.asList(powerUpName));
                    getPlayerInput().put("keyOrder", Arrays.asList("Respawn"));
                    sendInput();
                } catch (PlayerNotFoundException ex) {
                    logger.info(ex.getMessage());
                }
                break;
            case Constants.TAGBACK_GRENADE:
                break;
            case Constants.TARGETING_SCOPE:
                break;
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
        this.setNickname(nickname);
        String host = utils.askUserInput("IP Address", "127.0.0.1");
        String type = utils.askUserInput("Connection type", Arrays.asList("RMI", "SOCKET"), true);
        String map = utils.askUserInput("Map", Arrays.asList("0", "1", "2", "3"), false);
        this.connect(nickname, host, type, map);
        utils.println("Waiting for the server to start the game...this can take a while...");
        utils.hold();
    }

    public CLIUtil getUtils() { return utils; }
}
