package it.polimi.se2019.server.games.board;

import com.google.gson.annotations.SerializedName;

/**
 * This enumeration contains the values to be set as link between tiles.
 *
 * @author Rodolfo Mariotti
 */
public enum LinkType {
    @SerializedName("DOOR") DOOR,
    @SerializedName("OPEN") OPEN,
    @SerializedName("WALL") WALL
}
