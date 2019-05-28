package it.polimi.se2019.client.util;

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
}
