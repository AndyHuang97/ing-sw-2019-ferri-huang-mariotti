package it.polimi.se2019.server.games;

import com.google.gson.Gson;
import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.controller.Controller;
import it.polimi.se2019.server.controller.ControllerState;
import it.polimi.se2019.server.controller.WaitingForMainActions;
import it.polimi.se2019.server.controller.WaitingForRespawn;
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

/**
 * The GameManager provides all the methods to manage a game, to start it, to end it, etc
 * Not only that, the gameManager also stores the games in a list to allow multiple games in parallel and provides
 * the correct methods to access a specific game.
 * Last the gameManger also saves the games to file, this is to allow the games to be restored in case of server crash
 *
 */
public class GameManager {
	private static final Logger logger = Logger.getLogger(GameManager.class.getName());
	private List<Game> gameList;
	private int waitingListMaxSize;
	private int waitingListStartTimerSize;
	private int startTimerSeconds;
	private List<Tuple> waitingList;
	private Map<String, CommandHandler> playerCommandHandlerMap;
	private List<String> mapPreference;
	private String dumpName;
	private int pingIntervalMilliseconds;
	private Controller controller;

	/**
	 * A tuple data structure used to store the related data together
	 *
	 */
	public class Tuple<X, Y> {
		public final UserData userData;
		public final CommandHandler commandHandler;
		public Tuple(UserData userData, CommandHandler commandHander) {
			this.userData = userData;
			this.commandHandler = commandHander;
		}
	}

	/**
	 * The default constructor, nothing special happens here
	 *
	 */
	public GameManager() {
		this.gameList = new ArrayList<>();
		this.waitingList = new ArrayList<>();
		this.mapPreference = new ArrayList<>();
		this.playerCommandHandlerMap = new HashMap<>();
	}

	/**
	 * This is the real magic, it initializes the game manager by loading the settings from file and by loading the saved
	 * games from disk. Loading takes a few lines due to the complex recreation of the observer/observable pattern that gets destroyed
	 * during serialization
	 *
	 * @param dumpName its the name of the dump file
	 *
	 */
	public void init(String dumpName) {
		try (InputStream input = GameManager.class.getClassLoader().getResource("config.properties").openStream()) {
			Properties prop = new Properties();
			// load a properties file
			prop.load(input);
			waitingListMaxSize = Integer.parseInt(prop.getProperty("game_manager.waiting_list_max_size"));
			waitingListStartTimerSize = Integer.parseInt(prop.getProperty("game_manager.waiting_list_start_timer_size"));
			startTimerSeconds = Integer.parseInt(prop.getProperty("game_manager.start_timer_seconds"));
			pingIntervalMilliseconds = Integer.parseInt(prop.getProperty("game_manager.ping_interval_milliseconds"));
		} catch (IOException ex) {
			logger.info(ex.toString());
		}
		this.dumpName = dumpName;
		try {
			if (GameManager.class.getClassLoader().getResource(dumpName) != null) {
				logger.info("Loading saved games from: " + GameManager.class.getClassLoader().getResource(dumpName).toString());
				BufferedReader br = new BufferedReader(new InputStreamReader(GameManager.class.getClassLoader().getResource(dumpName).openStream()));
				//Read JSON file
				List<Game> tmpGameList = new ArrayList<>();
				try {
					Gson gson = new Gson();
					tmpGameList = new ArrayList<>(Arrays.asList(gson.fromJson(br, Game[].class)));
				} finally {
					br.close();
				}
				// this is to rebuild the games with all the notify etc
				tmpGameList.forEach(tmpGame -> {
					logger.info("Restoring a new game with users: " + String.join(", ", tmpGame.getPlayerList().stream().map(player -> player.getUserData().getNickname()).collect(Collectors.toList())));
					// first we build the players
					List<Player> newPlayerList = new ArrayList<>();
					tmpGame.getPlayerList().forEach(tmpPlayer -> {
						CharacterState tmpCharacterState = tmpPlayer.getCharacterState();
						CharacterState newCharacterState = new CharacterState(tmpCharacterState.getDeaths(), tmpCharacterState.getValueBar(),
								tmpCharacterState.getDamageBar(), tmpCharacterState.getMarkerBar(), tmpCharacterState.getAmmoBag(),
								tmpCharacterState.getWeaponBag(), tmpCharacterState.getPowerUpBag(), tmpCharacterState.getTile(),
								tmpCharacterState.getScore(), false, tmpCharacterState.getColor());
						Player newPlayer = new Player(tmpPlayer.getId(), false, tmpPlayer.getUserData(), newCharacterState, tmpPlayer.getColor());
						newPlayerList.add(newPlayer);
					});
					// then the kill shot tracker
					KillShotTrack newKillShotTrack = new KillShotTrack(tmpGame.getKillShotTrack().getDeathTrack(), newPlayerList, tmpGame.getKillShotTrack().getKillCounter());
					// then the game
					Game newGame = new Game(tmpGame.getStartDate(), newPlayerList, newPlayerList.stream().filter(player -> player.getId().equals(tmpGame.getCurrentPlayer().getId())).findAny().orElseThrow(() -> new NullPointerException("player not found")),
							tmpGame.getBoard(), newKillShotTrack, tmpGame.getWeaponDeck(), tmpGame.getPowerUpDeck(), tmpGame.getAmmoCrateDeck());
					// at the end we register all we need to register
					newGame.getPlayerList().forEach(newPlayer -> {
						// register game in the player
						newPlayer.register(newGame);
						newPlayer.getCharacterState().register(newGame);
					});
					newGame.getBoard().register(newGame);
					newKillShotTrack.register(newGame);
					gameList.add(newGame);
				});
				logger.info("Done with restoration!");
			} else {
				logger.info("No gamefile, skip loading saved games!");
			}
		} catch (IOException | NullPointerException ex) {
			ex.printStackTrace();
			logger.info("Error while loading gamefile, skip loading saved games!");
		}
	}

	/**
	 * One of the methods to dump to file, it can put a new game to file or update an existing game to file
	 *
	 * @param game is the game object
	 *
	 */
	public void dumpToFile(Game game) {
		internalDumpToFile(game, false);
	}

	/**
	 * The other methods to dump to file, it can put a new game to file or update an existing game to file and delete it
	 *
	 * @param game is the game object
	 * @param deleteGame the action I want to perform
	 *
	 */
	public void dumpToFile(Game game, boolean deleteGame) {
		internalDumpToFile(game, deleteGame);
	}

	/**
	 * This is the real file dumper, it first loads the saved games from file, it the analyzes the command by searching
	 * for an existing save of the game by matching the date of when the game started that can be considered unique.
	 * In case it becomes a problem the requirement of uniqueness we can switch to uuid. After having located the previous
	 * game sae it gets replaced by the new one or deleted if specified. After all of this it saves back to file.
	 *
	 * @param game is the game object
	 * @param deleteGame the action I want to perform
	 *
	 */
	private void internalDumpToFile(Game game, boolean deleteGame) {
		try {
			List<Game> tmpGameList = new ArrayList<>();
			if (GameManager.class.getClassLoader().getResource(dumpName) != null) {
				BufferedReader br = new BufferedReader(new InputStreamReader(GameManager.class.getClassLoader().getResource(dumpName).openStream()));
				//Read JSON file
				try {
					Gson gson = new Gson();
					tmpGameList = new ArrayList<>(Arrays.asList(gson.fromJson(br, Game[].class)));
				} finally {
					br.close();
				}
			}
			if (tmpGameList.stream().anyMatch(tGame -> tGame.getStartDate().toString().equals(game.getStartDate().toString()))) {
				for (int i = 0; i < tmpGameList.size(); i++) {
					if (tmpGameList.get(i).getStartDate().toString().equals(game.getStartDate().toString())) {
						if (deleteGame) {
							logger.info("Removing a game from file, location: " + GameManager.class.getClassLoader().getResource(dumpName).getPath());
							tmpGameList.set(i, null);
						} else {
							logger.info("Updating a game to file, location: " + GameManager.class.getClassLoader().getResource(dumpName).getPath());
							tmpGameList.set(i, game);
						}
					}
				}
				while (tmpGameList.remove(null)) {}
			} else if (!deleteGame) {
				logger.info("Saving a new game to file, location: " + GameManager.class.getClassLoader().getResource(dumpName).getPath());
				tmpGameList.add(game);
			}
			FileWriter writer = new FileWriter(new File(GameManager.class.getClassLoader().getResource(dumpName).getPath()));
			// Write file
			try {
				Gson gson = new Gson();
				writer.write(gson.toJson(tmpGameList.toArray()));
			} finally {
				writer.close();
			}
		} catch (Exception ex) {
			logger.info("Error while saving games to file");
		}
	}

	/**
	 * A check to control if a user is in the waiting list already
	 *
	 * @param nickname the nickname to check
	 * @return if a user is in the waiting list
	 *
	 */
	public boolean isUserInWaitingList(String nickname) {
		// used  to check if user is in waiting list (used by view)
		return waitingList.stream().anyMatch(tuple -> tuple.userData.getNickname().equals(nickname));
	}

	/**
	 * A check to control if a user is in a game
	 *
	 * @param nickname the nickname to check
	 * @return if a user is in a game
	 *
	 */
	public boolean isUserInGameList(String nickname) {
		// used  to check if user is in waiting list (used by view)
		return gameList.stream().anyMatch(
				game -> game.getPlayerList().stream().anyMatch(
						player -> player.getUserData().getNickname().equals(nickname)
				)
		);
	}

	/**
	 * The exception for a game not found
	 *
	 */
	public class GameNotFoundException extends Exception {
		public GameNotFoundException(String errorMessage) {
			super(errorMessage);
		}
	}

	/**
	 * This is a very important, retrieves the game by nickname
	 *
	 * @param nickname the nickname of one of the players
	 * @return the corresponding game
	 *
	 */
	public Game retrieveGame(String nickname) throws GameNotFoundException {
		// used to check if user is in a game (used by view)
		return gameList.stream().filter(
				game -> game.getPlayerList().stream().anyMatch(
						player -> player.getUserData().getNickname().equals(nickname)
				)
		).findAny().orElseThrow(() -> new GameNotFoundException("Nickname " + nickname + " has no games available!"));
	}

	/**
	 * This exception is thrown when you perform some illegal actions on a player already in a game
	 *
	 */
	public class AlreadyPlayingException extends Exception {
		public AlreadyPlayingException(String errorMessage) {
			super(errorMessage);
		}
	}

	/**
	 * This starts a new game, first it checks that there are not too many players, then it starts creating the players,
	 * it links the observable/observers to then receive the updates. It stores che command handlers of the players, it initializes the deck,
	 * cards, board etc.
	 * Last it delivers the game to all clients and picks the starting client. Then it resets the waiting list and the map preference board.
	 *
	 */
	public void createGame() throws IndexOutOfBoundsException {
		logger.info("Starting a new game");
		//create the new game and reset waiting list, do not use it
		List<Player> playerList = new ArrayList<>();
		Game newGame = new Game(playerList);
		waitingList.forEach(tuple -> {
			PlayerColor color = Stream.of(PlayerColor.values()).filter(
					playerColor -> playerList.stream().noneMatch(player -> player.getColor().equals(playerColor))
			).findAny().orElseThrow(() -> new IndexOutOfBoundsException("Too many players!"));

			CharacterState characterState = new CharacterState(color);
			Player player = new Player(UUID.randomUUID().toString(), true, tuple.userData, characterState, color);
			playerList.add(player);

			// register game in the player and his commandHandler
			player.register(newGame);
			characterState.register(newGame);
			// register all commandHandlers to the game
			newGame.register(tuple.commandHandler);
			playerCommandHandlerMap.put(tuple.userData.getNickname(),tuple.commandHandler);
		});

		Map<String, Long> occurrences = mapPreference.stream().collect(Collectors.groupingBy(s -> s, Collectors.counting()));
		Map.Entry<String, Long> max = occurrences.entrySet()
				.stream()
				.max(Comparator.comparing(Map.Entry::getValue)).orElseThrow(IllegalStateException::new);
		Logger.getGlobal().info("Map: "+max.getKey());
		newGame.initGameObjects(max.getKey());
        Logger.getGlobal().info("Game objects were loaded");

		newGame.getBoard().register(newGame);
        newGame.getKillShotTrack().register(newGame);

		mapPreference = new ArrayList<>();

		this.waitingList.forEach(tuple -> {
			try {
				tuple.commandHandler.register(controller);
				tuple.commandHandler.update(new Response(newGame, true, ""));
				if (tuple.userData.getNickname().equals(newGame.getCurrentPlayer().getUserData().getNickname())) {
					tuple.commandHandler.update(new Response(null, true, Constants.RESPAWN));
				}
				Logger.getGlobal().info("Initialized game was broadcasted");
			} catch (Observer.CommunicationError e) {
				logger.info(e.getMessage());
			}
		});
		gameList.add(newGame);
		waitingList = new ArrayList<>();
		dumpToFile(newGame);
	}

	/**
	 * This is used to start the countdown when you want to create a game, it sleeps and after that it checks
	 * if conditions are still valid and creates the game
	 *
	 * @param previousGameListSize the old size list as a reference
	 *
	 */
	private void delayedGameCreation(int previousGameListSize) throws IndexOutOfBoundsException {
		logger.info("Starting game creation countdown (" + startTimerSeconds + "s)...");
		try {
			Thread.sleep((long) startTimerSeconds * 1000);
		} catch(InterruptedException e) {
			logger.info(e.getMessage());
		}
		if (previousGameListSize == gameList.size() && waitingList.size() >= 3) {
			createGame();
		}
	}

	/**
	 * This destroys a game and removes it from file.
	 *
	 * @param game the game to terminate
	 *
	 */
	public void terminateGame(Game game) {
		logger.info("Terminating a game, users will be notified");
		game.getPlayerList().forEach(p -> {
			try {
				playerCommandHandlerMap.get(p.getUserData().getNickname()).update(new Response(null, true, Constants.FINISHGAME));
			} catch (Exception ex) {
				logger.info("Cannot notify user " + p.getUserData().getNickname() + " of game end");
			}
			playerCommandHandlerMap.remove(p.getUserData().getNickname());
		});
		gameList.remove(game);
		dumpToFile(game, true);
	}

	/**
	 * The pinger daemon is a very classical ping pong mechanism to check if a client is alive. Its protocol independent
	 * so it works with both rmi and socket. There also is the implementation of what to do when a client fails to respond
	 * usually it can quit the game or just disconnect it. It is implemented as a timer that runs at an interval.
	 *
	 */
	public class IsClientAlive extends TimerTask {
		private String nickname;
		private CommandHandler commandHandler;
		private Timer timer;

		/**
		 * The constructor of the class
		 *
		 * @param nickname the user nickname (to identify the game)
		 * @param commandHandler the command handler to check if connection is alive
		 * @param timer the timer object
		 *
		 */
		public IsClientAlive(String nickname, CommandHandler commandHandler, Timer timer) {
			this.nickname = nickname;
			this.commandHandler = commandHandler;
			this.timer = timer;
		}

		/**
		 * The run block of the timer, if first sends a ping if the response is pong it just comes back to sleep. If no
		 * response it checks if the user is in a game. If so it checks if the game is a 3 or less players game. If it is it
		 * shuts the game down. If not it just disconnects the player form the game. If user is not connected to any game, it just frees
		 * the waiting list
		 *
		 */
		public void run(){
		    try {
                try {
                    commandHandler.ping();
                } catch (Observer.CommunicationError ex) {
					timer.cancel();
					timer.purge();
                    // check if user is in waiting list or not
                    if (isUserInGameList(nickname)) {
						Game currentGame = retrieveGame(nickname);
						currentGame.deregister(commandHandler);
						playerCommandHandlerMap.remove(nickname);
						// in case of less than 3 players quit the game and announce the winner etc
						if (currentGame.getActivePlayerList().size() <= 3) {
							logger.info("User " + nickname + " disconnected, game is going to terminate");
							currentGame.getPlayerByNickname(nickname).setActive(false);
							terminateGame(currentGame);
						} else {
							logger.info("User " + nickname + " disconnected, game is going to continue");
							currentGame.getPlayerByNickname(nickname).setActive(false);
							if (currentGame.getCurrentPlayer().getUserData().getNickname().equals(nickname)) {
								ControllerState newControllerState;
								if (currentGame.getActivePlayerList().stream().anyMatch(p -> p.getCharacterState().isDead())) {
									WaitingForRespawn newState = new WaitingForRespawn();
									Logger.getGlobal().info("Someone was killed");
									newControllerState = newState.nextState(new ArrayList<>(), currentGame, currentGame.getPlayerByNickname(nickname));
								} else {
									currentGame.updateTurn();
									if (currentGame.getCurrentPlayer().getCharacterState().isFirstSpawn()) {
										Logger.getGlobal().info("No one was killed, first spawn");
										newControllerState = new WaitingForRespawn();
									} else {
										Logger.getGlobal().info("No one was killed, not first spawn");
										newControllerState = new WaitingForMainActions();
									}
								}
								Logger.getGlobal().info("Next current player is: " + currentGame.getCurrentPlayer().getUserData().getNickname());
								Logger.getGlobal().info("Next state is " + newControllerState.getClass().getSimpleName());
								controller.setControllerStateForGame(currentGame, newControllerState);
								controller.requestUpdate(currentGame);
								Logger.getGlobal().info("Trying to send a selection message");
								CommandHandler nextCommandHandler = playerCommandHandlerMap.get(currentGame.getCurrentPlayer().getUserData().getNickname());
								newControllerState.sendSelectionMessage(nextCommandHandler);
							}
						}
                    } else {
                    	for (int i = 0; i < waitingList.size(); i++) {
                    		if (waitingList.get(i).userData.getNickname().equals(nickname)) {
                    			waitingList.remove(i);
							}
						}
						playerCommandHandlerMap.remove(nickname);
						logger.info("User " + nickname + " disconnected from the waiting list or from a terminated game, current waiting list size is " + waitingList.size() + " players");
					}
                }
            } catch (Exception ex) {
		        logger.info("A ping daemon died, probably a game ended.");
            }
		}
	}

	/**
	 * This parts add users to the waiting list. First a routine check to verify user is not already doing something.
	 * If negative, it starts the pinger daemon and adds the user to the waiting list. If the waiting list is more than 3 players
	 * it starts the countdown, if more than 5 it starts the game.
	 *
	 * @param newUser just the userdata
	 * @param currentCommandHandler his command handler
	 * @param ping if you want the pinger to be active, dont use false if you dont know what you are doing
	 *
	 */
	private void internalAddUserToWaitingList(UserData newUser, CommandHandler currentCommandHandler, boolean ping) throws AlreadyPlayingException, IndexOutOfBoundsException {
		// add user to waiting list / game (used by view)
		if (isUserInGameList(newUser.getNickname()) || isUserInWaitingList(newUser.getNickname())) {
			throw new AlreadyPlayingException("User " + newUser.getNickname() + "is already playing or waiting!");
		}
		waitingList.add(new Tuple(newUser, currentCommandHandler));
		if (ping) {
			startPingDaemon(newUser.getNickname(), currentCommandHandler);
		}
		logger.info("Added user " + newUser.getNickname() + " to the waiting list, current waiting list size is " + waitingList.size() + " players");
		if (waitingList.size() == waitingListStartTimerSize) {
			new Thread(() -> delayedGameCreation(gameList.size())).start();
		}
		if (waitingList.size() >= waitingListMaxSize) {
			createGame();
		}
	}

	/**
	 * This is where the pinger daemon is initialized, just a timer creation and a scheduling
	 *
	 * @param nickname just the userdata
	 * @param commandHandler his command handler
	 *
	 */
	public void startPingDaemon(String nickname, CommandHandler commandHandler) {
		try {
			Timer timer = new Timer();
			timer.schedule(new IsClientAlive(nickname, commandHandler, timer), 0, pingIntervalMilliseconds);
		} catch (IllegalArgumentException e) {
			logger.info(e.getMessage());
		}
	}

	/**
	 * Just the nice entrance to the internal function
	 *
	 * @param newUser just the userdata
	 * @param currentCommandHandler his command handler
	 *
	 */
	public void addUserToWaitingList(UserData newUser, CommandHandler currentCommandHandler) throws AlreadyPlayingException, IndexOutOfBoundsException {
		internalAddUserToWaitingList(newUser, currentCommandHandler, true);
	}

	/**
	 * Just the nice entrance to the internal function
	 *
	 * @param newUser just the userdata
	 * @param currentCommandHandler his command handler
	 * @param ping if you want the pinger to be active, dont use false if you dont know what you are doing
	 *
	 */
	public void addUserToWaitingList(UserData newUser, CommandHandler currentCommandHandler, boolean ping) throws AlreadyPlayingException, IndexOutOfBoundsException {
		internalAddUserToWaitingList(newUser, currentCommandHandler, ping);
	}

	/**
	 * Get the waiting list as a list of tuple
	 *
	 * @return returns the waiting list
	 *
	 */
	public List<Tuple> getWaitingList() {
		// used just in tests
		return waitingList;
	}

	/**
	 * Get the command handler map
	 *
	 * @return the command handler map
	 *
	 */
	public Map<String, CommandHandler> getPlayerCommandHandlerMap() {
		return playerCommandHandlerMap;
	}

	/**
	 * Get the list of map preferences
	 *
	 * @return the list of map preferences
	 *
	 */
	public List<String> getMapPreference() {
		return mapPreference;
	}

	/**
	 * Get the list of games
	 *
	 * @return the list of games
	 *
	 */
	public List<Game> getGameList() {
		return gameList;
	}

	/**
	 * Set the controller
	 *
	 * @param controller the controller
	 *
	 */
	public void setController(Controller controller) {
		this.controller = controller;
	}
}