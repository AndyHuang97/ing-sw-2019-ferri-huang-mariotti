package it.polimi.se2019.server.deserialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.se2019.server.actions.ActionUnit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OptionalEffectDeserializer implements RandomDeserializer<List<ActionUnit>> {

    @Override
    public List<ActionUnit> deserialize(JsonObject json, DynamicDeserializerFactory deserializerFactory) throws ClassNotFoundException {
        if (json.isJsonNull()) return Collections.emptyList();

        JsonArray jsonActionsArray = json.getAsJsonArray("optionalEffectList");

        List<ActionUnit> actionUnitArrayList = new ArrayList<>();
        ActionUnitDeserializer actionUnitDeserializer = (ActionUnitDeserializer) deserializerFactory.getDeserializer("actionunit");

        if(jsonActionsArray != null) {
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
        }

        return actionUnitArrayList;
    }
}
