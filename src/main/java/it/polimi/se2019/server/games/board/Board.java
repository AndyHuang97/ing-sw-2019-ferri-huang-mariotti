package it.polimi.se2019.server.games.board;

import it.polimi.se2019.server.graphs.Graph;

public class Board {
    private Tile[][] tileMap;
    private Graph<Tile> tileTree;

    public Board() {

    }

    public Board (Tile[][] tileMap) {
        this.tileMap = tileMap;
    }

    public Tile[][] getTileMap() {
        return tileMap;
    }

    public void setTileMap(Tile[][] tileMap) {
        this.tileMap = tileMap;
    }

    public Tile getTile(int xCoord, int yCoord) {
        return tileMap[xCoord][yCoord];
    }

    public int[] getTilePosition(Tile T) { //throws TileNotFoundException {
        int[] result = new int[2];

        for (int xCoord = 0; xCoord < tileMap[0].length; xCoord++) {
            for (int yCoord = 0; yCoord < tileMap.length; yCoord++) {
                if (tileMap[xCoord][yCoord] == T) {
                    result[0] = xCoord;
                    result[1] = yCoord;
                } else {
                    //throw TileNotFoundException;
                    ;
                }
            }
        }

        return result;
    }
}