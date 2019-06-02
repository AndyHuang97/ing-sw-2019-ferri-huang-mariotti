package it.polimi.se2019.server.deserialize;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.se2019.server.games.board.NormalTile;
import it.polimi.se2019.server.games.board.Tile;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TileDeserializerTest {
    DynamicDeserializerFactory factory = new DynamicDeserializerFactory();
    TileDeserializer tileDeserializer = new TileDeserializer();

    @Test
    public void testDeserialize() {
        String path = "src/test/java/it/polimi/se2019/server/deserialize/data/tileData.json";
        BufferedReader bufferedReader;

        Tile tile = null;

        try {
            bufferedReader = new BufferedReader(new FileReader(path));
            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(bufferedReader).getAsJsonObject();

            tile = tileDeserializer.deserialize(json, factory);

            try {
                bufferedReader.close();
            } catch (IOException e) {
                Assert.fail("Buffer close error");
            }
        } catch (FileNotFoundException e) {
            Assert.fail("File not found");
        } catch (ClassNotFoundException e) {
            Assert.fail("Class not found");
        }

        Assert.assertFalse(tile.isSpawnTile());
    }

}
