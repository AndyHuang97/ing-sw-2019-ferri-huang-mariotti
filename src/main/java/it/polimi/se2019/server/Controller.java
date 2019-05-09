package it.polimi.se2019.server;


import it.polimi.se2019.client.View;
import it.polimi.se2019.server.games.GameManager;
import it.polimi.se2019.server.playerActions.PlayerAction;
import it.polimi.se2019.util.Observer;
import it.polimi.se2019.util.Request;

public class Controller implements Observer<PlayerAction> {

    private GameManager gameManager;
    private View view;

    public Controller(GameManager activeGames, View view) {
        this.gameManager = activeGames;
        this.view = view;
    }

    public void onNotify(Request request) {
        /**
         * Create an Action object by parsing the messageType and params list from
         * the array, then run the action
         */

    }

    public void ApplyAction(PlayerAction action){

    }

    @Override
    public void update(PlayerAction message) {

    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }
}
