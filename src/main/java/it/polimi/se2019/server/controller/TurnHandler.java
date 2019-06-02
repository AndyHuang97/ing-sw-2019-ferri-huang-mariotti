package it.polimi.se2019.server.controller;

/**
 * The TurnHandler is the class that defines a turn's logic.
 */
public abstract class TurnHandler {

    private BattleHandler battleHandler;
    private TurnPhase turnPhase;

    public TurnHandler() {
        this.turnPhase = TurnPhase.FIRSTSPAWN;
    }

    public TurnHandler(BattleHandler battleHandler, TurnPhase turnPhase) {
        this.battleHandler = battleHandler;
        this.turnPhase = turnPhase;
    }

    /**
     * The nextPhase method updates the turn phase.
     */
    public void nextPhase() {

    }

    /**
     * The setUp method contains the business logic of a player's turn.
     */
    public abstract void run();

    public BattleHandler getBattleHandler() {
        return battleHandler;
    }

    public void setBattleHandler(BattleHandler battleHandler) {
        this.battleHandler = battleHandler;
    }

    public TurnPhase getTurnPhase() {
        return turnPhase;
    }

    public void setTurnPhase(TurnPhase turnPhase) {
        this.turnPhase = turnPhase;
    }
}
