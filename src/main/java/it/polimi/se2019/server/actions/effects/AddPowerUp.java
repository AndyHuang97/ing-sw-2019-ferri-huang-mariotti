package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;

import java.util.Map;

public class AddPowerUp implements Effect {

    private Player player;

    public AddPowerUp(Player player) {
        this.player = player;
    }

    @Override
    public void run(Game game, Map<String, Map<Targetable, Integer>> targets) {

    }
}
