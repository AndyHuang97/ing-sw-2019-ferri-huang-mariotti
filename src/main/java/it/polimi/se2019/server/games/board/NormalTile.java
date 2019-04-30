package it.polimi.se2019.server.games.board;

public class NormalTile extends  Tile {

    public AmmoCrate ammoCrate;

    public NormalTile(String color, LinkType[] links, AmmoCrate ammoCrate) {
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
