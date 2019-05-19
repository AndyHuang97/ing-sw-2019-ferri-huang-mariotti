package it.polimi.se2019.server.games;

import java.io.*;

import com.google.gson.Gson;
import it.polimi.se2019.server.users.UserData;

import java.util.*;
import java.util.logging.Logger;

public class GameManager {
	private static final Logger logger = Logger.getLogger(GameManager.class.getName());
	private List<Game> gameList;
	private int waitingListMaxSize;
	private List<UserData> waitingList;
	private String dumpName;

	public GameManager() {
		gameList = new ArrayList<>();
		waitingList = new ArrayList<>();
		try (InputStream input = new FileInputStream("src/main/resources/config.properties")) {
			Properties prop = new Properties();
			// load a properties file
			prop.load(input);
			waitingListMaxSize = Integer.parseInt(prop.getProperty("gamemanager.waitinglistmaxsize"));
		} catch (IOException ex) {
			logger.info(ex.toString());
		}
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

	public boolean isUserInWaitingList(String nickname) {
		// used  to check if user is in waiting list (used by view)
		return waitingList.stream().anyMatch(user -> user.getNickname().equals(nickname));
	}

	public boolean isUserInGameList(String nickname) {
		// used  to check if user is in waiting list (used by view)
		return gameList.stream().anyMatch(
				game -> game.getPlayerList().stream().anyMatch(
						player -> player.getUserData().getNickname().equals(nickname)
				)
		);
	}

	public class GameNotFoundException extends Exception {
		private GameNotFoundException(String errorMessage) {
			super(errorMessage);
		}
	}

	public Game retrieveGame(String nickname) throws GameNotFoundException {
		// used to check if user is in a game (used by view)
		return gameList.stream().filter(
				game -> game.getPlayerList().stream().anyMatch(
						player -> player.getUserData().getNickname().equals(nickname)
				)
		).findAny().orElseThrow(() -> new GameNotFoundException("Nickname " + nickname + " has no games available!"));
	}

	private class AlreadyPlayingException extends Exception {
		private AlreadyPlayingException(String errorMessage) {
			super(errorMessage);
		}
	}

	public Game addUserToWaitingList(UserData newUser) throws AlreadyPlayingException {
		// add user to waiting list / game (used by view)
		if (isUserInGameList(newUser.getNickname()) || isUserInWaitingList(newUser.getNickname())) {
			throw new AlreadyPlayingException("User " + newUser.getNickname() + "is already playing or waiting!");
		}
		waitingList.add(newUser);
		if (waitingList.size() > waitingListMaxSize) {
			//create the new game and reset waiting list
			Game newGame = new Game();
			gameList.add(newGame);
			waitingList = new ArrayList<>();
			return newGame;
		}
		return null;
	}

	public List<UserData> getWaitingList() {
		return waitingList;
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