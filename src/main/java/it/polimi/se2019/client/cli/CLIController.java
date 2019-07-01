package it.polimi.se2019.client.cli;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.client.util.Util;
import it.polimi.se2019.server.cards.Card;
import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.exceptions.PlayerNotFoundException;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.AmmoColor;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;

import java.util.*;
import java.util.stream.Collectors;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class CLIController {
    private static final Logger logger = Logger.getLogger(CLIController.class.getName());
    CLIView view;
    CLIUtil utils;

    public void handleMapLoading() {
        final String[] map = {utils.loadMapString(view.getModel().getGame().getBoard().getId())};
        Tile[][] tileMap = view.getModel().getGame().getBoard().getTileMap();
        StringBuilder weaponCreates = new StringBuilder();
        IntStream.range(0, tileMap[0].length)
                .forEach(y -> IntStream.range(0, tileMap.length)
                        .forEach(x -> {
                            final List<StringBuilder> replacement = Arrays.asList(new StringBuilder(), new StringBuilder());
                            if (tileMap[x][y] != null) {
                                final int[] lineSize = new int[2];
                                if (!tileMap[x][y].isSpawnTile()) {
                                    String ammoName = tileMap[x][y].getAmmoCrate().getName();
                                    replacement.get(0).append(utils.getUpperLowerCrate(ammoName).get(0));
                                    lineSize[0] = 3;
                                    replacement.get(1).append(utils.getUpperLowerCrate(ammoName).get(1));
                                    lineSize[1] = 3;
                                } else {
                                    String color = tileMap[x][y].getRoomColor().getColor();
                                    weaponCreates.append(utils.getPrintableRoomColor(color) + color.toLowerCase().substring(0, 1).toUpperCase() + color.toLowerCase().substring(1) + " Weapons Create" + Colors.RESET + "\n");
                                    weaponCreates.append(handleWeapons(tileMap[x][y].getWeaponCrate()));
                                }
                                view.getModel().getGame().getPlayerList().forEach(p -> {
                                    System.out.println(p.getCharacterState().getTile());
                                    if (p.getCharacterState().getTile() != null && tileMap[x][y].getId().equals(p.getCharacterState().getTile().getId())) {
                                        if (lineSize[0] < 6) {
                                            replacement.get(0).append(utils.getPrintablePlayerColor(p.getColor().getColor()) + "●" + Colors.RESET);
                                            lineSize[0]++;
                                        } else {
                                            replacement.get(1).append(utils.getPrintablePlayerColor(p.getColor().getColor()) + "●" + Colors.RESET);
                                            lineSize[1]++;
                                        }
                                    }
                                });
                                while (lineSize[0] < 6) {
                                    replacement.get(0).append(" ");
                                    lineSize[0]++;
                                }
                                while (lineSize[1] < 6) {
                                    replacement.get(1).append(" ");
                                    lineSize[1]++;
                                }
                            } else {
                                replacement.get(0).append("      ");
                                replacement.get(1).append("      ");
                            }
                            Character upperPlaceholder = (char) ('a' + Util.convertToIndex(x,y));
                            String semicompiledMap = map[0].replace(new String(new char[6]).replace('\0', upperPlaceholder), replacement.get(0).toString());
                            Character lowerPlaceholder = (char) ('A' + Util.convertToIndex(x,y));
                            String compiledMap = semicompiledMap.replace(new String(new char[6]).replace('\0', lowerPlaceholder), replacement.get(1).toString());
                            map[0] = compiledMap;
                        }));
        utils.println("+-+-+-+-+-+-+-+-+");
        for (int i = 0; i < 7; i++) {
            if (i < view.getModel().getGame().getKillshotTrack().getKillCounter()) utils.print("|" + Colors.RED_BACKGROUND + "☠" + Colors.RESET);
            else utils.print("|☠");
        }
        if (view.getModel().getGame().getKillshotTrack().getKillCounter() >= 8) utils.println("|" + Colors.RED_BACKGROUND +  "☼"  + Colors.RESET + "|");
        else utils.println("|" + Colors.RED + "☼" + Colors.RESET + "|");
        utils.println(map[0]);
        utils.print("\n");
        utils.println(weaponCreates.toString());
    }

    public void handleCharactersLoading() {
        view.getModel().getGame().getPlayerList().forEach(p -> {
            if (!p.getUserData().getNickname().equals(view.getNickname())) {
                handlePlayerBoard(p);
                // not loaded weapons
                utils.println(p.getUserData().getNickname() + " Discharged weapons:\n");
                utils.println(handleWeapons(p.getCharacterState().getWeaponBag().stream().filter(weapon -> !weapon.isLoaded()).collect(Collectors.toList())));
            }
        });
        try {
            Player currentPlayer = view.getModel().getGame().getPlayerByNickname(view.getNickname());
            handlePlayerBoard(currentPlayer);
            utils.println("My weapons:");
            utils.println(handleWeapons(currentPlayer.getCharacterState().getWeaponBag()));
            utils.println("My powerUps:");
            utils.println(handlePowerUps(currentPlayer.getCharacterState().getPowerUpBag()));
        } catch (PlayerNotFoundException ex) {
            logger.info(ex.getMessage());
        }
    }

    public void handlePlayerBoard(Player p) {
        // printing the nickname
        utils.print(utils.getPrintablePlayerColor(p.getColor().getColor()) + p.getUserData().getNickname() + " ⬤" + Colors.RESET);
        // Showing ammo and score
        utils.print("   " + p.getCharacterState().getScore() + "pts  ");
        utils.print(Colors.BLUE + "  ■" + Colors.RESET + " x");
        utils.print(p.getCharacterState().getAmmoBag().get(AmmoColor.BLUE).toString());
        utils.print(Colors.RED + "  ■" + Colors.RESET + " x");
        utils.print(p.getCharacterState().getAmmoBag().get(AmmoColor.RED).toString());
        utils.print(Colors.YELLOW + "  ■" + Colors.RESET + " x");
        utils.println(p.getCharacterState().getAmmoBag().get(AmmoColor.YELLOW).toString() + "\n");
        // printing the damage bar and marker bar
        utils.print("         ");
        if (view.getModel().getGame().isFrenzy()) utils.print("   ");
        else utils.print(">>✋");
        utils.print("         ");
        if (view.getModel().getGame().isFrenzy()) utils.print("  ");
        else utils.print(">\uD83D\uDF8B  ");
        for (PlayerColor pc : PlayerColor.values()) {
            if (pc != p.getColor()) {
                utils.print(utils.getPrintablePlayerColor(pc.getColor()) + "  ⧫" + Colors.RESET + " x" + p.getCharacterState().getMarkerBar().get(pc).toString());
            }
        }
        utils.print("\n");
        List<String> symbols = Arrays.asList("◊", "◊", "◊", "◊", "◊", "◊", "◊", "◊", "◊", "◊", "☠", "☼");
        utils.println("+---+---+---+---+---+---+---+---+---+---+---+---+");
        utils.println("|   |   |   |   |   |   |   |   |   |   |   |   |");
        p.getCharacterState().getDamageBar().forEach(c -> {
            utils.print("| " + utils.getPrintableBackgroudPlayerColor(c.getColor()) + symbols.get(0) + Colors.RESET + " ");
            symbols.remove(0);
        });
        symbols.forEach(s -> utils.print("| " + s + Colors.RESET + " "));
        utils.println("|");
        utils.println("|   |   |   |   |   |   |   |   |   |   |   |   |");
        utils.print("+-");
        if (view.getModel().getGame().isFrenzy()) utils.print("-");
        else utils.print("1");
        utils.println("-+---+---+---+---+---+---+---+---+---+---+---+");
        // printing the skull bar
        if (view.getModel().getGame().isFrenzy()) utils.print("               ");
        else utils.print("           ");
        for (int i = 0; i < p.getCharacterState().getValueBar().length; i++) {
            if (i < p.getCharacterState().getDeaths()) utils.print("   " + Colors.RED + "☠" + Colors.RESET);
            else utils.print("   " + p.getCharacterState().getValueBar()[i]);
        }
        if (view.getModel().getGame().isFrenzy()) utils.println("                  ");
        else utils.println("              ");
    }

    public String handleWeapons(List<Weapon> weapons) {
        Scanner[] scanners = new Scanner[weapons.size()];
        for (int i = 0; i < weapons.size(); i++) {
            scanners[i] = new Scanner(handleWeapon(weapons.get(i)));
        }
        return utils.nColumnsFormatter(3, scanners);
    }

    public String handlePowerUps(List<PowerUp> powerUps) {
        Scanner[] scanners = new Scanner[powerUps.size()];
        for (int i = 0; i < powerUps.size(); i++) {
            scanners[i] = new Scanner(handlePowerUp(powerUps.get(i)));
        }
        return utils.nColumnsFormatter(2, scanners);
    }

    public String handlePowerUp(PowerUp c) {
        StringBuilder result = new StringBuilder();
        result.append("+------------------------+\n");
        result.append("| " + c.getName());
        for (int i =  c.getName().length(); i < 23; i += 1) {
            result.append(" ");
        }
        result.append("|\n");
        result.append("+------------------------+\n");
        result.append("|                        |\n");
        result.append("|                        |\n");
        result.append("+------------------------+\n");
        return result.toString();
    }

    public String handleWeapon(Weapon c) {
        StringBuilder result = new StringBuilder();
        result.append("+------------------+\n");
        result.append("| " + c.getName());
        for (int i =  c.getName().length(); i < 17; i += 1) {
            result.append(" ");
        }
        result.append("|\n");
        result.append("+------------------+\n");
        result.append("|                  |\n");
        result.append("|                  |\n");
        result.append("+------------------+\n");
        return result.toString();
    }

    public void setView(CLIView view) {
        this.view = view;
        this.utils = view.getUtils();
    }
}
