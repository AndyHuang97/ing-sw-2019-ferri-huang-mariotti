package it.polimi.se2019.server;


import it.polimi.se2019.server.games.GameManager;

import java.util.Observable;

public class Model extends Observable {
    private GameManager gameManager;

    public Model(GameManager activeGames) {
        this.gameManager = activeGames;
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

    public GameManager getGameManager() {
        return null;
    }
}