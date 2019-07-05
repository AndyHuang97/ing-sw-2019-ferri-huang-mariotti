package it.polimi.se2019.util;

import it.polimi.se2019.server.games.Targetable;

import java.util.List;
import java.util.Map;

/**
 * This class represents a set of commands. It's used by the CommandHandler to store the data parsed from the network
 * about the action requested by the player.
 *
 * @author Andrea Huang
 */
public class InternalMessage implements Message {
    private Map<String, List<Targetable>> commands;

    /**
     * Builds an InternalMessage object from a map that associate a command with his params.
     *
     * @param commands stored commands
     */
    public InternalMessage(Map<String, List<Targetable>> commands) {
        this.commands = commands;
    }

    /**
     * Gets the params of the selected command.
     *
     * @param commandName selected command
     * @return the params of the selected command, or null if the command is not in map
     */
    public List<Targetable> getCommandParams(String commandName) {
        return commands.get(commandName);
    }

    /**
     * Getter method for the commands attribute.
     *
     * @return
     */
    public Map<String, List<Targetable>> getCommands() {
        return commands;
    }
}