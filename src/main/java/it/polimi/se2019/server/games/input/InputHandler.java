package it.polimi.se2019.server.games.input;

import it.polimi.se2019.server.games.command.*;

public class InputHandler {

    /** Retrieves correct command based on playerInput */
    public Command handleInput(Input input) {
        switch (input.getCommandName()) {
            case "M" :
                return new MoveCommand(((MoveInput) input).getTile());
            case "G" :
                return new GrabCommand(((GrabInput) input).getCard());
            case "S":
                return new ShootCommand(((ShootInput) input).getTargetPlayer());
            default :
                System.out.println("Not recognized input. Aborting ...");
                return null;
        }
    }
}
