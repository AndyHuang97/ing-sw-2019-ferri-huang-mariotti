package it.polimi.se2019.server.actions;

import it.polimi.se2019.server.actions.conditions.Condition;
import it.polimi.se2019.server.actions.effects.Effect;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.server.users.UserData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionUnitHandler {

    static List<Condition> conditions;
    static Game game;

    public static void main(String[] args) {
        Game game;
        Player p1,p2;
        Map<String, Map<Targetable, Integer>> targetables = new HashMap<>();
        Map<Targetable, Integer> targetMap = new HashMap<>();
        Effect eff1, eff2;

        // damage target/targetList
        eff1 = (p, t) -> {
            for(Targetable player : t.get("targetMap").keySet())
            {
                ((Player) player).getCharacterState().addDamage(
                        p.getCurrentPlayer().getColor(), t.get("targetMap").get(player));

                // log
                System.out.println("<Player " + ((Player) player).getUserData().getNickname() + "> receives " +
                        t.get("targetMap").get(player) + " damage from " + p.getCurrentPlayer().getUserData().getNickname());
                System.out.println("<Player " + ((Player) player).getUserData().getNickname() + "> damage bar: "
                        + ((Player) player).getCharacterState().getDamageBar());
            }
        };

        p1 = new Player(true, new UserData("BLUE"), new CharacterState(), PlayerColor.BLUE);
        p2 = new Player(true, new UserData("GREEN"), new CharacterState(), PlayerColor.GREEN);

        targetMap.put(p2, 1);
        targetables.put("targetMap", targetMap);
        targetables.put("tileMap", new HashMap<>());
        targetables.put("ammoMap", new HashMap<>());
        game = new Game();
        game.setCurrentPlayer(p1);
        game.setPlayerList(Arrays.asList(p1,p2));
        eff1.run(game, targetables);
    }

    public void handleActionUnit(Predicate<List<Condition>> tester, Consumer<Game> battleHandler)
    {

    }
}
