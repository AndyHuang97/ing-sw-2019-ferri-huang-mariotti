package it.polimi.se2019.server.games;

import it.polimi.se2019.server.games.player.PlayerColor;

import java.util.*;
import static java.util.Map.Entry.*;

import static java.util.stream.Collectors.toMap;

public class PlayerDeath {

    private PlayerColor deadPlayer;
    private List<PlayerColor> attackers;
    private PlayerColor firstAttacker;
    private Integer deathCount;

    /**
     * Constructs the main object to notify every player to update their score.
     * @param deadPlayer is the player that got killed.
     * @param damageBar is an ordered list of the attacks that deadPlayer  received.
     * @param firstAttacker is the first attacker of the dead player, needed to keep track of bonus point.
     * @param deathCount is the number of deaths of the dead player.
     */
    public PlayerDeath(PlayerColor deadPlayer, List<PlayerColor> damageBar, PlayerColor firstAttacker, Integer deathCount) {
        this.deadPlayer = deadPlayer;
        this.attackers = rankAttackers(damageBar);
        this.firstAttacker = firstAttacker;
        this.deathCount = deathCount;
    }

    /**
     * Sorts in descending order the players who attacked the dead player.
     * @param damageBar is a list of damage the dead player has received.
     * @return rankedKillers, a descending sorted list of the attacker.
     */
    public List<PlayerColor> rankAttackers(List<PlayerColor> damageBar) {

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

    public List<PlayerColor> getAttackers() {
        return attackers;
    }

    public void setAttackers(List<PlayerColor> attackers) {
        this.attackers = attackers;
    }

    public PlayerColor getFirstAttacker() {
        return firstAttacker;
    }

    public void setFirstAttacker(PlayerColor firstAttacker) {
        this.firstAttacker = firstAttacker;
    }

    public Integer getDeathCount() {
        return deathCount;
    }

    public void setDeathCount(Integer deathCount) {
        this.deathCount = deathCount;
    }

    public PlayerColor getDeadPlayer() {
        return deadPlayer;
    }

    public void setDeadPlayer(PlayerColor deadPlayer) {
        this.deadPlayer = deadPlayer;
    }
}
