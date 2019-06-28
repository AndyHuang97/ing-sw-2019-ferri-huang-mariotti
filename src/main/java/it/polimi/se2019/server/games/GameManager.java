package it.polimi.se2019.server.games;

import com.google.gson.Gson;
import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.server.users.UserData;
import it.polimi.se2019.util.Observer;
import it.polimi.se2019.util.Response;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameManager {
	private static final Logger logger = Logger.getLogger(GameManager.class.getName());
	private List<Game> gameList;
	private int waitingListMaxSize;
	private int waitingListStartTimerSize;
	private int startTimerSeconds;
	private List<Tuple> waitingList;
	private List<String> mapPreference;
	private String dumpName;
	private int pingIntervalMilliseconds;

	public class Tuple<X, Y> {
		public final UserData userData;
		public final CommandHandler commandHandler;
		public Tuple(UserData userData, CommandHandler commandHander) {
			this.userData = userData;
			this.commandHandler = commandHander;
		}
	}

	public GameManager() {
		this.gameList = new ArrayList<>();
		this.waitingList = new ArrayList<>();
		this.mapPreference = new ArrayList<>();
	}

	public void init(String dumpName) {
		try (InputStream input = new FileInputStream("src/main/resources/config.properties")) {
			Properties prop = new Properties();
			// load a properties file
			prop.load(input);
			this.waitingListMaxSize = Integer.parseInt(prop.getProperty("game_manager.waiting_list_max_size"));
			this.waitingListStartTimerSize = Integer.parseInt(prop.getProperty("game_manager.waiting_list_start_timer_size"));
			this.startTimerSeconds = Integer.parseInt(prop.getProperty("game_manager.start_timer_seconds"));
			this.pingIntervalMilliseconds = Integer.parseInt(prop.getProperty("game_manager.ping_interval_milliseconds"));
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
		//System.out.println(dumpName);
		logger.info("Saving games to file");
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
			logger.info("Error while saving games to file");
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

	public void createGame() throws IndexOutOfBoundsException {
		logger.info("Starting a new game");
		//create the new game and reset waiting list, do not use it
		Game newGame = new Game();
		List<Player> playerList = new ArrayList<>();
		this.waitingList.forEach(tuple -> {
			PlayerColor color = Stream.of(PlayerColor.values()).filter(
					playerColor -> playerList.stream().noneMatch(player -> player.getColor().equals(playerColor))
			).findAny().orElseThrow(() -> new IndexOutOfBoundsException("Too many players!"));
			// TODO: initialize character state or it is fine?
			CharacterState characterState = new CharacterState();
			playerList.add(new Player(UUID.randomUUID().toString(), true, tuple.userData, characterState, color));
			// register all players
			newGame.register(tuple.commandHandler);
		});
		newGame.setPlayerList(playerList);

		//TODO read mapIndex from players' preference
		Map<String, Long> occurrences =
				mapPreference.stream().collect(Collectors.groupingBy(s -> s, Collectors.counting()));
		Map.Entry<String, Long> max = occurrences.entrySet()
				.stream()
				.max(Comparator.comparing(Map.Entry::getValue)).orElseThrow(IllegalStateException::new);
		Logger.getGlobal().info("Map: "+max.getKey());
		newGame.initGameObjects(max.getKey());
		mapPreference = new ArrayList<>();

		this.waitingList.forEach(tuple -> {
			try {
				tuple.commandHandler.update(new Response(newGame, true, Constants.POWERUP));
			} catch (Observer.CommunicationError e) {
				logger.info(e.getMessage());
			}
		});
		this.gameList.add(newGame);
		this.waitingList = new ArrayList<>();
	}

	private void delayedGameCreation(int previousGameListSize) throws IndexOutOfBoundsException {
		logger.info("Starting game creation countdown (" + this.startTimerSeconds + "s)...");
		try {
			Thread.sleep((long) this.startTimerSeconds * 1000);
		} catch(InterruptedException e) {
			logger.info(e.toString());
			Thread.currentThread().interrupt();
		}
		if (previousGameListSize == this.gameList.size()) {
			createGame();
		}
	}


	public class IsClientAlive extends TimerTask {
		private String nickname;
		private CommandHandler commandHandler;
		private Timer timer;

		public IsClientAlive(String nickname, CommandHandler commandHandler, Timer timer) {
			this.nickname = nickname;
			this.commandHandler = commandHandler;
			this.timer = timer;
		}

		public void run(){
			try {
				this.commandHandler.update(new Response(null, true, "ping"));
			} catch (Observer.CommunicationError e) {
				logger.info("User " + this.nickname + " disconnected");
				//TODO: handle disconnection
				this.timer.cancel();
				this.timer.purge();
			}
		}
	}


	public void addUserToWaitingList(UserData newUser, CommandHandler currentCommandHandler) throws AlreadyPlayingException, IndexOutOfBoundsException {
		// add user to waiting list / game (used by view)
		if (isUserInGameList(newUser.getNickname()) || isUserInWaitingList(newUser.getNickname())) {
			throw new AlreadyPlayingException("User " + newUser.getNickname() + "is already playing or waiting!");
		}
		this.waitingList.add(new Tuple(newUser, currentCommandHandler));
		try {
			Timer timer = new Timer();
			timer.schedule(new IsClientAlive(newUser.getNickname(), currentCommandHandler, timer), 0, this.pingIntervalMilliseconds);
		} catch (IllegalArgumentException e) {
			logger.info(e.getMessage());
		}
		logger.info("Added user " + newUser.getNickname() + " to the waiting list, current waiting list size is " + this.waitingList.size() + " players");
		if (this.waitingList.size() == waitingListStartTimerSize) {
			new Thread(() -> delayedGameCreation(this.gameList.size())).start();
		}
		if (waitingList.size() >= waitingListMaxSize) {
			createGame();
		}
	}

	public List<Tuple> getWaitingList() {
		// TODO: is that method useful? Other classes cannot use Tuple type! ANSWER: No it is not useful for anybody
		return waitingList;
	}

	public List<String> getMapPreference() {
		return mapPreference;
	}

	public List<Game> getGameList() {
		return gameList;
	}
}