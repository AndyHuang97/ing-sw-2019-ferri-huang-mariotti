package it.polimi.se2019.server.games;

import it.polimi.se2019.server.dataupdate.KillShotTrackUpdate;
import it.polimi.se2019.server.dataupdate.StateUpdate;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.util.Observer;
import it.polimi.se2019.util.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

public class KillShotTrack implements Serializable {

    private static final Logger logger = Logger.getLogger(KillShotTrack.class.getName());

    private transient List<it.polimi.se2019.util.Observer> observerList = new ArrayList<>();

    public static final int[] NORMAL_VALUE_BAR = {8,6,4,2,1,1};

    private Map<Integer, EnumMap<PlayerColor, Integer>> deathTrack;
    private Integer killCounter;
    private Integer killsForFrenzy;

    private boolean frenzyTriggered = false;

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
        try(InputStream input = KillShotTrack.class.getClassLoader().getResource("config.properties").openStream()){
            Properties prop = new Properties();
            prop.load(input);
            killsForFrenzy = Integer.parseInt(prop.getProperty("game.kills_for_frenzy"));

        } catch (IOException e) {
            logger.warning(e.toString());
        }

    }

    /**
     * Adds the player death to the kill shot track and notifies all the players for score update.
     *
     * @param player is the player that was killed.
     * @param overkill indicates whether an overkill occurred.
     * @return true if the death triggers the frenzy, false otherwise.
     */
    //TODO maybe add a PlayerNotDeadExcpetion
    public boolean addDeath(Player player, boolean overkill) {

        EnumMap<PlayerColor, Integer> colorIntegerEnumMap;

        PlayerColor deadPlayerColor = player.getColor();

        // create the death message
        PlayerDeath playerDeath = new PlayerDeath(player, frenzyTriggered);

        if(!deathTrack.containsKey(killCounter)) {
            colorIntegerEnumMap = new EnumMap<>(PlayerColor.class);
            deathTrack.put(killCounter, colorIntegerEnumMap);
            updateTrackSlotValue(overkill, deadPlayerColor, colorIntegerEnumMap, 0);
        }
        else {
            // final frenzy mode, key already present in hash map.
            colorIntegerEnumMap = deathTrack.get(killCounter);

            if(!colorIntegerEnumMap.containsKey(deadPlayerColor)) {
                updateTrackSlotValue(overkill, deadPlayerColor, colorIntegerEnumMap, 0);
            }
            else {
                updateTrackSlotValue(overkill, deadPlayerColor, colorIntegerEnumMap, colorIntegerEnumMap.get(deadPlayerColor));
            }
        }

        boolean triggerFrenzy = updateCounter();
        notify(playerDeath);

        if (triggerFrenzy) {
            frenzyTriggered = true;
        }

        notifyKillShotTrackChange();

        player.getCharacterState().resetDamageBar();

        return triggerFrenzy;
    }

    /**
     * Calculate the score of each player for the kill shot track
     * @return map that associate the color of a player with the points it gets for the kills done
     */
    public Map<PlayerColor, Integer> calculateScore() {

        System.out.println(deathTrack);
        Map<PlayerColor, Integer> totalKillsByColor = new HashMap<>();

        // parse the deathTrack and reduce it to a Map<PlayerColor, Integer>
        for (Integer index : deathTrack.keySet()) {
            Map<PlayerColor, Integer> killEntry = deathTrack.get(index);

            for (PlayerColor playerColor : killEntry.keySet()) {
                Integer valueToUpdate;

                if (totalKillsByColor.containsKey(playerColor)) {
                    valueToUpdate = totalKillsByColor.get(playerColor);
                } else {
                    valueToUpdate = 0;
                }

                totalKillsByColor.put(playerColor, valueToUpdate + killEntry.get(playerColor));
            }
        }

        List<PlayerColor> killerList = new ArrayList<>();

        for (int i = 0; i < deathTrack.size(); i++) {
            PlayerColor colorOfActualMax = null;
            Integer actualMax = null;

            for (Map.Entry<PlayerColor, Integer> entry : totalKillsByColor.entrySet()) {

                if (colorOfActualMax == null || actualMax < entry.getValue()) {
                    actualMax = entry.getValue();
                    colorOfActualMax = entry.getKey();
                }

                else if (actualMax == entry.getValue()) {
                    PlayerColor tieBreakWinner =  tieBreaker(Arrays.asList(colorOfActualMax, entry.getKey()));

                    if (tieBreakWinner != colorOfActualMax && tieBreakWinner != null) {
                        actualMax = entry.getValue();
                        colorOfActualMax = entry.getKey();
                    }
                }
            }

            totalKillsByColor.remove(colorOfActualMax);

            if (colorOfActualMax != null) {
                killerList.add(colorOfActualMax);
            }
        }

        Map<PlayerColor, Integer> scoreMap = new HashMap<>();
        int counter = 0;
        // now we should have a sorted list where the first element si the color of the player with more kills
        // just one more for loop
        for (PlayerColor playerColor : killerList) {
            scoreMap.put(playerColor, NORMAL_VALUE_BAR[counter]);
            counter++;
        }

        return scoreMap;

    }

    private PlayerColor tieBreaker(List<PlayerColor> playerColors) {
        for (Integer index : deathTrack.keySet()) {
            Map<PlayerColor, Integer> killEntry = deathTrack.get(index);

            for (PlayerColor playerColor : killEntry.keySet()) {
                if (playerColors.contains(playerColor)) {
                    return playerColor;
                }
            }
        }

        return null;
    }


    private void updateTrackSlotValue(boolean overkill, PlayerColor deadPlayerColor, Map<PlayerColor, Integer> colorIntegerEnumMap,
                              Integer baseValue) {
        if(!overkill) {
            colorIntegerEnumMap.put(deadPlayerColor, baseValue + 1);
        }
        else {
            colorIntegerEnumMap.put(deadPlayerColor, baseValue + 2);
        }
    }


    private boolean updateCounter() {
        // when kill counter reaches the number for final frenzy, it stops adding.
        if (killCounter < killsForFrenzy-1) {
            killCounter++;
            return false;
        }
        else {
            killCounter = killsForFrenzy - 1;
            return true;
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
        notifyKillShotTrackChange();
    }

    public Integer getKillCounter() {
        return killCounter;
    }

    public void setKillCounter(Integer killCounter) {
        this.killCounter = killCounter;
        notifyKillShotTrackChange();
    }

    public Integer getKillsForFrenzy() {
        return killsForFrenzy;
    }

    public void register(Observer observer) {
        synchronized (observerList) {
            observerList.add(observer);
        }
    }

    private void notify(PlayerDeath playerDeath) {
        for (Observer observer : observerList) {
            try {
                Observer<PlayerDeath> dynamicObserver = (Observer<PlayerDeath>) observer;
                dynamicObserver.update(playerDeath);
            } catch (Observer.CommunicationError | ClassCastException e) {
                // wrong observer
            }
        }
    }

    private void notify(Response response) {
        for (Observer observer : observerList) {
            try {
                Observer<Response> dynamicObserver = (Observer<Response>) observer;
                dynamicObserver.update(response);
            } catch (Observer.CommunicationError | ClassCastException e) {
                // wrong observer
            }
        }
    }

    private void notifyKillShotTrackChange() {
        StateUpdate killShotTrackUpdate = new KillShotTrackUpdate(this);
        Response response = new Response(Arrays.asList(killShotTrackUpdate));

        notify(response);
    }
}