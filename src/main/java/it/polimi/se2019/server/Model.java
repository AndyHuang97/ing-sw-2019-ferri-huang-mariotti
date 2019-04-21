package it.polimi.se2019.server;


import it.polimi.se2019.server.games.ActiveGames;

import java.util.Observable;

public class Model extends Observable {
    private ActiveGames activeGames;

    public Model(ActiveGames activeGames) {
        this.activeGames = activeGames;
    }

    public void getStatus() {

    }

    public boolean isActionValid() {
        return false;
    }

    public void runAction() {

    }

    public void updateTurn() {

    }

    public ActiveGames getActiveGames() {
        return null;
    }
}