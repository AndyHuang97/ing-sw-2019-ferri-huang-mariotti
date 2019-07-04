package it.polimi.se2019.server.games;

import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.cards.ammocrate.AmmoCrate;
import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.dataupdate.CurrentPlayerStateUpdate;
import it.polimi.se2019.server.dataupdate.CurrentWeaponUnpdate;
import it.polimi.se2019.server.dataupdate.KillShotTrackUpdate;
import it.polimi.se2019.server.dataupdate.StateUpdate;
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
 */
public class Game extends Observable<Response> implements it.polimi.se2019.util.Observer<Response>, Serializable {

	private Date startDate;
	private List<Player> playerList;
	private Player currentPlayer;
	private Board board;
	private KillShotTrack killshotTrack;
	private Deck<Weapon> weaponDeck;
	private Deck<PowerUp> powerupDeck;
	private Deck<AmmoCrate> ammoCrateDeck;
	private List<ActionUnit> currentActionUnitsList;
	private Weapon currentWeapon;
	private Set<Targetable> cumulativeDamageTargetSet;
	private Set<Targetable> cumulativeTargetSet;
	private boolean frenzy;

	private Tile virtualPlayerPosition;
	private List<PowerUp> usedPowerups = new ArrayList<>();
	private List<Weapon> usedWeapons = new ArrayList<>();

	public Game() {
		// don't use this constructor
		this.startDate = new Date();
		this.playerList = new ArrayList<>();
		this.currentPlayer = null;
		this.board = new Board();
		this.killshotTrack = new KillShotTrack(playerList);
		this.weaponDeck = null;
		this.powerupDeck = null;
		this.ammoCrateDeck = null;
		this.currentActionUnitsList = new ArrayList<>();
		this.currentWeapon = null;
		this.cumulativeDamageTargetSet = new HashSet<>();
		this.cumulativeTargetSet = new HashSet<>();
		this.frenzy = false;
	}

	public Game(List<Player> playerList) {
		// use this constructor to start a new game
		this.startDate = new Date();
		this.playerList = playerList;
		this.currentPlayer = null;
		this.board = new Board();
		this.killshotTrack = new KillShotTrack(playerList);
		this.weaponDeck = null;
		this.powerupDeck = null;
		this.ammoCrateDeck = null;
		this.currentActionUnitsList = new ArrayList<>();
		this.currentWeapon = null;
		this.cumulativeDamageTargetSet = new HashSet<>();
		this.cumulativeTargetSet = new HashSet<>();
		this.frenzy = false;
	}

	public Game(Date startDate, List<Player> playerList, Player currentPlayer, Board board, KillShotTrack killshotTrack, Deck<Weapon> weaponDeck, Deck<PowerUp> powerupDeck, Deck<AmmoCrate> ammoCrateDeck) {
		// use this one to resume a current one ?
		this.startDate = startDate;
		this.playerList = playerList;
		this.currentPlayer = currentPlayer;
		this.board = board;
		this.killshotTrack = killshotTrack;
		this.weaponDeck = weaponDeck;
		this.powerupDeck = powerupDeck;
		this.ammoCrateDeck = ammoCrateDeck;
		this.currentActionUnitsList = new ArrayList<>();
		this.currentWeapon = null;
		this.cumulativeDamageTargetSet = new HashSet<>();
		this.cumulativeTargetSet = new HashSet<>();
	}

	public GameData generateGameData() {
		return new GameData(getStartDate());
	}

	public void initGameObjects(String mapIndex) {

	    // trick to avoid notify before everything is initialized
		this.currentPlayer = playerList.get(0);
		this.killshotTrack = new KillShotTrack(playerList);

		new DirectDeserializers();
		this.setBoard(DirectDeserializers.deserializeBoard(mapIndex));
		this.setAmmoCrateDeck(DirectDeserializers.deserializeAmmoCrate());
		this.setWeaponDeck(DirectDeserializers.deserialzerWeaponDeck());
		this.setPowerupDeck(DirectDeserializers.deserialzerPowerUpDeck());
		this.getWeaponDeck().shuffle();
		this.getAmmoCrateDeck().shuffle();
		this.getPowerupDeck().shuffle();

		initBoard();
		initPlayerPowerUps();
		initPlayerAmmoBag();
	}

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

	public void initPlayerPowerUps() {
		this.getPlayerList().stream()
				.forEach(p -> {
					for (int i=0; i<2; i++) {
						givePowerUpToPlayer(p);
					}
				});

	}

	public void initPlayerAmmoBag() {
		this.getPlayerList().stream()
				.forEach(p -> {
					Stream.of(AmmoColor.values()).forEach(color -> p.getCharacterState().getAmmoBag().put(color,1));
				});
	}

	public void givePowerUpToPlayer(Player player) {
		player.getCharacterState().getPowerUpBag().add(drawPowerupFromDeck());
	}

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

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public void setCurrentPlayer(Player currentPlayer) {
        if (currentPlayer.getActive()) this.currentPlayer = currentPlayer;
        else throw new IllegalStateException();

        CurrentPlayerStateUpdate currentPlayerUpdate = new CurrentPlayerStateUpdate(currentPlayer);

        Response request = new Response(Arrays.asList(currentPlayerUpdate));
        notify(request);

	}

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

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public List<Player> getPlayerList() {
		return playerList;
	}

	public List<Player> getActivePlayerList() {
		return playerList.stream().filter(Player::getActive).collect(Collectors.toList());
	}

	public void setPlayerList(List<Player> playerList) {
		this.playerList = playerList;
	}

	public List<Player> getRanking() {
		List<Player> ranking = new ArrayList<>();
		ranking.addAll(playerList);

		// it's the end of the game, get points from the players who got damages on their boards
        killshotTrack.killPlayersAndGetScore(playerList);
        // the score of each player has been updated, time to calculate the bonus points on the KillShotTrack
        Map<PlayerColor, Integer> killShotTracBonusPoints = killshotTrack.calculateScore();

        for (Map.Entry<PlayerColor, Integer> entry : killShotTracBonusPoints.entrySet()) {
            CharacterState characterState = getPlayerByColor(entry.getKey()).getCharacterState();

            characterState.setScore(characterState.getScore() + entry.getValue());
        }

        Map<Integer, List<Player>> scorePlayersMap = new HashMap<>();

        // the final players scores have been calculated, let's sort the array
		Comparator<Player> scoreComparator = (p1, p2) ->  p1.getCharacterState().getScore().compareTo(p2.getCharacterState().getScore());
		ranking.sort(scoreComparator.reversed());

		return ranking;
	}

	/**
	 * TODO may add a PlayerNotFoundException instead of returning null
	 * @param color
	 * @return
	 */
	public Player getPlayerByColor(PlayerColor color) {
		Optional<Player> optPlayer = getPlayerList().stream()
				.filter(p -> p.getColor() == color)
				.findFirst();
		return optPlayer.orElse(null);
	}

	public List<PlayerColor> getActiveColors() {
		return getPlayerList().stream()
				.map(p -> p.getColor())
				.collect(Collectors.toList());
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public Player getPlayerByNickname(String nickname) throws PlayerNotFoundException {
	    for (Player player : playerList) {
	        if (player.getUserData().getNickname().equals(nickname)) {
	            return player;
            }
        }

        throw new PlayerNotFoundException();
    }

	public KillShotTrack getKillshotTrack() {
		return killshotTrack;
	}

	public void setKillshotTrack(KillShotTrack killshotTrack) {
		this.killshotTrack = killshotTrack;

		// notify KillShotTrack change
        KillShotTrackUpdate killShotTrackUpdate = new KillShotTrackUpdate(killshotTrack);

        List<StateUpdate> updateList = new ArrayList<>();
        updateList.add(killShotTrackUpdate);

        Response response = new Response(updateList);
        notify(response);
	}

	public Deck<Weapon> getWeaponDeck() {
		return weaponDeck;
	}

	public void setWeaponDeck(Deck<Weapon> weaponDeck) {
		this.weaponDeck = weaponDeck;
	}

	public Deck<PowerUp> getPowerupDeck() {
		return powerupDeck;
	}

	public void setPowerupDeck(Deck<PowerUp> powerupDeck) {
		this.powerupDeck = powerupDeck;
	}

	public Weapon getCurrentWeapon() {
		return currentWeapon;
	}

	public void setCurrentWeapon(Weapon currentWeapon) {
		this.currentWeapon = currentWeapon;

		StateUpdate currentWeaponUpdate = new CurrentWeaponUnpdate(currentWeapon);
        Response response = new Response(Arrays.asList(currentWeaponUpdate));

        notify(response);
	}

	public List<ActionUnit> getCurrentActionUnitsList() {
		return currentActionUnitsList;
	}

	public void setCurrentActionUnitsList(List<ActionUnit> currentActionUnitsList) {
		this.currentActionUnitsList = currentActionUnitsList;
	}

	public boolean isFrenzy() {
		return frenzy;
	}

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
     * This method is used to forward changes from other parts of the Model to the View.
     * @param response change request from other classes of the model.
	 */
	@Override
	public void update(Response response) {
        notify(response);
	}

	public List<Targetable> getActionUnitTargetList(String actionUnitName) {
		return getCurrentActionUnitsList().stream()
				.filter(au -> au.getName().equals(actionUnitName))
				.map(au -> au.getCommands().get(CommandConstants.TARGETLIST))
				.findFirst().orElseThrow(IllegalStateException::new);
	}

	public ActionUnit getActionUnit(String actionUnitName) {
		return getCurrentActionUnitsList().stream()
				.filter(au -> au.getName().equals(actionUnitName))
				.findFirst().orElseThrow(IllegalStateException::new);
	}

	public Set<Targetable> getCumulativeDamageTargetSet() {
		return cumulativeDamageTargetSet;
	}

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
     * @return the possible position of the current player at the end of the turn
     */
    public Tile getVirtualPlayerPosition() {
        return virtualPlayerPosition;
    }

    public void setVirtualPlayerPosition(Tile virtualPlayerPosition) {
        this.virtualPlayerPosition = virtualPlayerPosition;
    }

    public Player getStartingPlayer() {
        return playerList.get(0);
    }

    public void addDeath(Player player, boolean overkill) {
        boolean triggerFrenzy = killshotTrack.addDeath(player, overkill);

        if (!isFrenzy() && triggerFrenzy) {
            setFrenzy(true);
        }

    }

    private Weapon drawWeaponFromDeck() {
        Weapon drawnWeapon = weaponDeck.drawCard();
        if (drawnWeapon == null) {
            this.weaponDeck = new Deck<>(usedWeapons);
            drawnWeapon = weaponDeck.drawCard();
        }

        return drawnWeapon;
    }

    private AmmoCrate drawAmmoCrateFromDeck() {
        AmmoCrate drawnAmmoCrate = ammoCrateDeck.drawCard();
        if (drawnAmmoCrate == null) {
            this.ammoCrateDeck = DirectDeserializers.deserializeAmmoCrate();
            drawnAmmoCrate = ammoCrateDeck.drawCard();
        }
        return ammoCrateDeck.drawCard();
    }

    private PowerUp drawPowerupFromDeck() {
        PowerUp drawnPowerup = powerupDeck.drawCard();
        if (drawnPowerup == null) {
            this.powerupDeck = new Deck<>(usedPowerups);
            drawnPowerup = powerupDeck.drawCard();
        }
        return powerupDeck.drawCard();
    }

    public void discardPowerup(PowerUp powerUp) {
        usedPowerups.add(powerUp);
    }

    public void discardWeapon(Weapon weapon) {
        usedWeapons.add(weapon);
    }
}