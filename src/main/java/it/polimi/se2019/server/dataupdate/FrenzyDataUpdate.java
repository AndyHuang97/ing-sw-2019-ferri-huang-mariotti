package it.polimi.se2019.server.dataupdate;

import it.polimi.se2019.util.LocalModel;

public class FrenzyDataUpdate implements StateUpdate {
    boolean frenzy;


    public FrenzyDataUpdate(boolean frenzy) {
        this.frenzy = frenzy;
    }

    @Override
    public void updateData(LocalModel model) {
        model.getGame().frenzySetter(frenzy);
    }
}
