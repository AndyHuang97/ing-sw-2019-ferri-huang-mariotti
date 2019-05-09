package it.polimi.se2019.server.controller;


import it.polimi.se2019.server.games.GameManager;
import it.polimi.se2019.server.playerActions.PlayerAction;
import it.polimi.se2019.server.virtualview.VirtualView;
import it.polimi.se2019.util.Observer;
import it.polimi.se2019.util.Request;

public class Controller implements Observer<PlayerAction> {

    private GameManager gameManager;
    private VirtualView virtualView;
    private TurnHandler turnHandler;

    public Controller(GameManager activeGames, VirtualView virtualView) {
        this.gameManager = activeGames;
        this.virtualView = virtualView;
    }

    public void onNotify(Request request) {
        /**
         * Create an Action object by parsing the messageType and params list from
         * the array, then run the action
         */

    }

    public void applyAction(PlayerAction action){

    }

    @Override
    public void update(PlayerAction message) {

    }

    public VirtualView getVirtualView() {
        return virtualView;
    }

    public void setVirtualView(VirtualView virtualView) {
        this.virtualView = virtualView;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }
}
