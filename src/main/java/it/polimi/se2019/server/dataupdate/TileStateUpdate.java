package it.polimi.se2019.server.dataupdate;

import it.polimi.se2019.server.cards.ammocrate.AmmoCrate;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.games.board.Board;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.util.LocalModel;

import java.util.List;

public class TileStateUpdate implements StateUpdate {
    String id;

    AmmoCrate ammoCrate;
    List<Weapon> weaponCrate;

    public TileStateUpdate(String id, AmmoCrate ammoCrate, List<Weapon> weaponCrate) {
        this.id = id;
        this.ammoCrate = ammoCrate;
        this.weaponCrate = weaponCrate;
    }

    @Override
    public void updateData(LocalModel model) {
        Board board = model.getBoard();
        Tile tileToUpdate = board.getTileFromID(id);

        if (ammoCrate != null) tileToUpdate.setAmmoCrate(ammoCrate);
        else if (weaponCrate != null) tileToUpdate.setWeaponCrate(weaponCrate);
    }
}
