package it.polimi.se2019.server.games;

import java.util.*;

public class GameManager {

	private List<Game> gameList;

	/**
	 * Default constructor.
	 */
	public GameManager() {
		gameList = new ArrayList<>();
	}

	/**
	 * Constructor for an already existing server with some active games.
	 * @param gameList
	 */
	public GameManager(List<Game> gameList) {
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
	public List<Game> getGameList() {
		return gameList;
	}

	/**
	 * @param gameList
	 */
	public void setGameList(List<Game> gameList) {
		this.gameList = gameList;
	}

}