package it.polimi.se2019.server.games.command;

import it.polimi.se2019.server.games.player.Player;

public class GrabCommand implements Command {

    private String card;

    public GrabCommand(String card) {
        this.card = card;
    }

    @Override
    public void execute(Player player){
        player.grab(card);
    }
}
