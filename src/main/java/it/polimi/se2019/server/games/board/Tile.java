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
 * This class is used to represent a single square of the map. There are methods to manage weapon crates and ammo
 * crates on each tile.
 *
 * @author Rodolfo Mariotti
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

    /**
     * Tile constructor. Initializes a new tile.
     *
     * @param id unique identifier
     * @param links link type for every cardinal position
     * @param roomColor color of the tile
     * @param xPosition x coordinate of the tile in the map
     * @param yPosition y coordinate of the tile in the mao
     */
	public Tile(String id, LinkType[] links, RoomColor roomColor, int xPosition, int yPosition) {
		this.id = id;
		this.roomColor = roomColor;
		this.links = links;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
	}

    /**
     * Tile is a Targetable object so it has an id. This method is the getter for the id attribute.
     *
     * @return unique identifier of the tile
     */
	@Override
	public String getId() {
		return id;
	}

    /**
     * Setter for the id attribute
     *
     * @param id unique identifier of the tile
     */
	public void setId(String id) {
		this.id = id;
	}

    /**
     * Getter method for the roomColor attribute. Each tile has a roomColor, a group of tiles with the same
     * roomColor form a room.
     *
     * @return the color of the tile
     */
	public RoomColor getRoomColor() {
		return roomColor;
	}

    /**
     * Getter method, returns the type of the north link.
     *
     * @return type of the north link
     */
	public LinkType getNorthLink() {
		return links[0];
	}

    /**
     * Getter method, returns the type of the east link.
     *
     * @return type of the east link
     */
	public LinkType getEastLink() {
		return links[1];
	}

    /**
     * Getter method, returns the type of the south link.
     *
     * @return type of the south link
     */
	public LinkType getSouthLink() {
		return links[2];
	}

    /**
     * Getter method, returns the type of the west link.
     *
     * @return type of the west link
     */
	public LinkType getWestLink() {
		return links[3];
	}

    /**
     * Gets the list of tiles visible from this tile, so each tile of the room and the tiles of each room connected to
     * this tile by an open link or a door link.
     *
     * @param board board where this tile is
     * @return list of visible tiles
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

    /**
     * Get the list of players that stands on a tile tht is visible from this tile.
     *
     * @param game this tile is part of a board that is part of the game
     * @return list of visible targets
     */
	public List<Player> getVisibleTargets(Game game) {
		List<Player> visibleTargets;
		List<Tile> visibleTiles = getVisibleTiles(game.getBoard());

		visibleTargets = game.getPlayerList().stream()
				.filter(p -> p.getCharacterState().getTile()!=null)
				.filter(p -> visibleTiles.contains(p.getCharacterState().getTile()))
				.collect(Collectors.toList());
		return visibleTargets;
	}

    /**
     * Get the list of players on this tile.
     *
     * @param game this tile is part of a board that is part of the game
     * @return list of player on this tile
     */
	public List<Player> getPlayers(Game game) {
		List<Player> players;

		players = game.getPlayerList().stream()
									  .filter(p -> p.getCharacterState().getTile() == this)
									  .collect(Collectors.toList());

		return players;
	}

    /**
     * Get the tiles that have the same room color. The group of tiles with the same colors form a
     * room.
     *
     * @param board board where this tile is
     * @return tiles of the same room
     */
	public List<Tile> getRoom(Board board) {
		return board.getTileList().stream()
				.filter(Objects::nonNull)
				.filter(tile -> tile.getRoomColor().equals(roomColor))
				.collect(Collectors.toList());

	}

    /**
     * Get a textual representation of the tile object.
     *
     * @return textual representation of the tile object
     */
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

    /**
     * Getter method for the weaponCrate attribute.
     *
     * @return weapon crate of this tile
     */
	public List<Weapon> getWeaponCrate() {
		return weaponCrate;
	}

    /**
     * Setter method for the weaponCrate attribute.
     *
     * @param weaponCrate reference to the object that will be set as weapon crate
     */
	public void setWeaponCrate(List<Weapon> weaponCrate) {
	    this.weaponCrate = weaponCrate;
	}

    /**
     * Getter method for the ammoCrate attribute.
     *
     * @return ammo crate of this tile
     */
	public AmmoCrate getAmmoCrate() {
		return ammoCrate;
	}

    /**
     * Setter method for the ammoCrate attribute.
     *
     * @param ammoCrate reference to the object that will be set as ammo crate
     */
	public void setAmmoCrate(AmmoCrate ammoCrate) {
	    this.ammoCrate = ammoCrate;
	}

    /**
     * Getter method for the isSpawnTile attribute.
     *
     * @return true if this tile contains a spawn, false if contains an ammo crate
     */
	public boolean isSpawnTile() {
		return isSpawnTile;
	}

    /**
     * Setter method for the isSpawnTile attribute.
     *
     * @param spawnTile new value of the isSpawnTile attribute
     */
	public void setSpawnTile(boolean spawnTile) {
		isSpawnTile = spawnTile;
	}

    /**
     * Getter method for the xPosition attribute.
     *
     * @return position of the tile on the x axis of board
     */
	public int getxPosition() {
		return xPosition;
	}

    /**
     * Getter method for the yPosition attribute.
     *
     * @return position of the tile on the y axis of board
     */
	public int getyPosition() {
		return yPosition;
	}
}