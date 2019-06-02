package it.polimi.se2019.server.games.board;

import com.google.gson.Gson;
import it.polimi.se2019.server.exceptions.TileNotFoundException;
import it.polimi.se2019.server.graphs.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Board {

    private String id;
    private Tile[][] tileMap;
    private Graph<Tile> tileTree;

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

    public int[] getTilePosition(Tile t) throws TileNotFoundException {
        int[] result = new int[2];

        for (int xCoord = 0; xCoord < tileMap.length; xCoord++) {
            for (int yCoord = 0; yCoord < tileMap[0].length; yCoord++) {
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

    public List<Tile> tileMapToList() {
        List<Tile> list = new ArrayList<>();
        for (Tile[] array : tileMap){
            list.addAll(Arrays.asList(array));
        }
        return list;
    }

    public Tile getSpawnTile(RoomColor roomColor) {
        return tileMapToList().stream()
                .filter(Objects::nonNull)
                .filter(Tile::isSpawnTile)
                .filter(t -> t.getRoomColor() == roomColor)
                .collect(Collectors.toList()).get(0);
    }

    public Graph<Tile> getTileTree() {
        return tileTree;
    }

    public void setTileTree(Graph<Tile> tileTree) {
        this.tileTree = tileTree;
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
}