package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.player.Player;

public class AddPowerUp implements Effect {

    private Player player;

    public AddPowerUp(Player player) {
        this.player = player;
    }

    @Override
    public void run() {

    }
}
