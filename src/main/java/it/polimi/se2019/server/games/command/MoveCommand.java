package it.polimi.se2019.server.games.command;

import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;

public class MoveCommand implements Command {

    private Tile tile;

    public MoveCommand(Tile tile) {
        this.tile = tile;
    }

    @Override
    public void execute(Player player) {
        //player.moveTo(tile);
    }
}
