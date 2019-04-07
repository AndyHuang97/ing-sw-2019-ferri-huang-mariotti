package it.polimi.SE2019.server.games.board;

import java.util.ArrayList;

/**
 * 
 */
public class Board {

	private ArrayList<Tile> tileList;
	private ArrayList<Link> links;

	/**
	 * Default constructor
	 * @param tileList
	 * @param links
	 */
	public Board(ArrayList<Tile> tileList, ArrayList<Link> links) {
		this.tileList = tileList;
		this.links = links;
	}

	/**
	 * @param tileA
	 * @param tileB
	 * @return
	 */
	public String getLinkType(Tile tileA, Tile tileB) {
		// TODO implement here
		return "";
	}

	/**
	 * @return
	 */
	public ArrayList<Tile> getTileList() {
		// TODO implement here
		return null;
	}

	/**
	 * @param value
	 */
	public void setTileList(ArrayList<Tile> value) {
		// TODO implement here
	}

	/**
	 * @return
	 */
	public ArrayList<Link> getLinks() {
		// TODO implement here
		return null;
	}

	/**
	 * @param value
	 */
	public void setLinks(ArrayList<Link> value) {
		// TODO implement here
	}

}