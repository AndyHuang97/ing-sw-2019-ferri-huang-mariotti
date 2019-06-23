package it.polimi.se2019.server.games.board;

import it.polimi.se2019.server.cards.weapons.Weapon;

import java.util.List;

@Deprecated
public class SpawnTile extends Tile {

    private List<Weapon> weaponCrate;

    public SpawnTile(RoomColor color, LinkType[] links, List<Weapon> weaponCrate) {
        super(color, links);
        this.weaponCrate = weaponCrate;
    }

    public List<Weapon> getWeaponCrate() {
        return weaponCrate;
    }

    public void setWeaponCrate(List<Weapon> weaponCrate) {
        this.weaponCrate = weaponCrate;
    }
}
