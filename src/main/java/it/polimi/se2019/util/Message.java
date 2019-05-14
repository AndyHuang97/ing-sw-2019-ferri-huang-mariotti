package it.polimi.se2019.util;

import it.polimi.se2019.server.games.Targetable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Message {
    /**
     * TODO: implement composition of PlayerActions
     * String messageType, List<Targetable>
     */

    private Map<String, List<Targetable>> commands;
    //private String messageType;
    //private List<Targetable> params;

    public List<Targetable> getCommandParams(String commandName) {
        return commands.get(commandName);
    }

    public Map<String, List<Targetable>> getCommands() {
        return commands;
    }
}