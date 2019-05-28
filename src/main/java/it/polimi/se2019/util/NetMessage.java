package it.polimi.se2019.util;

import it.polimi.se2019.server.games.Targetable;

import java.util.List;
import java.util.Map;

public class NetMessage implements Message {
    /**
     * TODO: implement composition of PlayerActions
     * String messageType, List<Targetable>
     */

    private Map<String, List<String>> commands;
    //private String messageType;
    //private List<Targetable> params;

    public NetMessage(Map<String, List<String>> commands) {
        this.commands = commands;
    }

    public List<String> getCommandParams(String commandName) {
        return commands.get(commandName);
    }

    public Map<String, List<String>> getCommands() {
        return commands;
    }
}