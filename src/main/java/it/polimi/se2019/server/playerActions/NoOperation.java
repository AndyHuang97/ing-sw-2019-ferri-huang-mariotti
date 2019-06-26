package it.polimi.se2019.server.playerActions;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.util.ErrorResponse;

import java.util.List;

public class NoOperation extends PlayerAction {

    public NoOperation(int amount) {
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
        return true;
    }

    @Override
    public ErrorResponse getErrorMessage() {
        return new ErrorResponse("No operation");
    }

    @Override
    public String getId() {
        return Constants.NOP;
    }
}
