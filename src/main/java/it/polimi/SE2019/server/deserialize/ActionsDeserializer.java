package it.polimi.SE2019.server.deserialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.SE2019.server.actions.ActionUnit;

import java.util.ArrayList;

public class ActionsDeserializer  implements RandomDeserializer<ArrayList<ActionUnit>> {
    @Override
    public ArrayList<ActionUnit> deserialize(JsonObject json, DynamicDeserializerFactory deserializerFactory) {
        if (json.isJsonNull()) return null;

        JsonArray jsonActionsArray = json.getAsJsonArray("actions");

        ArrayList<ActionUnit> actionUnitArrayList = new ArrayList<ActionUnit>();
        ActionUnitDeserializer actionUnitDeserializer = (ActionUnitDeserializer) deserializerFactory.getDeserializer("actionunit");

        for (JsonElement action : jsonActionsArray) {
            JsonObject jsonAction = action.getAsJsonObject();

            String name = jsonAction.get("name").getAsString();
            JsonObject jsonActionUnit = jsonAction.getAsJsonObject();

            ActionUnit actionUnit = actionUnitDeserializer.deserialize(jsonActionUnit, deserializerFactory);

            actionUnitArrayList.add(actionUnit);
        }

        return actionUnitArrayList;
    }
}
