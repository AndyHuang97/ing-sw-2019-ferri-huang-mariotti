package it.polimi.SE2019.server.games.board;

/**
 * 
 */
public class Board {

	private Tile[][] tileList ;


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