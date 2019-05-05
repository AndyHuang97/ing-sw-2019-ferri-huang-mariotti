package it.polimi.se2019.server.games.input;

public abstract class Input {
    private String commandName;

    public Input(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }
}
