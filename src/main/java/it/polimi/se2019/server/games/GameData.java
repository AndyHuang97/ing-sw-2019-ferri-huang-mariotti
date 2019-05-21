package it.polimi.se2019.server.games;

import java.util.*;

/**
 * 
 */
public class GameData {

	private Date startDate;


	/**
	 * Default constructor
	 * @param startDate
	 */
	public GameData(Date startDate) {
		this.startDate = startDate;
	}


	/**
	 * 
	 */
	public void serialize() {

	}

	/**
	 * @return startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

}