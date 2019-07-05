package it.polimi.se2019.server.games;

import it.polimi.se2019.server.dataupdate.KillShotTrackUpdate;
import it.polimi.se2019.server.dataupdate.StateUpdate;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.util.Observer;
import it.polimi.se2019.util.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

/**
 * This class represents the kill shot track, it's used to keep track of the deaths and the player that made the kill.
 * At the end of the game it's used to calculate a bonus score for the players that made most kills.
 * KillShotTrack it's also a big part of the model because manages the death of the players and triggers the frenzy mode.
 * This class should be observed by Game and by all Player in the game because it sends updates on changes to himself and
 * triggers score update on the players.
 *
 * @author Rodolfo Mariotti
 */
public class KillShotTrack implements Serializable {
    // needed to send log dta to the server
    private static final Logger logger = Logger.getLogger(KillShotTrack.class.getName());

    // needed for internal observable implementation
    private transient List<it.polimi.se2019.util.Observer> observerList = new ArrayList<>();

    // needed to calculate the bonus points at the end of the game
    private static final int[] NORMAL_VALUE_BAR = {8,6,4,2,1,1};

    private Map<Integer, EnumMap<PlayerColor, Integer>> deathTrack;
    private Integer killCounter;
    private Integer killsForFrenzy;

    // needed to correctly send data during frenzy mode
    private boolean frenzyTriggered = false;

    /**
     * Use this constructor while initializing a KillShotTrack for a new game.
     *
     * @param playerList the list of players of the new game (they will be registered as observers)
     */
    public KillShotTrack(List<Player> playerList) {
        this.deathTrack = new HashMap<>();
        this.killCounter = 0;
        loadConfig();
        registerAllPlayers(playerList);
    }

    /**
     * Use this constructor to reinitialize a KillShotTrack while loading a saved game
     *
     * @param deathTrack state of the deathTrack when the was stop
     * @param playerList list of the players when the game was stop
     * @param killCounter killCounter value when the game was stop
     */
    public KillShotTrack(Map<Integer, EnumMap<PlayerColor, Integer>> deathTrack, List<Player> playerList, Integer killCounter) {
        this.deathTrack = deathTrack;
        this.killCounter = killCounter;
        loadConfig();
        registerAllPlayers(playerList);
    }

    /**
     * Loads configuration parameters.
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
    public boolean addDeath(Player player, boolean overkill) {

        EnumMap<PlayerColor, Integer> colorIntegerEnumMap;

        // get the eleventh value to know who caused the Fatal Shot
        PlayerColor killerPlayerColor = player.getCharacterState().getDamageBar().get(10);

        // create the death message
        PlayerDeath playerDeath = new PlayerDeath(player, frenzyTriggered);

        if(!deathTrack.containsKey(killCounter)) {
            colorIntegerEnumMap = new EnumMap<>(PlayerColor.class);
            deathTrack.put(killCounter, colorIntegerEnumMap);
            updateTrackSlotValue(overkill, killerPlayerColor, colorIntegerEnumMap, 0);
        }
        else {
            // final frenzy mode, key already present in hash map.
            colorIntegerEnumMap = deathTrack.get(killCounter);

            if(!colorIntegerEnumMap.containsKey(killerPlayerColor)) {
                updateTrackSlotValue(overkill, killerPlayerColor, colorIntegerEnumMap, 0);
            }
            else {
                updateTrackSlotValue(overkill, killerPlayerColor, colorIntegerEnumMap, colorIntegerEnumMap.get(killerPlayerColor));
            }
        }

        boolean triggerFrenzy = updateCounter();
        notify(playerDeath);

        if (triggerFrenzy) {
            frenzyTriggered = true;
        }

        notifyKillShotTrackChange();

        return triggerFrenzy;
    }

    /**
     * This method should be called at the end of the game to collect points from all player that took damage
     * and are on the field at this time.
     *
     * @param playerList list of the players you need to collect points from
     */
    public void killPlayersAndGetScore(List<Player> playerList) {
        for (Player player : playerList) {

            CharacterState characterState = player.getCharacterState();

            if (!characterState.getDamageBar().isEmpty()) {
                PlayerDeath playerDeath = new PlayerDeath(player, frenzyTriggered);
                notify(playerDeath);
            }
        }
    }

    /**
     * Calculate the score of each player for the kill shot track.
     *
     * @return map that associate the color of a player with the points it gets for the kills done during the game
     */
    public Map<PlayerColor, Integer> calculateScore() {

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

    /**
     * Method used by calculateScore() in order to solve ties between a set player with the same kill number.
     * The tie is broken by giving the player that made a kill first priority over the others.
     *
     * @param playerColors list of the colors of tied players
     * @return the color of the player that wins the tie (the one wo killed someone first)
     */
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


    /**
     * This method is used by addDeath to alter the value of the EnumMap(s) that is the value of the Map deathTrack.
     * This method adds an entry (or update it) that should represent a box in the kill shot track.
     * It's generic and can be used to edit every EnumMap.
     *
     * @param overkill if true adds two skulls else just one
     * @param killerPlayerColor the color of the skulls to add
     * @param colorIntegerEnumMap the reference of the data to update (should be the value of an entry in deathTrack)
     * @param baseValue the number of the skulls of kill's color already on the track
     */
    private void updateTrackSlotValue(boolean overkill, PlayerColor killerPlayerColor, Map<PlayerColor, Integer> colorIntegerEnumMap,
                              Integer baseValue) {
        if(!overkill) {
            colorIntegerEnumMap.put(killerPlayerColor, baseValue + 1);
        }
        else {
            colorIntegerEnumMap.put(killerPlayerColor, baseValue + 2);
        }
    }


    /**
     * This method is used to increase the kill counter. The kill counter should be updated only using this method
     * because it will stop the counter if it reaches the number of killsForFrenzy (a value set by loadConfig()).
     * This method will return true if the frenzy mode should be activated.
     *
     * @return true if killCounter reached the killsForFrenzy number (and the frenzy mode should be triggered), false
     *         otherwise
     */
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

    /**
     * Set a list of player as observers for the KillShotTrack. Once set the players will receive updates
     * on their scores for every death.
     *
     * @param playerList the list of players that needs to observe KillShotTrack
     */
    public void registerAllPlayers(List<Player> playerList) {
        for(Player p : playerList) {
            register(p);
        }
    }

    /**
     * Getter method for the deathTrack attribute.
     *
     * @return value of deathTrack
     */
    public Map<Integer, EnumMap<PlayerColor, Integer>> getDeathTrack() {
        return deathTrack;
    }

    /**
     * Setter method for the deathTrack attribute. Notifies registered observers.
     *
     * @param deathTrack every entry of the deathTrack map represents one kill on the KillShotTrack.
     *        The key (Integer) represents the kill number and is associated to a map that contains the color
     *        of the player that did the kill and the number of skulls he put on the KillShotTrack for that.
     */
    public void setDeathTrack(Map<Integer, EnumMap<PlayerColor, Integer>> deathTrack) {
        this.deathTrack = deathTrack;
        notifyKillShotTrackChange();
    }

    /**
     * Getter method for the killCounter attribute.
     *
     * @return number of kills done from the beginning of the game to te start od the frenzy mode.
     */
    public Integer getKillCounter() {
        return killCounter;
    }

    /**
     * Setter method for the killCounter attribute. Notifies registered observers.
     *
     * @param killCounter value to be set as killCounter
     */
    public void setKillCounter(Integer killCounter) {
        this.killCounter = killCounter;
        notifyKillShotTrackChange();
    }

    public Integer getKillsForFrenzy() {
        return killsForFrenzy;
    }

    /**
     * This method is used to resgister an observer to this object.
     *
     * @param observer the observer to register
     */
    public void register(Observer observer) {
        synchronized (observerList) {
            observerList.add(observer);
        }
    }

    /**
     * This method is used to call the update method of the registered observer to notify a player death (represented
     * by a PlayerDeath object). Since not all observer registered are waiting for a player death this method
     * will dynamically select the appropriate ones from them.
     *
     * @param playerDeath contains the data to send to the observers
     */
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

    /**
     * This method is used to call the update method of the registered observer to notify a change in the KillShotTrack
     * inside a Response object. Since not all observer registered are waiting this kind of data this method
     * will dynamically select the appropriate ones from them.
     *
     * @param response contains the data to send to the observers
     */
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

    /**
     * This method is used to tell the KillShot track to send an update to his observers about a change in it's
     * state. A StateUpdate of the killShotTrack will be created and put inside a Response, then the Response will be sent.
     */
    private void notifyKillShotTrackChange() {
        StateUpdate killShotTrackUpdate = new KillShotTrackUpdate(this);
        Response response = new Response(Arrays.asList(killShotTrackUpdate));

        notify(response);
    }
}