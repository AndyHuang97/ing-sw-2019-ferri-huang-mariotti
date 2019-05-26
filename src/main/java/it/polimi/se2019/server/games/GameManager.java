package it.polimi.se2019.server.games;

import com.google.gson.Gson;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.server.users.UserData;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class GameManager {
	private static final Logger logger = Logger.getLogger(GameManager.class.getName());
	private List<Game> gameList;
	private int waitingListMaxSize;
	private List<Tuple> waitingList;
	private String dumpName;

	public class Tuple<X, Y> {
		public final UserData userData;
		public final CommandHandler commandHandler;
		public Tuple(UserData userData, CommandHandler commandHander) {
			this.userData = userData;
			this.commandHandler = commandHander;
		}
	}

	public GameManager() {
		gameList = new ArrayList<>();
		waitingList = new ArrayList<>();
	}

	public void init(String dumpName) {
		try (InputStream input = new FileInputStream("src/main/resources/config.properties")) {
			Properties prop = new Properties();
			// load a properties file
			prop.load(input);
			waitingListMaxSize = Integer.parseInt(prop.getProperty("gamemanager.waitinglistmaxsize"));
		} catch (IOException ex) {
			logger.info(ex.toString());
		}
		this.dumpName = dumpName;
		try {
			BufferedReader br  = new BufferedReader(new FileReader(dumpName));
			//Read JSON file
			try {
				Gson gson = new Gson();
				this.gameList = new ArrayList<>(Arrays.asList(gson.fromJson(br, Game[].class)));
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

	public boolean isUserInWaitingList(String nickname) {
		// used  to check if user is in waiting list (used by view)
		return waitingList.stream().anyMatch(tuple -> tuple.userData.getNickname().equals(nickname));
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
		public GameNotFoundException(String errorMessage) {
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

	public class AlreadyPlayingException extends Exception {
		public AlreadyPlayingException(String errorMessage) {
			super(errorMessage);
		}
	}

	public Game createGame(List<Tuple> waitingList) throws IndexOutOfBoundsException {
		//create the new game and reset waiting list, do not use it
		Game newGame = new Game();
		List<Player> playerList = new ArrayList<>();
		waitingList.forEach(tuple -> {
			PlayerColor color = Stream.of(PlayerColor.values()).filter(
					playerColor -> playerList.stream().noneMatch(player -> player.getColor().equals(playerColor))
			).findAny().orElseThrow(() -> new IndexOutOfBoundsException("Too many players!"));
			playerList.add(new Player(true, tuple.userData, new CharacterState(), color));
			// register all players
			newGame.register(tuple.commandHandler);
		});
		newGame.setPlayerList(playerList);
		return newGame;
	}

	public void addUserToWaitingList(UserData newUser, CommandHandler currentCommandHandler) throws AlreadyPlayingException, IndexOutOfBoundsException {
		// add user to waiting list / game (used by view)
		if (isUserInGameList(newUser.getNickname()) || isUserInWaitingList(newUser.getNickname())) {
			throw new AlreadyPlayingException("User " + newUser.getNickname() + "is already playing or waiting!");
		}
		this.waitingList.add(new Tuple(newUser, currentCommandHandler));
		if (waitingList.size() > waitingListMaxSize) {
			Game newGame = createGame(waitingList);
			this.gameList.add(newGame);
			this.waitingList = new ArrayList<>();
		}
	}

	public List<Tuple> getWaitingList() {
		// TODO: is that method useful? Other classes cannot use Tuple type!
		return waitingList;
	}

	public List<Game> getGameList() {
		return gameList;
	}
}