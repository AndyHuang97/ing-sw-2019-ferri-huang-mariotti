package it.polimi.se2019.server.deserialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.util.DeserializerConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class ActionsDeserializer  implements RandomDeserializer<List<ActionUnit>> {

    private static final Logger logger = Logger.getLogger(ActionsDeserializer.class.getName());

    @Override
    public List<ActionUnit> deserialize(JsonObject json, DynamicDeserializerFactory deserializerFactory) throws ClassNotFoundException {
        if (json.isJsonNull()) return Collections.emptyList();

        JsonArray jsonActionsArray = json.getAsJsonArray(DeserializerConstants.ACTIONS);

        List<ActionUnit> actionUnitArrayList = new ArrayList<>();
        ActionUnitDeserializer actionUnitDeserializer = (ActionUnitDeserializer) deserializerFactory.getDeserializer(DeserializerConstants.ACTIONUNIT);

        for (JsonElement action : jsonActionsArray) {
            JsonObject jsonActionUnit = action.getAsJsonObject();

            ActionUnit actionUnit = null;

            try {
                actionUnit = actionUnitDeserializer.deserialize(jsonActionUnit, deserializerFactory);
            } catch (ClassNotFoundException e) {
                logger.warning("Class not found.");
                throw e;
            }

            actionUnitArrayList.add(actionUnit);
        }

        return actionUnitArrayList;
    }
}
