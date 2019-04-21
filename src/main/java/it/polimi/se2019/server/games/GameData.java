package it.polimi.se2019.server.games;

import java.util.*;

/**
 * 
 */
public class GameData {

	private String id;
	private Date startDate;


	/**
	 * Default constructor
	 * @param id
	 * @param startDate
	 */
	public GameData(String id, Date startDate) {
		this.id = id;
		this.startDate = startDate;
	}


	/**
	 * 
	 */
	public void serialize() {

	}

	/**
	 * @return id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
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