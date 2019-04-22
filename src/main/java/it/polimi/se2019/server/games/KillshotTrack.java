package it.polimi.se2019.server.games;

import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.util.Observable;

import java.util.*;

public class KillshotTrack extends Observable<PlayerDeath> {

    private Map<TrackState, EnumMap<PlayerColor, Integer>> deathTrack;
    private TrackState trackState;

    public KillshotTrack(List<Player> playerList) {
        deathTrack = new EnumMap<>(TrackState.class);
        trackState = TrackState.FIRSTKILL;
        registerAllPlayers(playerList);
    }

    public KillshotTrack(Map<TrackState, EnumMap<PlayerColor, Integer>> deathTrack, List<Player> playerList, TrackState trackState) {
        this.deathTrack = deathTrack;
        this.trackState = trackState;
        registerAllPlayers(playerList);
    }

    //TODO maybe add a PlayerNotDeadExcpetion
    public void addDeath(Player player, boolean overkill) {

        EnumMap<PlayerColor, Integer> colorIntegerEnumMap;

        CharacterState deadPlayerState = player.getCharacterState();
        PlayerColor deadPlayerColor = player.getColor();
        List<PlayerColor> damageBar = deadPlayerState.getDamageBar();
        PlayerColor firstKiller = damageBar.get(0);

        PlayerDeath playerDeath = new PlayerDeath(deadPlayerColor, damageBar, firstKiller, deadPlayerState.getDeathCount());
        if(!deathTrack.containsKey(trackState)) {
            colorIntegerEnumMap = new EnumMap<>(PlayerColor.class);
            updateTrackSlotValue(overkill, deadPlayerColor, colorIntegerEnumMap, 0);
            deathTrack.put(trackState, colorIntegerEnumMap);
        }
        else {
            // final frenzy mode(TrackState.EIGTHFRENZY)
            colorIntegerEnumMap = deathTrack.get(trackState);
            if(!colorIntegerEnumMap.containsKey(deadPlayerColor)) {
                updateTrackSlotValue(overkill, deadPlayerColor, colorIntegerEnumMap, 0);
            }
            else {
                updateTrackSlotValue(overkill, deadPlayerColor, colorIntegerEnumMap, colorIntegerEnumMap.get(deadPlayerColor));
            }
        }

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

    public void nextState() {

        switch (trackState){
            case FIRSTKILL: trackState = TrackState.SECONDKILL; break;
            case SECONDKILL: trackState = TrackState.THIRDKILL; break;
            case THIRDKILL: trackState = TrackState.FOURTHKILL; break;
            case FOURTHKILL: trackState = TrackState.FIFTHKILL; break;
            case FIFTHKILL: trackState = TrackState.SIXTHKILL; break;
            case SIXTHKILL: trackState = TrackState.SEVENTHKILL; break;
            default: trackState = TrackState.EIGTHFRENZY; break;   // includes SEVENTHKILL and EIGTHFRENZY
        }

    }

    public void registerAllPlayers(List<Player> playerList) {
        for(Player p : playerList) {
            register(p);
        }
    }

    public Map<TrackState, EnumMap<PlayerColor, Integer>> getDeathTrack() {
        return deathTrack;
    }

    public void setDeathTrack(Map<TrackState, EnumMap<PlayerColor, Integer>> deathTrack) {
        this.deathTrack = deathTrack;
    }

    public TrackState getTrackState() {
        return trackState;
    }

    public void setTrackState(TrackState trackState) {
        this.trackState = trackState;
    }
}