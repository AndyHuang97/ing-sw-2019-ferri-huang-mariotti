package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;

import java.util.Map;

public class DamageRoom implements Effect {

    private String Color;
    private Integer amount;

    @Override
    public void run(Game game, Map<String, Map<Targetable, Integer>> targets) {

    }
}
