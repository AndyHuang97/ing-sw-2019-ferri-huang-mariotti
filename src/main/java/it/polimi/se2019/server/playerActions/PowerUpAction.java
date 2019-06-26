package it.polimi.se2019.server.playerActions;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.util.ErrorResponse;

import java.util.List;

public class PowerUpAction extends PlayerAction {


    public PowerUpAction(int amount) {
        super(amount);
    }

    @Override
    public void unpack(List<Targetable> params) throws UnpackingException {

    }

    @Override
    public void run() {

    }

    @Override
    public boolean check() {
        return false;
    }

    @Override
    public ErrorResponse getErrorMessage() {
        return null;
    }

    @Override
    public String getId() {
        return Constants.POWERUP;
    }
}
