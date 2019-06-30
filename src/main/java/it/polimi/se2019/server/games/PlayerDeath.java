package it.polimi.se2019.server.games;

import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;

import java.util.*;
import static java.util.Map.Entry.*;

import static java.util.stream.Collectors.toMap;

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

    public List<PlayerColor> getDamageBar() {
        return damageBar;
    }

    public void setDamageBar(List<PlayerColor> damageBar) {
        this.damageBar = damageBar;
    }

    public PlayerColor getDeadPlayer() {
        return deadPlayer;
    }

    public void setDeadPlayer(PlayerColor deadPlayer) {
        this.deadPlayer = deadPlayer;
    }

    public int[] getValueBar() {
        return valueBar;
    }

    public void setValueBar(int[] valueBar) {
        this.valueBar = valueBar;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public boolean isDeathDuringFrenzy() {
        return deathDuringFrenzy;
    }
}
