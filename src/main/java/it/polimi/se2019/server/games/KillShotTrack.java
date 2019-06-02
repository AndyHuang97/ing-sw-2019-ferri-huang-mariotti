package it.polimi.se2019.server.games;

import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.util.Observable;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;

public class KillShotTrack extends Observable<PlayerDeath> {

    private static final Logger logger = Logger.getLogger(KillShotTrack.class.getName());

    private Map<Integer, EnumMap<PlayerColor, Integer>> deathTrack;
    private Integer killCounter;
    private Integer killsForFrenzy;

    /**
     * Default constructor.
     *
     * @param playerList
     */
    public KillShotTrack(List<Player> playerList) {
        this.deathTrack = new HashMap<>();
        this.killCounter = 0;
        loadConfig();
        registerAllPlayers(playerList);
    }

    /**
     * Constructor used for loading a saved game.
     *
     * @param deathTrack
     * @param playerList
     * @param killCounter
     */
    public KillShotTrack(Map<Integer, EnumMap<PlayerColor, Integer>> deathTrack, List<Player> playerList, Integer killCounter) {
        this.deathTrack = deathTrack;
        this.killCounter = killCounter;
        loadConfig();
        registerAllPlayers(playerList);
    }

    /**
     * Loads configuration parameters.
     *
     */
    private void loadConfig() {
        try(InputStream input = new FileInputStream("src/main/resources/config.properties")){
            Properties prop = new Properties();
            prop.load(input);
            killsForFrenzy = Integer.parseInt(prop.getProperty("game.kills_for_frenzy"));

        }catch (IOException e) {
            logger.warning(e.toString());
        }

    }

    /**
     * Adds the player death to the kill shot track and notifies all the players for score update.
     *
     * @param player is the player that was killed.
     * @param overkill indicates whether an overkill occurred.
     */
    //TODO maybe add a PlayerNotDeadExcpetion
    public void addDeath(Player player, boolean overkill) {

        EnumMap<PlayerColor, Integer> colorIntegerEnumMap;

        PlayerColor deadPlayerColor = player.getColor();

        // create the death message
        PlayerDeath playerDeath = new PlayerDeath(player);
        if(!deathTrack.containsKey(killCounter)) {
            colorIntegerEnumMap = new EnumMap<>(PlayerColor.class);
            updateTrackSlotValue(overkill, deadPlayerColor, colorIntegerEnumMap, 0);
            deathTrack.put(killCounter, colorIntegerEnumMap);
        }
        else {
            // final frenzy mode(TrackState.EIGTHFRENZY), key already present in hash map.
            colorIntegerEnumMap = deathTrack.get(killCounter);
            if(!colorIntegerEnumMap.containsKey(deadPlayerColor)) {
                updateTrackSlotValue(overkill, deadPlayerColor, colorIntegerEnumMap, 0);
            }
            else {
                updateTrackSlotValue(overkill, deadPlayerColor, colorIntegerEnumMap, colorIntegerEnumMap.get(deadPlayerColor));
            }
        }

        updateCounter();
        notify(playerDeath);
    }


    public void updateTrackSlotValue(boolean overkill, PlayerColor deadPlayerColor, Map<PlayerColor, Integer> colorIntegerEnumMap,
                              Integer baseValue) {
        if(!overkill) {
            colorIntegerEnumMap.put(deadPlayerColor, baseValue + 1);
        }
        else {
            colorIntegerEnumMap.put(deadPlayerColor, baseValue + 2);
        }
    }


    public void updateCounter() {
        // when kill counter reaches the number for final frenzy, it stops adding.
        if (killCounter < killsForFrenzy-1) {
            killCounter++;
        }
        else {
            killCounter = killsForFrenzy - 1;
        }
    }

    public void registerAllPlayers(List<Player> playerList) {
        for(Player p : playerList) {
            register(p);
        }
    }

    public Map<Integer, EnumMap<PlayerColor, Integer>> getDeathTrack() {
        return deathTrack;
    }

    public void setDeathTrack(Map<Integer, EnumMap<PlayerColor, Integer>> deathTrack) {
        this.deathTrack = deathTrack;
    }

    public Integer getKillCounter() {
        return killCounter;
    }

    public void setKillCounter(Integer killCounter) {
        this.killCounter = killCounter;
    }

    public Integer getKillsForFrenzy() {
        return killsForFrenzy;
    }
}