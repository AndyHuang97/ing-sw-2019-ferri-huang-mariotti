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
 * This class is used by te server tu manager multiple games. It's capable of creating new games for the players waiting
 * in queue for the start of a new match; saving the state the ames on the disck and much more. Check out the
 * documentation of each method for a full overview of what this class does.
 *
 * @author Federico Ferri
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
     * This class create an association between an commandHandler object and a userData object.
     * Tuple objects are used by the GameManager before the players are associated with a game (and before their
     * commandHandler is added to the  playerCommandHandlerMap.
     */
	public class Tuple {
		public final UserData userData;
		public final CommandHandler commandHandler;
		public Tuple(UserData userData, CommandHandler commandHander) {
			this.userData = userData;
			this.commandHandler = commandHander;
		}
	}

    /**
     * This constructor builds an empty GameManager object.
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
     * Serialize and dump the selected game to file.
     *
     * @param game the game that needs to be dumped
     */
	public void dumpToFile(Game game) {
		internalDumpToFile(game, false);
	}

    /**
     * Serialize and dump the selected game to file.
     *
     * @param game the game that needs to be dumped
     * @param deleteGame if true the selected game will be dumped and removed from the GameManager
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
			} else {
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
     * Check if the selected player is in the waiting list.
     *
     * @param nickname the nickname of the player that needs to be found in the waiting list
     * @return true if a player with the selected nickname is in waiting list, false otherwise
     */
	public boolean isUserInWaitingList(String nickname) {
		// used  to check if user is in waiting list (used by view)
		return waitingList.stream().anyMatch(tuple -> tuple.userData.getNickname().equals(nickname));
	}

    /**
     * Check if the selected player is playing in some game.
     *
     * @param nickname the nickname of the player that needs to be found
     * @return true if a player with the selected nickname is playing in some game, false otherwise
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
     * This exception is thrown when the GmaeManager is unable to retrive a game.
     */
	public class GameNotFoundException extends Exception {
		public GameNotFoundException(String errorMessage) {
			super(errorMessage);
		}
	}

    /**
     * Get the game where the player with the selected nickname is playing.
     *
     * @param nickname the nickname of the player
     * @return the game where the player with the selected nickname is playing
     * @throws GameNotFoundException the is no player with the selected nickname in any game
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
     * This exception should be thrown when a player that is already playing or is already in a waiting list
     * tries to join a game or a waiting list.
     */
	public class AlreadyPlayingException extends Exception {
		public AlreadyPlayingException(String errorMessage) {
			super(errorMessage);
		}
	}

    /**
     * Create a new game for the players in the waiting list. Starting with just a small set of Player objects this
     * method is able to add a new game and also register correctly the observers needed to run a game.
     *
     * @throws IndexOutOfBoundsException thrown if the waiting list contains more than five players
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
     * This method is used to call createGame() vith a certain delay, so that other players can connect during
     * the delay time and join the game before it starts.
     *
     * @param previousGameListSize the size of the waiting list before the countdown
     * @throws IndexOutOfBoundsException thrown if the waiting list contains more than five players
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
     * This method notifies players that the game is terminating and removes the game from the list of games.
     *
     * @param game the game that will be terminated
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
     * This method is called ny addUserToWaitingList() to implement it's functionality.
     *
     * @param newUser contains information about the new user
     * @param currentCommandHandler the command handler of the new user
     * @param ping true if the the GameManager needs to start a ping daemon for the new user
     * @throws AlreadyPlayingException thrown if the user is already laying in some game
     * @throws IndexOutOfBoundsException thrown if the waiting list contains more than five players
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
     * Start a ping daemon for the new user.
     *
     * @param nickname name of the player that needs the ping daemon
     * @param commandHandler command handler of the player
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
     * This method adds a new user to the waiting list and starts a ping daemon fr that player.
     *
     * @param newUser contains information about the new user
     * @param currentCommandHandler the command handler of the new user
     * @throws AlreadyPlayingException thrown if the user is already laying in some game
     * @throws IndexOutOfBoundsException thrown if the waiting list contains more than five players
     */
	public void addUserToWaitingList(UserData newUser, CommandHandler currentCommandHandler) throws AlreadyPlayingException, IndexOutOfBoundsException {
		internalAddUserToWaitingList(newUser, currentCommandHandler, true);
	}

    /**
     * This method adds a new user to the waiting list.
     *
     * @param newUser contains information about the new user
     * @param currentCommandHandler the command handler of the new user
     * @param ping true if the the GameManager needs to start a ping daemon for the new user
     * @throws AlreadyPlayingException thrown if the user is already laying in some game
     * @throws IndexOutOfBoundsException thrown if the waiting list contains more than five players
     */
	public void addUserToWaitingList(UserData newUser, CommandHandler currentCommandHandler, boolean ping) throws AlreadyPlayingException, IndexOutOfBoundsException {
		internalAddUserToWaitingList(newUser, currentCommandHandler, ping);
	}

    /**
     * Getter method for the waitingList attribute. Only for testing, should be avoided in production code.
     *
     * @return the list of player waiting for a game to be created
     */
	public List<Tuple> getWaitingList() {
		// used just in tests
		return waitingList;
	}


    /**
     * Getter method for the playerCommandHandlerMap attribute.
     *
     * @return a map that associates the nickname of a player with his commandHandler
     */
	public Map<String, CommandHandler> getPlayerCommandHandlerMap() {
		return playerCommandHandlerMap;
	}

    /**
     * Getter method for the mapPreference attribute.
     *
     * @return an index that represent the type of map chosen for the next game
     */
	public List<String> getMapPreference() {
		return mapPreference;
	}

    /**
     * Getter method for the gameList attribute.
     *
     * @return list of games managed by the GameManager
     */
	public List<Game> getGameList() {
		return gameList;
	}

    /**
     * Setter method for the controller attribute. Sets the controller for every game.
     *
     * @param controller the controller instance that will be used to handle every game managed by the GameManager
     */
	public void setController(Controller controller) {
		this.controller = controller;
	}
}