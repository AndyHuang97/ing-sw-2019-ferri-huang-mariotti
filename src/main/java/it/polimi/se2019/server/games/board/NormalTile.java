package it.polimi.se2019.server.games.board;

import it.polimi.se2019.server.cards.ammocrate.AmmoCrate;

public class NormalTile extends Tile {

    private AmmoCrate ammoCrate;

    public NormalTile(RoomColor color, LinkType[] links, AmmoCrate ammoCrate) {
        super(color, links);
        this.ammoCrate = ammoCrate;
    }

    public AmmoCrate getAmmoCrate() {
        return ammoCrate;
    }

    public void setAmmoCrate(AmmoCrate ammoCrate) {
        this.ammoCrate = ammoCrate;
    }


}
