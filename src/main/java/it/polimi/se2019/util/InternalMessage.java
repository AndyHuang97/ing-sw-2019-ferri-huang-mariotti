package it.polimi.se2019.util;

import it.polimi.se2019.server.games.Targetable;

import java.util.List;
import java.util.Map;

/**
 * The internal message class, very important for the network
 *
 * @author AH
 *
 */
public class InternalMessage implements Message {
    private Map<String, List<Targetable>> commands;

    /**
     * The constructor makes the commands
     *
     * @param commands what to send
     *
     */
    public InternalMessage(Map<String, List<Targetable>> commands) {
        this.commands = commands;
    }

    /**
     * Getter of the command (single)
     *
     * @param commandName the name of the command
     * @return the command
     *
     */
    public List<Targetable> getCommandParams(String commandName) {
        return commands.get(commandName);
    }

    /**
     * Getter of the commands (all)
     *
     * @return the commands
     *
     */
    public Map<String, List<Targetable>> getCommands() {
        return commands;
    }
}