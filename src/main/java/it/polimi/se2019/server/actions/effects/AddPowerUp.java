package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;

import java.util.List;
import java.util.Map;

public class AddPowerUp implements Effect {

    private Player player;

    public AddPowerUp(Player player) {
        this.player = player;
    }

    @Override
    public void run(Game game, Map<String, List<Targetable>> targets) {
        Targetable powerUp = targets.get("powerUp").get(0);
        game.getCurrentPlayer().getCharacterState().addPowerUp((PowerUp) powerUp);
    }
}
