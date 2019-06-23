package it.polimi.se2019.server.actions.conditions;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class HaveTargetsAtDistance implements Condition {

    private Integer distance;

    public HaveTargetsAtDistance(Integer distance) {
        this.distance = distance;
    }

    @Override
    public boolean check(Game game, Map<String, List<Targetable>> targets) {
        Tile attackerTile = game.getCurrentPlayer().getCharacterState().getTile();

        List<Player> targetList = game.getBoard().getPlayersAtDistance(game, attackerTile, distance);

        Logger.getGlobal().info("HaveTargetsAtDistance: "+!targetList.isEmpty());
        return !targetList.isEmpty();
    }

}
