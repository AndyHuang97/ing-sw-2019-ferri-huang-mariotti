package it.polimi.se2019.client.util;

import it.polimi.se2019.server.games.board.Board;

public class Util {

    public static int[] convertToCoords(int i) {
        int[] coords = new int[2];

        coords[0] = i % 4;
        coords[1] = i / 4;

        return coords;
    }

    public static int convertToIndex(int x, int y) {
        return x + y*4;
    }

    public static int getMapId(Board board) {
        int[][] lookupTable = {{2, 3}, {1, 0}};
        int bottomRight = board.getTile(0, 2) == null ? 1 : 0;
        int topLeft = board.getTile(3, 0) == null ? 1 : 0;
        return lookupTable[bottomRight][topLeft];
    }
}
