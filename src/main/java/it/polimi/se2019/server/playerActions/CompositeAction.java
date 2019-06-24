package it.polimi.se2019.server.playerActions;

import java.util.ArrayList;
import java.util.List;

public class CompositeAction {

    private List<PlayerAction> action = new ArrayList<>();

    public CompositeAction(PlayerAction... baseActions) {
        for (PlayerAction playerAction : baseActions) {
            action.add(playerAction);
        }
    }

    public List<PlayerAction> getAction() {
        return action;
    }


}
