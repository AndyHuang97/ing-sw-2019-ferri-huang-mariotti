package it.polimi.se2019.util;

import com.google.gson.Gson;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.server.users.UserData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {
    public static void main(String[] args) {
        Map<String, List<Targetable>> commands = new HashMap<>();
        Player player = new Player(false, new UserData("Jon"), null, PlayerColor.BLUE);
        List<Targetable> targets = Arrays.asList(player);
        commands.put("player", targets);
        Request request = new Request(new Message(commands), "client1");
        Gson gson = new Gson();

        String json = gson.toJson(request);
        System.out.println(json);
    }
}
