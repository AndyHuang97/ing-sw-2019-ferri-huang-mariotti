package it.polimi.se2019.server.deserialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.util.DeserializerConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OptionalEffectDeserializer implements RandomDeserializer<List<ActionUnit>> {

    @Override
    public List<ActionUnit> deserialize(JsonObject json, DynamicDeserializerFactory deserializerFactory) throws ClassNotFoundException {
        if (json.isJsonNull()) return Collections.emptyList();

        JsonArray jsonActionsArray = json.getAsJsonArray(DeserializerConstants.OPTIONALEFFECTLIST);

        List<ActionUnit> actionUnitArrayList = new ArrayList<>();
        ActionUnitDeserializer actionUnitDeserializer = (ActionUnitDeserializer) deserializerFactory.getDeserializer(DeserializerConstants.ACTIONUNIT);

        if(jsonActionsArray != null) {
            for (JsonElement action : jsonActionsArray) {
                JsonObject jsonActionUnit = action.getAsJsonObject();

                ActionUnit actionUnit = null;

                actionUnit = actionUnitDeserializer.deserialize(jsonActionUnit, deserializerFactory);

                actionUnitArrayList.add(actionUnit);
            }
        }

        return actionUnitArrayList;
    }
}
