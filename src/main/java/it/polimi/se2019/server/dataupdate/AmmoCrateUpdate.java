package it.polimi.se2019.server.dataupdate;

import it.polimi.se2019.server.cards.ammocrate.AmmoCrate;
import it.polimi.se2019.server.games.board.Board;
import it.polimi.se2019.util.LocalModel;

public class AmmoCrateUpdate implements StateUpdate {
    private int xPosition;
    private int yPosition;

    private AmmoCrate ammoCrate;

    public AmmoCrateUpdate(int xPosition, int yPosition, AmmoCrate ammoCrate) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.ammoCrate = ammoCrate;
    }

    @Override
    public void updateData(LocalModel model) {
        Board board = model.getBoard();

        board.setAmmoCrate(xPosition, yPosition, ammoCrate);
    }
}
