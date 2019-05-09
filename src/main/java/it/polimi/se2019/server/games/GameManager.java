package it.polimi.se2019.server.games;

import java.io.*;

import com.google.gson.Gson;

import java.util.*;
import java.util.logging.Logger;

public class GameManager {
	private static final Logger logger = Logger.getLogger(GameManager.class.getName());
	private List<Game> gameList;
	private String dumpName;

	public GameManager() {
		gameList = new ArrayList<>();
	}

	public void init(String dumpName) {
		this.dumpName = dumpName;

		try {
			BufferedReader br  = new BufferedReader(new FileReader(dumpName));
			//Read JSON file
			try {
				Gson gson = new Gson();
				this.gameList = Arrays.asList(gson.fromJson(br, Game[].class));
			} finally {
				br.close();
			}
		} catch (IOException e) {
			logger.info("Error while loading gamefile, skip loading saved games!");
		}
	}

	public void dumpToFile() {
		logger.info("Loading saved games");
		try {
			FileWriter writer = new FileWriter(this.dumpName);
			// Write file
			try {
				Gson gson = new Gson();
				writer.write(gson.toJson(gameList.toArray()));
			} finally {
				writer.close();
			}

		} catch (IOException e) {
			logger.info("Error while loading gamefile, skip loading saved games!");
		}
	}

	public void addGame(Game game) {
		gameList.add(game);
	}

	public Game retrieveGame() {
		return null;
	}

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