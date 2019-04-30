package it.polimi.se2019.server.games.board;

import it.polimi.se2019.server.exceptions.TileNotFoundException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 */
public abstract class Tile {

	private String color;
	private LinkType[] links;

	// ------------------------------------------------------
	// The following class variables are not present in UML.
	// They are used for testing inputs and commands from player.
	private List<Player> players;
	private Integer x,y;

	public Tile(List<Player> players, Integer x, Integer y) {
		this.players = players;
		this.x = x;
		this.y = y;
	}
	// ------------------------------------------------------
	/**
	 *
	 * @param color
	 * @param links is an array with 4 cells: 0 - north, 1 - south, 2 - east, 3 - west.
	 */

	public Tile(String color, LinkType[] links) {
		this.color = color;
		this.links = links;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public LinkType getNorthLink() {
		return links[0];
	}

	public void setNorthLink(LinkType northLink) {
		links[0] = northLink;
	}

	public LinkType getEastLink() {
		return links[1];
	}

	public void setEastLink(LinkType eastLink) {
		links[1] = eastLink;
	}

	public LinkType getSouthLink() {
		return links[2];
	}

	public void setSouthLink(LinkType southLink) {
		links[2] = southLink;
	}

	public LinkType getWestLink() {
		return links[3];
	}

	public void setWestLink(LinkType westLink) {
		links[3] = westLink;
	}

	// ------------------------------------------------------
	// The following methods are not present in UML
	/*public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}
git
	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	public void setXY(Integer x, Integer y) {this.x = x; this.y = y; }
	 */
	// ------------------------------------------------------

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

                    visibleTiles.addAll(tile.getRoom(board));
                }
            }

        } catch(TileNotFoundException e) {

        }



		return visibleTiles;
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

		for(int i = 0; i < 3; i++) {
			for(int j = 0; i < 4; j++) {
				if(board.getTileMap()[j][i].color == color) {
					tiles.add(board.getTileMap()[j][i]);
				}
			}
		}

		return tiles;
	}

}