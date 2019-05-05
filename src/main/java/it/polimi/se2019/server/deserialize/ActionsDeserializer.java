package it.polimi.se2019.server.deserialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.se2019.server.actions.ActionUnit;

import java.util.ArrayList;

public class ActionsDeserializer  implements RandomDeserializer<ArrayList<ActionUnit>> {
    @Override
    public ArrayList<ActionUnit> deserialize(JsonObject json, DynamicDeserializerFactory deserializerFactory) throws ClassNotFoundException {
        if (json.isJsonNull()) return null;

        JsonArray jsonActionsArray = json.getAsJsonArray("actions");

        ArrayList<ActionUnit> actionUnitArrayList = new ArrayList<>();
        ActionUnitDeserializer actionUnitDeserializer = (ActionUnitDeserializer) deserializerFactory.getDeserializer("actionunit");

        for (JsonElement action : jsonActionsArray) {
            JsonObject jsonActionUnit = action.getAsJsonObject();

            ActionUnit actionUnit = null;

            try {
                actionUnit = actionUnitDeserializer.deserialize(jsonActionUnit, deserializerFactory);
            } catch (ClassNotFoundException e) {
                throw e;
            }

            actionUnitArrayList.add(actionUnit);
        }

        return actionUnitArrayList;
    }
}
