package it.polimi.se2019.server.games.command;

import it.polimi.se2019.server.games.player.Player;

public interface Command {
    void execute(Player player);
}
