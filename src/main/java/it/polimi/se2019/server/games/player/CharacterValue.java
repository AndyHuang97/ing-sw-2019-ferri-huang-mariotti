package it.polimi.se2019.server.games.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum CharacterValue {

    ZERODEATHS(8,6,4,2),
    ONEDEATH(6,4, 2, 1),
    TWODEATHS(4, 2, 1, 1),
    THREEDEATHS(2,1,1,1),
    FOURDEATHS(1,1,1,1);

    List<Integer> valueList;

    private CharacterValue(int i, int i1, int i2, int i3) {

        valueList = new ArrayList<>(Arrays.asList(i, i1, i2, i3));
    }

    public Integer getValue(int pos) {
        return valueList.get(pos);
    }
}
