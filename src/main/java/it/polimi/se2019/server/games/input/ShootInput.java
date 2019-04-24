package it.polimi.se2019.server.games.input;

import it.polimi.se2019.server.games.player.Player;

public class ShootInput extends Input {

    private Player targetPlayer;

    public ShootInput(Player targetPlayer) {
        super("S");
        this.targetPlayer = targetPlayer;
    }

    public Player getTargetPlayer() {
        return targetPlayer;
    }

    public void setTargetPlayer(Player targetPlayer) {
        this.targetPlayer = targetPlayer;
    }
}
