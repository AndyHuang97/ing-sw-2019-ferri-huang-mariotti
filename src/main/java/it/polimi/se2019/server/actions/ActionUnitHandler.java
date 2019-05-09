package it.polimi.se2019.server.actions;

import it.polimi.se2019.server.actions.conditions.Condition;
import it.polimi.se2019.server.actions.effects.Effect;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.server.users.UserData;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionUnitHandler {

    static List<Condition> conditions;
    static Game game;

    public static void main(String[] args) {
        Game game;
        Player p1,p2;
        Map<String, List<Targetable>> targetables = new HashMap<>();
        List<Targetable> targetList = new ArrayList<>();
        Effect eff1, eff2;

        // damage target/targetList
        eff1 = (p, t) -> {
            for(Targetable player : t.get("targetMap"))
            {
                ((Player) player).getCharacterState().addDamage(
                        p.getCurrentPlayer().getColor(), 0);

                // log
                System.out.println("<Player " + ((Player) player).getUserData().getNickname() + "> receives " +
                        t.get("targetMap").get(0) + " damage from " + p.getCurrentPlayer().getUserData().getNickname());
                System.out.println("<Player " + ((Player) player).getUserData().getNickname() + "> damage bar: "
                        + ((Player) player).getCharacterState().getDamageBar());
            }
        };

        p1 = new Player(true, new UserData("BLUE"), new CharacterState(), PlayerColor.BLUE);
        p2 = new Player(true, new UserData("GREEN"), new CharacterState(), PlayerColor.GREEN);

        targetList.add(p1);
        targetables.put("targetMap", targetList);
        targetables.put("tileMap", new ArrayList<>());
        targetables.put("ammoMap", new ArrayList<>());
        game = new Game();
        game.setCurrentPlayer(p1);
        game.setPlayerList(Arrays.asList(p1,p2));
        eff1.run(game, targetables);
    }

    public void handleActionUnit(Predicate<List<Condition>> tester, Consumer<Game> battleHandler)
    {

    }
}
