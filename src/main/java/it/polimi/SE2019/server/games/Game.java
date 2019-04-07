package it.polimi.SE2019.server.games;

import it.polimi.SE2019.server.games.board.Board;
import it.polimi.SE2019.server.games.player.Color;
import it.polimi.SE2019.server.games.player.Player;

import java.util.*;

/**
 * 
 */
public class Game {

	private Color currentPlayer;
	private String id;
	private Date startDate;
	private ArrayList<Player> playerList;
	private Board board;


	/**
	 * Default constructor
	 * @param currentPlayer
	 * @param id
	 * @param startDate
	 * @param playerList
	 * @param board
	 */
	public Game(Color currentPlayer, String id, Date startDate, ArrayList<Player> playerList, Board board) {
		this.currentPlayer = currentPlayer;
		this.id = id;
		this.startDate = startDate;
		this.playerList = playerList;
		this.board = board;
	}


	/**
	 * 
	 */
	public GameData generateGameData() {
		return null;
	}

	public void getCurrentPlayer() {

	}

	public void updateTurn() {

	}

	/**
	 * @param value
	 */
	public void setCurrentPlayer(Color value) {

	}

	/**
	 * @return
	 */
	public String getId() {

		return "";
	}

	/**
	 * @param value
	 */
	public void setId(String value) {

	}

	/**
	 * @return
	 */
	public Date getStartDate() {

		return null;
	}

	/**
	 * @param value
	 */
	public void setStartDate(Date value) {

	}

	/**
	 * @return
	 */
	public ArrayList<Player> getPlayerList() {

		return null;
	}

	/**
	 * @param value
	 */
	public void setPlayerList(ArrayList<Player> value) {

	}

	/**
	 * @return
	 */
	public Board getBoard() {

		return null;
	}

	/**
	 * @param value
	 */
	public void setBoard(Board value) {

	}

}