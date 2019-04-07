package it.polimi.SE2019.server.games.board;

import it.polimi.SE2019.server.games.player.Player;

import java.util.ArrayList;

public class Room {
    private ArrayList<Tile> tileList;

    public Room(ArrayList<Tile> tileList) {
        this.tileList = tileList;
    }

    public ArrayList<Tile> getTileList() {
        return tileList;
    }

    public void setTileList(ArrayList<Tile> tileList) {
        this.tileList = tileList;
    }

    public ArrayList<Player> getPlayers() {
        return null;
    }
}
