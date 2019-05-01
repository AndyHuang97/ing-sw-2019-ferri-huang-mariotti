package it.polimi.se2019.server.games.board;

import com.google.gson.annotations.SerializedName;

public enum LinkType {
    @SerializedName("DOOR") DOOR,
    @SerializedName("OPEN") OPEN,
    @SerializedName("WALL") WALL
}
