package it.polimi.SE2019.server;


import it.polimi.SE2019.client.View;
import it.polimi.SE2019.util.Observer;

public class Controller implements Observer<PlayerAction> {
    private Model model;
    private View view;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
    }

    public void ApplyAction(PlayerAction action){

    }

    @Override
    public void update(PlayerAction message) {

    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
}
