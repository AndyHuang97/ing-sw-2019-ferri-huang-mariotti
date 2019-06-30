package it.polimi.se2019.server.deserialize;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.se2019.server.games.board.Board;
import it.polimi.se2019.server.games.board.Tile;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class BoardDeserializerTest {
    DynamicDeserializerFactory factory = new DynamicDeserializerFactory();
    BoardDeserializer boardDeserializer = new BoardDeserializer();

    @Before
    public void setUp() {
        factory.registerDeserializer("tile", new TileDeserializerSupplier());
    }

    @Test
    public void testDeserialize() {
        String path = "src/main/resources/json/maps/map0.json";
        BufferedReader bufferedReader;

        Board board = null;

        try {
            bufferedReader = new BufferedReader(new FileReader(path));
            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(bufferedReader).getAsJsonObject();

            board = boardDeserializer.deserialize(json, factory);

            try {
                bufferedReader.close();
            } catch (IOException e) {
                Assert.fail("Error on file close");
            }

        } catch (FileNotFoundException e) {
            Assert.fail("File not found");
        } catch (ClassNotFoundException e) {
            Assert.fail("Class not found");
        }

        Tile tile = board.getTile(0,0);
        Assert.assertFalse(tile.isSpawnTile());
    }
}
