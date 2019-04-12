package it.polimi.SE2019.server;


import it.polimi.SE2019.client.View;
import it.polimi.SE2019.server.games.ActiveGames;
import it.polimi.SE2019.util.Observer;

public class Controller implements Observer<PlayerAction> {
    private ActiveGames activeGames;
    private View view;

    public Controller(ActiveGames activeGames, View view) {
        this.activeGames = activeGames;
        this.view = view;
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

    public ActiveGames getActiveGames() {
        return activeGames;
    }

    public void setActiveGames(ActiveGames activeGames) {
        this.activeGames = activeGames;
    }
}
