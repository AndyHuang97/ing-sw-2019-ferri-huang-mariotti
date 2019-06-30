package it.polimi.se2019.server.dataupdate;

import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.games.board.Board;
import it.polimi.se2019.util.LocalModel;

import java.util.List;

public class WeaponCrateUpdate implements StateUpdate {
    private int xPosition;
    private int yPosition;

    private List<Weapon> weaponCrate;

    public WeaponCrateUpdate(int xPosition, int yPosition, List<Weapon> weaponCrate) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.weaponCrate = weaponCrate;
    }

    @Override
    public void updateData(LocalModel model) {
        Board board = model.getBoard();

        board.setWeaponCrate(xPosition, yPosition, weaponCrate);
    }
}
