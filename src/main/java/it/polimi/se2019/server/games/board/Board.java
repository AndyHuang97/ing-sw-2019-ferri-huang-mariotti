package it.polimi.se2019.server.games.board;

import it.polimi.se2019.server.actions.Direction;
import it.polimi.se2019.server.cards.ammocrate.AmmoCrate;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.dataupdate.AmmoCrateUpdate;
import it.polimi.se2019.server.dataupdate.WeaponCrateUpdate;
import it.polimi.se2019.server.exceptions.TileNotFoundException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.graphs.Graph;
import it.polimi.se2019.util.Observable;
import it.polimi.se2019.util.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Objects of this class represents the board of a game, they're composed by tiles.
 * Most of the method of this class are run by the controller to check condition for the action requested by the player.
 *
 * @author Rodolfo Mariotti
 */
public class Board extends Observable<Response> {
    private static final int FIRST_TILE = 0;

    private String id;
    private Tile[][] tileMap;
    private transient Graph<Tile> tileTree;

    /**
     * Create an empty board object. This constructor should only be used in tests.
     */
    public Board() { }

    /**
     * Create a complete board object. This constructor sets the id and the tileMap of the new object.
     *
     * @param id unique identifier of the board
     * @param tileMap tiles of the board
     */
    public Board (String id, Tile[][] tileMap) {
        this.id = id;
        this.tileMap = tileMap;
        this.tileTree = generateGraph();
    }

    /**
     * Getter method for the tileMap attribute.
     *
     * @return tiles of the map as a bi-dimensional array
     */
    public Tile[][] getTileMap() {
        return tileMap;
    }

    /**
     * Getter method for the tileMap attribute.
     *
     * @return tiles of the maps as a list
     */
    public List<Tile> getTileList() {
        List<Tile> tileList = new ArrayList<>();
        for (int i = 0; i < tileMap.length; i++) {
            for (int j = 0; j < tileMap[i].length; j++) {
                tileList.add(tileMap[i][j]);
            }
        }
        return tileList;
    }

    /**
     * Setter method for the tileMap attribute.
     *
     * @param tileMap reference to the array to set as tileMap
     */
    public void setTileMap(Tile[][] tileMap) {
        this.tileMap = tileMap;
    }

    /**
     * This method is used to retrieve a tile in a selected position.
     *
     * @param xCoord x coordinate of the tile
     * @param yCoord y coordinate of the tile
     * @return the tile at position (x, y) in the board
     */
    public Tile getTile(int xCoord, int yCoord) {
        return tileMap[xCoord][yCoord];
    }

    /**
     * This method is used to retrieve a tile of the board with a given id.
     *
     * @param id id of the tile to retrieve
     * @return the requested tile or null if no tile in board matches
     */
    public Tile getTileFromID(String id) {
        for (int x = 0; x < tileMap.length; x++) {
            for (int y = 0; y < tileMap[x].length; y++) {
                if (tileMap[x][y].getId().equals(id)) return tileMap[x][y];
            }
        }

        return null;
    }

    /**
     * Gets the position of a tile in board from a reference of a tile.
     *
     * @param tile reference of a tile
     * @return an array of two values containing the x coordinate and the y coordinate of the tile [x, y]
     * @throws TileNotFoundException if the tile is not in this board
     */
    public int[] getTilePosition(Tile tile) throws TileNotFoundException {
        int[] result = new int[2];

        for (int xCoord = 0; xCoord < tileMap.length; xCoord++) {
            for (int yCoord = 0; yCoord < tileMap[xCoord].length; yCoord++) {
                if (tileMap[xCoord][yCoord] == tile) {
                    result[0] = xCoord;
                    result[1] = yCoord;
                    return result;
                }
            }
        }
        throw new TileNotFoundException();
    }

    /**
     * Generate a graph of the board from the tile map. The graph is used by other methods
     * to calculate the distance between two tiles.
     *
     * @return graph of the board
     */
    public Graph<Tile> generateGraph() {
        // TODO: manage exceptions out of bond
        Graph<Tile> graph = new Graph<>();

        IntStream.range(0, tileMap[0].length)
                .forEach(y -> IntStream.range(0, tileMap.length)
                        .forEach(x -> graph.addVertex(tileMap[x][y])));

        for (int yCoord = 0; yCoord < tileMap[0].length; yCoord++) {
             for (int xCoord = 0; xCoord < tileMap.length; xCoord++) {
                Tile actualTile = tileMap[xCoord][yCoord];

                // intercept the null tile
                if (actualTile != null) {
                    if (actualTile.getSouthLink() == LinkType.OPEN || actualTile.getSouthLink() == LinkType.DOOR) {
                        Tile linkedTile = tileMap[xCoord][yCoord + 1];
                        graph.addEdge(actualTile, linkedTile);
                    }
                    if (actualTile.getEastLink() == LinkType.OPEN || actualTile.getEastLink() == LinkType.DOOR) {
                        Tile linkedTile = tileMap[xCoord + 1][yCoord];
                        graph.addEdge(actualTile, linkedTile);
                    }
                }
            }
        }

        return graph;
    }

    /**
     * This method gets the spawn tile for the specified color. Since there is one room for each color and
     * each room has only one spawn tile, this tile is unique.
     *
     * @param roomColor color of the room
     * @return spawn tile of the room selected
     */
    public Tile getSpawnTile(RoomColor roomColor) {
        return getTileList().stream()
                .filter(Objects::nonNull)
                .filter(Tile::isSpawnTile)
                .filter(t -> t.getRoomColor() == roomColor)
                .collect(Collectors.toList()).get(0);
    }

    /**
     * Getter method for the tileTree. The tile tree is used to calculate the distance between two tiles.
     *
     * @return th tree representation of the board
     */
    public Graph<Tile> getTileTree() {
        return tileTree;
    }

    /**
     * Getter method
     * @param tileTree
     */
    public void setTileTree(Graph<Tile> tileTree) {
        this.tileTree = tileTree;
    }

    /**
     * Gets the relative position between two tiles. Diagonal cases are not handled and will throw an exception.
     *
     * @param firstTile starting tile
     * @param secondTile arrival tile
     * @return direction between the two tiles
     * @throws IllegalStateException the selected tiles are not on the same row or column
     */
    public Direction getDirection(Tile firstTile, Tile secondTile) {
        try {
            int[] pos1 = this.getTilePosition(firstTile);
            int[] pos2 = this.getTilePosition(secondTile);

            if ((pos1[0] - pos2[0] == 0) && (pos1[1] - pos2[1] > 0)) {
                return Direction.NORTH;
            }
            if ((pos1[0] - pos2[0] < 0) && (pos1[1] - pos2[1] == 0)) {
                return Direction.EAST;
            }
            if ((pos1[0] - pos2[0] == 0) && (pos1[1] - pos2[1] < 0)) {
                return Direction.SOUTH;
            }
            if ((pos1[0] - pos2[0] > 0) && (pos1[1] - pos2[1] == 0)) {
                return Direction.WEST;
            }
        } catch(TileNotFoundException e) {
            Logger.getGlobal().warning(e.toString());
        }
        throw new IllegalStateException(); // diagonal case
    }

    /**
     * This method is used to check if the tiles in the tile list passed are all in the specified direction.
     *
     * @param dir direction to check
     * @param tile starting tile
     * @param tileList other tiles
     * @return true if all the tiles are in the specified direction false otherwise
     */
    public boolean isOneDirectionList(Direction dir, Tile tile, List<Tile> tileList) {
        try {
            if (!tileList.isEmpty()) {
                Tile secondTile = tileList.get(FIRST_TILE);
                if (dir.equals(this.getDirection(tile, secondTile))) {
                    return isOneDirectionList(dir, secondTile, tileList.subList(1,tileList.size()));
                }
                return false;
            }
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /**
     * Get all the tiles with the same specified distance from a specified tile.
     *
     * @param baseTile central point
     * @param distance radius
     * @return tiles equidistant (at distance specified by distance param) from the baseTile
     */
    public List<Tile> getTilesAtDistance(Tile baseTile, Integer distance) {
        return getTileList().stream()
                .filter(t -> getTileTree().distance(baseTile,t).equals(distance))
                .collect(Collectors.toList());
    }

    /**
     * Get all the players whit the same specified distance from a specified tile.
     *
     * @param game the game that contains the board and the players
     * @param baseTile central point
     * @param distance radius
     * @return players equidistant (at distance specified by distance param) from the baseTile
     */
    public List<Player> getPlayersAtDistance(Game game, Tile baseTile, Integer distance) {
        List<Tile> tileList = new ArrayList<>(this.getTilesAtDistance(baseTile, distance));

        List<Player> targetList = new ArrayList<>();
        tileList.stream()
                .map(t -> t.getPlayers(game).stream()
                        .filter(p -> !p.equals(game.getCurrentPlayer()))
                        .collect(Collectors.toList()))
                .forEach(targetList::addAll);
        return targetList;
    }

    /**
     * Gets a textual representation of the board object.
     *
     * @return textual representation of the board object
     */
    @Override
    public String toString() {
        StringBuilder bld = new StringBuilder();
        for(Tile[] row : tileMap) {
            for(Tile t : row) {
                if (t != null) {
                    bld.append(t.toString() + " ");
                }
            }
            bld.append("\n");
        }
        return bld.toString();
    }

    /**
     * Getter method for the id attribute.
     *
     * @return
     */
    public String getId() {
        return id;
    }

    public void setAmmoCrate(int xPosition, int yPosition, AmmoCrate ammoCrate) {
        Tile tile = getTile(xPosition, yPosition);
        tile.setAmmoCrate(ammoCrate);

        AmmoCrateUpdate ammoCrateUpdate = new AmmoCrateUpdate(xPosition, yPosition, ammoCrate);
        Response response = new Response(Arrays.asList(ammoCrateUpdate));

        notify(response);
    }

    public void setWeaponCrate(int xPosition, int yPosition, List<Weapon> weaponCrate) {
        Tile tile = getTile(xPosition, yPosition);
        tile.setWeaponCrate(weaponCrate);

        WeaponCrateUpdate weaponCrateUpdate = new WeaponCrateUpdate(xPosition, yPosition, weaponCrate);
        Response response = new Response(Arrays.asList(weaponCrateUpdate));

        notify(response);
    }
}