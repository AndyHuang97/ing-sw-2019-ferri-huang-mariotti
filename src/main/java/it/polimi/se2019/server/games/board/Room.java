package it.polimi.se2019.server.games.board;

import it.polimi.se2019.server.games.player.Player;

import java.util.List;

public class Room {
    private List<Tile> tileList;

    public Room(List<Tile> tileList) {
        this.tileList = tileList;
    }

    public List<Tile> getTileList() {
        return tileList;
    }

    public void setTileList(List<Tile> tileList) {
        this.tileList = tileList;
    }

    public List<Player> getPlayers() {
        return null;
    }
}
