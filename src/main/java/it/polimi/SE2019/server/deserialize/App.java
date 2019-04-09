package it.polimi.SE2019.server.deserialize;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class App {
    public static void main(final String[] args) {
        DynamicDeserializerFactory factory = new DynamicDeserializerFactory();

        factory.registerDeserializer("weapon", new WeaponDeserializerSupplier());
        factory.registerDeserializer("actions", new ActionsDeserializerSupplier());
        factory.registerDeserializer("actionunit", new ActionUnitDeserializerSupplier());
        factory.registerDeserializer("effects", new EffectDeserializerSupplier());
        factory.registerDeserializer("conditions", new ConditionDeserializerSupplier());

        WeaponDeserializer weaponDeserializer = (WeaponDeserializer) factory.getDeserializer("weapon");

        String path = "/home/gandalf/Documenti/Sviluppo/adrenaline/src/main/java/it/polimi/SE2019/server/deserialize/data//cards.json";
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(path));
            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(bufferedReader).getAsJsonObject();

            weaponDeserializer.deserialize(json, factory);

        } catch (FileNotFoundException e) {
            System.out.println("dadasda");
        } catch (ClassNotFoundException f) {
            System.out.println("dsadssavcvc");
        }
    }
}
