package it.polimi.se2019.server.games.player;

import it.polimi.se2019.server.games.PlayerDeath;
import it.polimi.se2019.util.Observer;


public class Score implements Observer<PlayerDeath> {

    private Integer amount;
    private Player player;

    public Score() {
        amount = 0;
        player = null;
    }

    public Score(Integer amount, Player player) {
        this.amount = amount;
        this.player = player;
    }

    public void getNewScore(PlayerDeath message) {



    }

    @Override
    public void update(PlayerDeath message) {
        getNewScore(message);
    }
}
