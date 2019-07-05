package it.polimi.se2019.server.games;

import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.cards.ammocrate.AmmoCrate;
import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.dataupdate.*;
import it.polimi.se2019.server.deserialize.DirectDeserializers;
import it.polimi.se2019.server.exceptions.PlayerNotFoundException;
import it.polimi.se2019.server.games.board.Board;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.AmmoColor;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.util.CommandConstants;
import it.polimi.se2019.util.Observable;
import it.polimi.se2019.util.Response;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class contains data and logic of a game. In the MVC pattern this class implements a big chunk of the
 * Model, the Observer pattern is used to communicate data updates to the view. This class also forwards the changes
 * of other parts of the model lo the view.
 *
 * @author Rodolfo Mariotti
 */
public class Game extends Observable<Response> implements it.polimi.se2019.util.Observer<Response>, Serializable {

	private Date startDate;
	private List<Player> playerList;
	private Player currentPlayer;
	private Board board;
	private KillShotTrack killShotTrack;
	private Deck<Weapon> weaponDeck;
	private Deck<PowerUp> powerUpDeck;
	private Deck<AmmoCrate> ammoCrateDeck;
	private List<ActionUnit> currentActionUnitsList;
	private Weapon currentWeapon;
	private Set<Targetable> cumulativeDamageTargetSet;
	private Set<Targetable> cumulativeTargetSet;
	private boolean frenzy;

	// needed to access the player position before an action is run
	private Tile virtualPlayerPosition;

	// needed to recover used cards when the decks run out of cards
	private List<PowerUp> usedPowerUps = new ArrayList<>();
	private List<Weapon> usedWeapons = new ArrayList<>();


    /**
     * Use this constructor to initialize an empty game. It's used mainly for testing purposes.
     */
	public Game() {
		this.startDate = new Date();
		this.playerList = new ArrayList<>();
		this.currentPlayer = null;
		this.board = new Board();
		this.killShotTrack = new KillShotTrack(playerList);
		this.weaponDeck = null;
		this.powerUpDeck = null;
		this.ammoCrateDeck = null;
		this.currentActionUnitsList = new ArrayList<>();
		this.currentWeapon = null;
		this.cumulativeDamageTargetSet = new HashSet<>();
		this.cumulativeTargetSet = new HashSet<>();
		this.frenzy = false;
	}

    /**
     * This constructor is used to initialize a Game. It's used by the Game Menager during the initialization of the
     * game.
     *
     * @param playerList list of the players in the game
     */
	public Game(List<Player> playerList) {
		this.startDate = new Date();
		this.playerList = playerList;
		this.currentPlayer = null;
		this.board = new Board();
		this.killShotTrack = new KillShotTrack(playerList);
		this.weaponDeck = null;
		this.powerUpDeck = null;
		this.ammoCrateDeck = null;
		this.currentActionUnitsList = new ArrayList<>();
		this.currentWeapon = null;
		this.cumulativeDamageTargetSet = new HashSet<>();
		this.cumulativeTargetSet = new HashSet<>();
		this.frenzy = false;
	}

    /**
     * This constructor should be used to resume a saved games. It let's you specify every attribute of the object.
     *
     * @param startDate the date when the game started
     * @param playerList the list of players in the game
     * @param currentPlayer the player who is playing his turn
     * @param board data structure used to manage the game map
     * @param killShotTrack used to track kills of the players
     * @param weaponDeck deck containing the cards that will spawn in weapon crates
     * @param powerUpDeck deck containing power up cards used by players to respawn and apply varius effects
     * @param ammoCrateDeck deck containing ammo cards that will spawn in ammo crates
     */
	public Game(Date startDate, List<Player> playerList, Player currentPlayer, Board board, KillShotTrack killShotTrack, Deck<Weapon> weaponDeck, Deck<PowerUp> powerUpDeck, Deck<AmmoCrate> ammoCrateDeck) {
		this.startDate = startDate;
		this.playerList = playerList;
		this.currentPlayer = currentPlayer;
		this.board = board;
		this.killShotTrack = killShotTrack;
		this.weaponDeck = weaponDeck;
		this.powerUpDeck = powerUpDeck;
		this.ammoCrateDeck = ammoCrateDeck;
		this.currentActionUnitsList = new ArrayList<>();
		this.currentWeapon = null;
		this.cumulativeDamageTargetSet = new HashSet<>();
		this.cumulativeTargetSet = new HashSet<>();
	}

    /**
     * This method is used to setup various game attributes. It's used to avoid a bloated constructor.
     * It's run by GameManager while setting up a new game.
     *
     * @requires mapIndex >= 0 && mapIndex <= 3;
     * @param mapIndex the index of the map that will be initialized
     */
	public void initGameObjects(String mapIndex) {
	    // trick to avoid notify before everything is initialized
		this.currentPlayer = playerList.get(0);
		this.killShotTrack = new KillShotTrack(playerList);

		new DirectDeserializers();
		this.setBoard(DirectDeserializers.deserializeBoard(mapIndex));
		this.setAmmoCrateDeck(DirectDeserializers.deserializeAmmoCrate());
		this.setWeaponDeck(DirectDeserializers.deserialzerWeaponDeck());
		this.setPowerUpDeck(DirectDeserializers.deserialzerPowerUpDeck());
		this.getWeaponDeck().shuffle();
		this.getAmmoCrateDeck().shuffle();
		this.getPowerUpDeck().shuffle();

		initBoard();
		initPlayerPowerUps();
		initPlayerAmmoBag();
	}

    /**
     * Method used during the initialization of the game to draw ammo crates and weapon crates from decks
     * and spawn them.
     */
	private void initBoard() {
		this.getBoard().getTileList().stream()
				.filter(Objects::nonNull)
				.filter(t -> t.isSpawnTile())
				.forEach(t -> {
					for (int i=0; i<3; i++) {
					    t.getWeaponCrate().add(drawWeaponFromDeck());
					}
				});
		this.getBoard().getTileList().stream()
				.filter(Objects::nonNull)
				.filter(t-> !t.isSpawnTile())
				.forEach(t -> t.setAmmoCrate(drawAmmoCrateFromDeck()));
	}


    /**
     * method used during the initialization of the game to draw power up cards form decks and spawn them.
     */
	private void initPlayerPowerUps() {
		this.getPlayerList().stream()
				.forEach(p -> {
					for (int i=0; i<2; i++) {
						givePowerUpToPlayer(p);
					}
				});

	}

    /**
     * This method is used to spawn one of each ammo in the player's ammo bag during the initialization of the game
     */
	private void initPlayerAmmoBag() {
		this.getPlayerList().stream()
				.forEach(p -> {
					Stream.of(AmmoColor.values()).forEach(color -> p.getCharacterState().getAmmoBag().put(color,1));
				});
	}

    /**
     * This method is used to add a power up (drawn from the deck) in the player's bag.
     * @param player player that will draw a power up
     */
	private void givePowerUpToPlayer(Player player) {
		player.getCharacterState().getPowerUpBag().add(drawPowerupFromDeck());
	}

    /**
     * This method is used to handle every actions that mus be run between turns. It updates the value of current player
     * to the next player and spawns used ammo crates and weapon crates.
     */
	public void updateTurn() {
		nextCurrentPlayer();

		for (Tile tile : getBoard().getTileList()) {
			if (tile != null) {
				if (tile.isSpawnTile()) {
					List<Weapon> weaponCrate = tile.getWeaponCrate();

					List<Weapon> updatedWeaponCrate = new ArrayList<>(weaponCrate);

					while (updatedWeaponCrate.size() < 3) {
					    updatedWeaponCrate.add(drawWeaponFromDeck());
                    }

					getBoard().setWeaponCrate(tile.getxPosition(), tile.getyPosition(), updatedWeaponCrate);
				} else {
					if (tile.getAmmoCrate() == null) {
						getBoard().setAmmoCrate(tile.getxPosition(), tile.getyPosition(), drawAmmoCrateFromDeck());
					}
				}
			}
		}
	}

    /**
     * Getter method for the current player attribute.
     *
     * @return a reference to the currentPlayer
     */
	public Player getCurrentPlayer() {
		return currentPlayer;
	}

    /**
     * Setter methods for the current player attribute. Notifies registered observers.
     *
     * @requires currentPlayer.getActive == true;
     * @param currentPlayer reference to the player that needs to be set as current player
     */
	public void setCurrentPlayer(Player currentPlayer) {
        if (currentPlayer.getActive()) this.currentPlayer = currentPlayer;
        else throw new IllegalStateException();

        CurrentPlayerStateUpdate currentPlayerUpdate = new CurrentPlayerStateUpdate(currentPlayer);

        Response request = new Response(Arrays.asList(currentPlayerUpdate));
        notify(request);

	}

    /**
     * When this method is run the curren player will be set to the next player in the players list.
     */
	public void nextCurrentPlayer() {
		// this takes into account if the next player is active or not
		int newIndex = playerList.indexOf(this.currentPlayer) + 1;
		if(newIndex >= playerList.size()) {newIndex = 0;}
		while (!playerList.get(newIndex).getActive()) {
			newIndex++;
			if(newIndex >= playerList.size()) {newIndex = 0;}
		}
		setCurrentPlayer(playerList.get(newIndex));
	}

    /**
     * Getter method for the startDate attribute
     *
     * @return the date of the game start
     */
	public Date getStartDate() {
		return startDate;
	}

    /**
     * Getter method for the startDate attribute
     *
     * @param startDate the date of the game start
     */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

    /**
     * Getter method for the player list
     *
     * @return list of the players
     */
	public List<Player> getPlayerList() {
		return playerList;
	}

    /**
     * Filter the list of player to show only active players.
     *
     * @return list of active players
     */
	public List<Player> getActivePlayerList() {
		return playerList.stream().filter(Player::getActive).collect(Collectors.toList());
	}

    /**
     * Setter method fot the player list.
     *
     * @param playerList reference to the list of player that will be set as payer list of the game
     */
	public void setPlayerList(List<Player> playerList) {
		this.playerList = playerList;
	}

    /**
     * This method is called at the ond of the game to get a list of player sorted from the one with the higher
     * score to the one with the lower.
     *
     * @return a sorted list of player (sorted for decreasing score)
     */
	public List<Player> getRanking() {
		List<Player> ranking = new ArrayList<>();
		ranking.addAll(playerList);

		// it's the end of the game, get points from the players who got damages on their boards
        killShotTrack.killPlayersAndGetScore(playerList);
        // the score of each player has been updated, time to calculate the bonus points on the KillShotTrack
        Map<PlayerColor, Integer> killShotTrackBonusPoints = killShotTrack.calculateScore();

        if (!killShotTrackBonusPoints.isEmpty()) {
            for (Map.Entry<PlayerColor, Integer> entry : killShotTrackBonusPoints.entrySet()) {
                CharacterState characterState = getPlayerByColor(entry.getKey()).getCharacterState();

                characterState.setScore(characterState.getScore() + entry.getValue());
            }
        }

        // the final players scores have been calculated, let's sort the array
		Comparator<Player> scoreComparator = (p1, p2) ->  p1.getCharacterState().getScore().compareTo(p2.getCharacterState().getScore());
		ranking.sort(scoreComparator.reversed());

		return ranking;
	}

	/**
     * Get the player of the specified color from the player list
     *
	 * @param color color of the requested player
	 * @return reference to the requested player
	 */
	public Player getPlayerByColor(PlayerColor color) {
		Optional<Player> optPlayer = getPlayerList().stream()
				.filter(p -> p.getColor() == color)
				.findFirst();
		return optPlayer.orElse(null);
	}

    /**
     * Gets a list of every color playing
     *
     * @return list containing the color of each player
     */
	public List<PlayerColor> getActiveColors() {
		return getPlayerList().stream()
				.map(p -> p.getColor())
				.collect(Collectors.toList());
	}

    /**
     * Getter method for the board attribute.
     *
     * @return reference to the board of the game
     */
	public Board getBoard() {
		return board;
	}

    /**
     * Setter method for the board attribute.
     *
     * @param board reference to the object that will be set as board of this game
     */
	public void setBoard(Board board) {
		this.board = board;
	}

    /**
     * Get the player with the specified nickname from the player list.
     *
     * @param nickname nickname of the requested player
     * @return reference to the requested player
     * @throws PlayerNotFoundException the given nickname does not mach any of the player of the game
     */
	public Player getPlayerByNickname(String nickname) throws PlayerNotFoundException {
	    for (Player player : playerList) {
	        if (player.getUserData().getNickname().equals(nickname)) {
	            return player;
            }
        }

        throw new PlayerNotFoundException();
    }

    /**
     * Getter method for the killShotTrack attribute.
     *
     * @return reference to the kill shot track attribute
     */
	public KillShotTrack getKillShotTrack() {
		return killShotTrack;
	}

    /**
     * Setter method for the killShotTrack attribute. Notifies registered observers.
     *
     * @param killShotTrack reference to the object that will be set as kill shot track for the game
     */
	public void setKillShotTrack(KillShotTrack killShotTrack) {
		this.killShotTrack = killShotTrack;

		// notify KillShotTrack change
        KillShotTrackUpdate killShotTrackUpdate = new KillShotTrackUpdate(killShotTrack);

        List<StateUpdate> updateList = new ArrayList<>();
        updateList.add(killShotTrackUpdate);

        Response response = new Response(updateList);
        notify(response);
	}

    /**
     * Getter method for the weaponDeck attribute.
     *
     * @return reference to the weapon deck of the game
     */
	public Deck<Weapon> getWeaponDeck() {
		return weaponDeck;
	}

    /**
     * Setter method for the weaponDeck attribute.
     *
     * @param weaponDeck reference to the object that will be set as weapon deck of the game
     */
	public void setWeaponDeck(Deck<Weapon> weaponDeck) {
		this.weaponDeck = weaponDeck;
	}

    /**
     * Getter method for the powerUpDeck attribute.
     *
     * @return reference to the power up deck of the game
     */
	public Deck<PowerUp> getPowerUpDeck() {
		return powerUpDeck;
	}

    /**
     * Setter method for the powerUpDeck attribute.
     *
     * @param powerUpDeck reference to the object that will be set as power up deck of the game
     */
	public void setPowerUpDeck(Deck<PowerUp> powerUpDeck) {
		this.powerUpDeck = powerUpDeck;
	}


    /**
     * Getter method for the currentWeapon attribute.
     *
     * @return reference to the current weapon of the game. It's the weapon that the player selected to use
     *         in this turn (can be set more than one time in a turn, in order to use different weapons).
     */
	public Weapon getCurrentWeapon() {
		return currentWeapon;
	}

    /**
     * Setter method for the currentWeapon attribute. Notifies registered observers.
     *
     * @param currentWeapon reference to the object that will be set as current weapon.
     */
	public void setCurrentWeapon(Weapon currentWeapon) {
		this.currentWeapon = currentWeapon;

		StateUpdate currentWeaponUpdate = new CurrentWeaponUnpdate(currentWeapon);
        Response response = new Response(Arrays.asList(currentWeaponUpdate));

        notify(response);
	}

    /**
     * Getter method for the currentActionUnitsList attribute.
     *
     * @return reference to the list of the action units executed during this turn
     */
	public List<ActionUnit> getCurrentActionUnitsList() {
		return currentActionUnitsList;
	}

    /**
     * Setter method for the currentActionUnitsList attribute.
     *
     * @param currentActionUnitsList reference to the object that will be set as current action unit list of the game.
     */
	public void setCurrentActionUnitsList(List<ActionUnit> currentActionUnitsList) {
		this.currentActionUnitsList = currentActionUnitsList;
	}

    /**
     * Getter method for the frenzy attribute.
     *
     * @return true if the game is in frenzy mode, false otherwise
     */
	public boolean isFrenzy() {
		return frenzy;
	}

    /**
     * This method is used to set the game in frenzy mode. The players will be divided in players before the frenzy
     * activator and players after the frenzy activator; the value bar of the players without damage will be swapped.
     *
     * @param frenzy true to set the game in frenzy mode, false to unset frenzy mode
     */
	public void setFrenzy(boolean frenzy) {
		this.frenzy = frenzy;

		boolean beforeFrenzyActivator = true;

		for (Player player : playerList) {
            CharacterState characterState = player.getCharacterState();
		    characterState.setBeforeFrenzyActivator(beforeFrenzyActivator);

            if (player == currentPlayer) {
                beforeFrenzyActivator = false;
            }

            characterState.swapValueBar(frenzy);
        }
	}

	/**
     * This method is used to forward changes (requests) from other parts of the Model to the View.
     *
     * @param response change response (update) from other classes of the model.
	 */
	@Override
	public void update(Response response) {
        notify(response);
	}

    /**
     * Get the list of targets for the selected action.
     *
     * @param actionUnitName name of an action unit in the currentActionUnitsList
     * @return target of the selected action
     * @throws IllegalStateException if there are no action units with that name in currentActionUnitsList
     */
	public List<Targetable> getActionUnitTargetList(String actionUnitName) {
		return getCurrentActionUnitsList().stream()
				.filter(au -> au.getName().equals(actionUnitName))
				.map(au -> au.getCommands().get(CommandConstants.TARGETLIST))
				.findFirst().orElseThrow(IllegalStateException::new);
	}

    /**
     * Gets an action unit from the currentActionUnitsList
     *
     * @param actionUnitName name of an action unit in the currentActionUnitsList
     * @return selected action unit
     * @throws IllegalStateException if there are no action units with that name in currentActionUnitsList
     */
	public ActionUnit getActionUnit(String actionUnitName) {
		return getCurrentActionUnitsList().stream()
				.filter(au -> au.getName().equals(actionUnitName))
				.findFirst().orElseThrow(IllegalStateException::new);
	}

    /**
     * Getter method for the cumulativeDamageTargetSet attribute.
     *
     * @return reference to the cumulativeDamageTargetSet of the game. Contains the set of the players
     *         damaged during this turn.
     */
	public Set<Targetable> getCumulativeDamageTargetSet() {
		return cumulativeDamageTargetSet;
	}

    /**
     * Setter method for the cumulativeDamageTargetSet attribute.
     *
     * @param cumulativeDamageTargetSet reference to the object that will be set as
     *                                  cumulativeDamageTargetSet of the game
     */
	public void setCumulativeDamageTargetSet(Set<Targetable> cumulativeDamageTargetSet) {
		this.cumulativeDamageTargetSet = cumulativeDamageTargetSet;
	}

	public Set<Targetable> getCumulativeTargetSet() {
		return cumulativeTargetSet;
	}

	public void setCumulativeTargetSet(Set<Targetable> cumulativeTargetSet) {
		this.cumulativeTargetSet = cumulativeTargetSet;
	}

	public Deck<AmmoCrate> getAmmoCrateDeck() {
		return ammoCrateDeck;
	}

	public void setAmmoCrateDeck(Deck<AmmoCrate> ammoCrateDeck) {
		this.ammoCrateDeck = ammoCrateDeck;
	}

    /**
     * Needed by the GrabPlayer action to access the player position before it's changed by the MovePlayerAction.run()
     *
     * @return the possible position of the current player at the end of the turn
     */
    public Tile getVirtualPlayerPosition() {
        return virtualPlayerPosition;
    }

    public void setVirtualPlayerPosition(Tile virtualPlayerPosition) {
        this.virtualPlayerPosition = virtualPlayerPosition;
    }

    /**
     * Gets the first player of the game.
     *
     * @return reference to the first player of the game
     */
    public Player getStartingPlayer() {
        return playerList.get(0);
    }

    /**
     * This method is called when a player is killed. If the kill triggers frenzy mode the isFrenzy() method
     * is called to set the game in frenzy mode.
     *
     * @param player reference to the killed player
     * @param overkill true if the player was killed by an overkill, false otherwise
     */
    public void addDeath(Player player, boolean overkill) {
        boolean triggerFrenzy = killShotTrack.addDeath(player, overkill);

        if (!isFrenzy() && triggerFrenzy) {
            setFrenzy(true);
        }

    }

    /**
     * This method draws a weapon from the weapon deck. If the bottom of the deck is reached
     * the discarded weapon cards are shuffled back into the deck.
     *
     * @return weapon drawn
     */
    private Weapon drawWeaponFromDeck() {
        Weapon drawnWeapon = weaponDeck.drawCard();
        if (drawnWeapon == null) {
            this.weaponDeck = new Deck<>(usedWeapons);
            drawnWeapon = weaponDeck.drawCard();
        }

        return drawnWeapon;
    }

    /**
     * This method draws an ammo crate from the ammo crate deck.If the bottom of the deck is reached
     * the discarded ammo crate cards are shuffled back into the deck.
     *
     * @return drawn ammo crate
     */
    private AmmoCrate drawAmmoCrateFromDeck() {
        AmmoCrate drawnAmmoCrate = ammoCrateDeck.drawCard();
        if (drawnAmmoCrate == null) {
            this.ammoCrateDeck = DirectDeserializers.deserializeAmmoCrate();
            drawnAmmoCrate = ammoCrateDeck.drawCard();
        }
        return drawnAmmoCrate;
    }

    /**
     * This method draws a power up from the power up deck. If the bottom of the deck is reached
     * the discarded power up cards are shuffled back into the deck.
     *
     * @return drawn power up
     */
    private PowerUp drawPowerupFromDeck() {
        PowerUp drawnPowerup = powerUpDeck.drawCard();

        if (drawnPowerup == null) {
            this.powerUpDeck = new Deck<>(usedPowerUps);
            drawnPowerup = powerUpDeck.drawCard();
        }

        return drawnPowerup;
    }

    /**
     * Discards a power up.
     *
     * @param powerUp power up to discard
     */
    public void discardPowerup(PowerUp powerUp) {
        usedPowerUps.add(powerUp);
    }

    /**
     * Discards a weapon card.
     *
     * @param weapon weapon card to discard
     */
    public void discardWeapon(Weapon weapon) {
        usedWeapons.add(weapon);
    }

    /**
     * Reload/unload the selected weapon. Notifies registered observers.
     *
     * @param weapon the weapon you want to reload/unload
     * @param state true if you want to reload the weapon, false if you want to unload it
     */
    public void setLoaded(Weapon weapon, boolean state) {
        weapon.setLoaded(state);

        StateUpdate weaponStateUpdate = new WeaponStateUpdate(weapon);

        Response response = new Response(Arrays.asList(weaponStateUpdate));
        notify(response);
    }
}