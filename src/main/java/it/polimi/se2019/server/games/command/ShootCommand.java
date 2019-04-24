package it.polimi.se2019.server.games.command;

import it.polimi.se2019.server.games.player.Player;

public class ShootCommand implements Command {

    private Player targetPlayer;

    public ShootCommand(Player targetPlayer) {
        this.targetPlayer = targetPlayer;
    }

    @Override
    public void execute(Player player){
        player.shoot(targetPlayer);
    }
}
