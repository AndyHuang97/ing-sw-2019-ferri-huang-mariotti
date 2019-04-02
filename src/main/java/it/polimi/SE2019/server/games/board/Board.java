package it.polimi.SE2019.server.games.board;

import java.util.ArrayList;

/**
 * 
 */
public class Board {

	private ArrayList<Room> roomList;
	private ArrayList<Link> links;

	/**
	 * Default constructor
	 */
	public Board() {
	}

	/**
	 * @param id1
	 * @param id2
	 * @return
	 */
	public String getLinkType(int id1, int id2) {
		// TODO implement here
		return "";
	}

	/**
	 * @return
	 */
	public ArrayList<Room> getRoomList() {
		// TODO implement here
		return null;
	}

	/**
	 * @param value
	 */
	public void setRoomList(ArrayList<Room> value) {
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