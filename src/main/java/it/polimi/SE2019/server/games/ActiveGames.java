package it.polimi.SE2019.server.games;

import java.util.*;

/**
 * 
 */
public class ActiveGames {

	private ArrayList<Game> gameList;

	/**
	 * Default constructor
	 * @param gameList
	 */
	public ActiveGames(ArrayList<Game> gameList) {
		this.gameList = gameList;
	}

	/**
	 * @param game
	 */
	public void addGame(Game game) {
		gameList.add(game);
	}

	/**
	 * @return null
	 */
	public Game retrieveGame() {
		return null;
	}

	/**
	 * @return gameList
	 */
	public ArrayList<Game> getGameList() {
		return gameList;
	}

	/**
	 * @param value
	 */
	public void setGameList(ArrayList<Game> value) {
		this.gameList = gameList;
	}

}