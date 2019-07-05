package it.polimi.se2019.util;

import it.polimi.se2019.server.games.Targetable;

import java.util.List;
import java.util.Map;

/**
 * Net message class
 *
 * @author AH
 *
 */
public class NetMessage implements Message {
    private Map<String, List<String>> commands;

    /**
     * The constructor for the netmessage class.
     *
     * @param commands the commands
     *
     */
    public NetMessage(Map<String, List<String>> commands) {
        this.commands = commands;
    }

    /**
     * The getter for the single command
     *
     * @param commandName the name of the command to get
     *
     */
    public List<String> getCommandParams(String commandName) {
        return commands.get(commandName);
    }

    /**
     * The getter for the commands
     *
     * @return commands
     *
     */
    public Map<String, List<String>> getCommands() {
        return commands;
    }
}