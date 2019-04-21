package it.polimi.se2019.server.games.board;

/**
 * 
 */
public class Board {

	private Tile[][] tileList ;

	/**
	 * Default constructor
	 */
	public Board() {
		this.tileList = null;
	}

	public Board(Tile[][] tileList) {
		this.tileList = tileList;
	}

	public Tile[][] getTileList() {
		return tileList;
	}

	public void setTileList(Tile[][] tileList) {
		this.tileList = tileList;
	}
}