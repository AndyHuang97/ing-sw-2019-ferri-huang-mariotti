package it.polimi.se2019.server.games.board;

import it.polimi.se2019.server.cards.ammocrate.AmmoCrate;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.exceptions.TileNotFoundException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * 
 */
public class Tile implements Targetable {

	private static final Logger logger = Logger.getLogger(Tile.class.getName());

	private final RoomColor roomColor;
	private final LinkType[] links;
	private final int xPosition;
	private final int yPosition;
	private boolean isSpawnTile;
	private List<Weapon> weaponCrate;
	private AmmoCrate ammoCrate;
	private String id;

	@Deprecated
	public Tile(RoomColor roomColor, LinkType[] links) {
		this.roomColor = roomColor;
		this.links = links;
		this.weaponCrate = null;
		this.ammoCrate = null;
		this.isSpawnTile = false;

		xPosition = 0;
		yPosition = 0;
	}

	@Deprecated
	public Tile(RoomColor roomColor, LinkType[] links, AmmoCrate ammoCrate) {
		this.roomColor = roomColor;
		this.links = links;
		this.ammoCrate = ammoCrate;
		this.weaponCrate = null;
		this.isSpawnTile = false;

		xPosition = 0;
		yPosition = 0;
	}

	@Deprecated
	public Tile(String id, LinkType[] links, List<Weapon> weaponCrate, RoomColor roomColor) {
		this.id = id;
		this.roomColor = roomColor;
		this.links = links;
		this.weaponCrate = weaponCrate;
		this.ammoCrate = null;
		this.isSpawnTile = true;

		xPosition = 0;
		yPosition = 0;
	}

	public Tile(String id, LinkType[] links, RoomColor roomColor, int xPosition, int yPosition) {
		this.id = id;
		this.roomColor = roomColor;
		this.links = links;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
	}

	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public RoomColor getRoomColor() {
		return roomColor;
	}

	/*
	public void setRoomColor(RoomColor roomColor) {
		this.roomColor = roomColor;
	}
	*/

	public LinkType getNorthLink() {
		return links[0];
	}

	/*
	public void setNorthLink(LinkType northLink) {
		links[0] = northLink;
	}
	*/

	public LinkType getEastLink() {
		return links[1];
	}

	/*
	public void setEastLink(LinkType eastLink) {
		links[1] = eastLink;
	}
	*/

	public LinkType getSouthLink() {
		return links[2];
	}

	/*
	public void setSouthLink(LinkType southLink) {
		links[2] = southLink;
	}
	*/

	public LinkType getWestLink() {
		return links[3];
	}

	/*
	public void setWestLink(LinkType westLink) {
		links[3] = westLink;
	}
	*/

	public List<Tile> getVisibleTiles(Board board) {
		List<Tile> visibleTiles = new ArrayList<>();
		int[] pos;
		Tile tile;

		visibleTiles.addAll(getRoom(board));
		try {
            pos = board.getTilePosition(this);
            for(int i = 0; i < 4; i++) {
                if(links[i] == LinkType.DOOR) {
                    switch (i) {
                        case 0:
                            tile = board.getTile(pos[0], pos[1]-1);
                            break;
                        case 1:
                            tile = board.getTile(pos[0]+1, pos[1]);
                            break;
                        case 2:
                            tile = board.getTile(pos[0], pos[1]+1);
                            break;
                        case 3:
                            tile = board.getTile(pos[0]-1, pos[1]);
                            break;
                        default:
                            tile = null;
                            break;
                    }
					if(tile != null) {
						visibleTiles.addAll(tile.getRoom(board));
					}
                }
            }

        } catch(TileNotFoundException e) {
			logger.warning("Tile not found.");
        }

		return visibleTiles;
	}

	public List<Player> getVisibleTargets(Game game) {
		List<Player> visibleTargets;
		List<Tile> visibleTiles = getVisibleTiles(game.getBoard());

		visibleTargets = game.getPlayerList().stream()
				.filter(p -> p.getCharacterState().getTile()!=null)
				.filter(p -> visibleTiles.contains(p.getCharacterState().getTile()))
				.collect(Collectors.toList());
		return visibleTargets;
	}

	public List<Player> getPlayers(Game game) {
		List<Player> players;

		players = game.getPlayerList().stream()
									  .filter(p -> p.getCharacterState().getTile() == this)
									  .collect(Collectors.toList());

		return players;
	}

	public List<Tile> getRoom(Board board) {
		List<Tile> tiles = new ArrayList<>();

		/*
		for(int i = 0; i < board.getTileMap()[0].length; i++) {
			for(int j = 0; j < board.getTileMap().length; j++) {
				if(board.getTileMap()[j][i].getRoomColor() == roomColor) {
					tiles.add(board.getTileMap()[j][i]);
				}
			}
		}*/
		tiles = board.getTileList().stream()
				.filter(Objects::nonNull)
				.filter(tile -> tile.getRoomColor().equals(roomColor))
				.collect(Collectors.toList());

		return tiles;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.getId()).append(") ");
		builder.append(roomColor.getColor());
		builder.append(": ");
		for (LinkType l : links) {
			builder.append(l);

			builder.append(" ");
		}
		return builder.toString();
	}

	public List<Weapon> getWeaponCrate() {
		return weaponCrate;
	}

	public void setWeaponCrate(List<Weapon> weaponCrate) {
	    this.weaponCrate = weaponCrate;
	}

	public AmmoCrate getAmmoCrate() {
		return ammoCrate;
	}

	public void setAmmoCrate(AmmoCrate ammoCrate) {
	    this.ammoCrate = ammoCrate;
	}

	public boolean isSpawnTile() {
		return isSpawnTile;
	}

	public void setSpawnTile(boolean spawnTile) {
		isSpawnTile = spawnTile;
	}

	public int getxPosition() {
		return xPosition;
	}

	public int getyPosition() {
		return yPosition;
	}
}