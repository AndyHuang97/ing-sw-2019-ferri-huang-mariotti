package it.polimi.se2019.server.games;

import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;

import java.util.*;
import static java.util.Map.Entry.*;

import static java.util.stream.Collectors.toMap;

/**
 * This class is sent to all players by the kill shot track when one player is killed, it contains enough
 * data so that every player that receives  this update can change his score getting points for the damage
 * did to the dead player.
 *
 * @author Rodolfo Mariotti
 */
public class PlayerDeath {

    private PlayerColor deadPlayer;
    private List<PlayerColor> damageBar;
    private int[] valueBar;
    private int deaths;
    private boolean deathDuringFrenzy;

    /**
     * Constructs the main object to notify every player to update their score.
     */
    public PlayerDeath(Player player, boolean deathDuringFrenzy) {
        this.deadPlayer = player.getColor();
        this.damageBar = player.getCharacterState().getDamageBar();
        this.valueBar = player.getCharacterState().getValueBar();
        this.deaths = player.getCharacterState().getDeaths();
        this.deathDuringFrenzy = deathDuringFrenzy;
    }

    /**
     * Sorts in descending order the players who attacked the dead player.
     *
     * @return rankedAttackers, a descending sorted list of the attackers.
     */
    public List<PlayerColor> rankedAttackers() {

        List<PlayerColor> rankedAttackers = new ArrayList<>();
        EnumMap<PlayerColor, Integer> map = new EnumMap<>(PlayerColor.class);

        for(PlayerColor p : damageBar) {
            if(!map.containsKey(p)) {
                map.put(p, 1);
            }
            else {
                map.put(p, map.get(p)+1);
            }
        }

        // sorting the hashMap by value
        Map<PlayerColor, Integer> sorted = map.entrySet()
                                              .stream()
                                              .sorted(Collections.reverseOrder(comparingByValue()))
                                              .collect(
                                                     toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                                        LinkedHashMap::new));

        for(PlayerColor p : sorted.keySet()) {
            rankedAttackers.add(p);
        }
        return rankedAttackers;
    }

    /**
     * Getter method for the damageBar attribute.
     *
     * @return reference to the damageBar object of the player death
     */
    public List<PlayerColor> getDamageBar() {
        return damageBar;
    }

    /**
     * Setter method fot the damageBar attribute.
     *
     * @param damageBar reference to the object that will be set as damageBar in the player death
     */
    public void setDamageBar(List<PlayerColor> damageBar) {
        this.damageBar = damageBar;
    }

    /**
     * Getter method for the deadPlayer attribute.
     *
     * @return reference to the died player that triggered this player death update
     */
    public PlayerColor getDeadPlayer() {
        return deadPlayer;
    }

    /**
     * Setter method for the deadPlayer attribute.
     *
     * @param deadPlayer reference to the object that will be set as deadPlayer in the player death
     */
    public void setDeadPlayer(PlayerColor deadPlayer) {
        this.deadPlayer = deadPlayer;
    }

    /**
     * Getter method for the valueBar attribute.
     *
     * @return reference to the array that contains the value bar of the dead player
     */
    public int[] getValueBar() {
        return valueBar;
    }

    /**
     * Setter method for the valueBar attribute.
     *
     * @param valueBar reference to the array that will be set as value bar of the dead player
     */
    public void setValueBar(int[] valueBar) {
        this.valueBar = valueBar;
    }

    /**
     * Getter method for the deaths attribute.
     *
     * @return number of deaths of the dead player
     */
    public int getDeaths() {
        return deaths;
    }

    /**
     * Setter method for the deaths attribute.
     *
     * @param deaths set the number of deaths of the dead player
     */
    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    /**
     * Getter method for the deathDuringFrenzy attribute.
     *
     * @return true if the player is dead during frenzy mode, false otherwise
     */
    public boolean isDeathDuringFrenzy() {
        return deathDuringFrenzy;
    }
}
