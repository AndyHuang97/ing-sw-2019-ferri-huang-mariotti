package it.polimi.se2019.server.games.board;

import com.google.gson.Gson;
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

public class Board extends Observable<Response> {

    private String id;
    private Tile[][] tileMap;
    private transient Graph<Tile> tileTree;

    public Board() {
    }

    public Board (String id, Tile[][] tileMap) {
        this.id = id;
        this.tileMap = tileMap;
        this.tileTree = generateGraph();
    }

    public Tile[][] getTileMap() {
        return tileMap;
    }

    public List<Tile> getTileList() {
        List<Tile> tileList = new ArrayList<>();
        for (int i = 0; i < tileMap.length; i++) {
            for (int j = 0; j < tileMap[i].length; j++) {
                tileList.add(tileMap[i][j]);
            }
        }
        return tileList;
    }

    public void setTileMap(Tile[][] tileMap) {
        this.tileMap = tileMap;
    }

    public Tile getTile(int xCoord, int yCoord) {
        return tileMap[xCoord][yCoord];
    }

    public Tile getTileFromID(String id) {
        for (int x = 0; x < tileMap.length; x++) {
            for (int y = 0; y < tileMap[x].length; y++) {
                if (tileMap[x][y].getId() == id) return tileMap[x][y];
            }
        }

        return null;
    }

    public int[] getTilePosition(Tile t) throws TileNotFoundException {
        int[] result = new int[2];

        for (int xCoord = 0; xCoord < tileMap.length; xCoord++) {
            for (int yCoord = 0; yCoord < tileMap[xCoord].length; yCoord++) {
                if (tileMap[xCoord][yCoord] == t) {
                    result[0] = xCoord;
                    result[1] = yCoord;
                    return result;
                }
            }
        }
        throw new TileNotFoundException();
    }

    public Graph<Tile> generateGraph() {
        // TODO: manage exceptions out of bond
        Graph<Tile> graph = new Graph<>();
        Gson gson = new Gson();

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

    public Tile getSpawnTile(RoomColor roomColor) {
        return getTileList().stream()
                .filter(Objects::nonNull)
                .filter(Tile::isSpawnTile)
                .filter(t -> t.getRoomColor() == roomColor)
                .collect(Collectors.toList()).get(0);
    }

    public Graph<Tile> getTileTree() {
        return tileTree;
    }

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

    private static final int FIRST_TILE = 0;
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

    public List<Tile> getTilesAtDistance(Tile baseTile, Integer distance) {
        return getTileList().stream()
                .filter(t -> getTileTree().distance(baseTile,t).equals(distance))
                .collect(Collectors.toList());
    }

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