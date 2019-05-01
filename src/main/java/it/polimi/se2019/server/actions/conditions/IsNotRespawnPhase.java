package it.polimi.se2019.server.actions.conditions;

public class IsNotRespawnPhase implements Condition {

    private String playerPhase;
    //TODO may need to implement a playerPhase if playerPhase class is necessary to manage a player turn.

    @Override
    public boolean check() {
        return false;
    }
}
