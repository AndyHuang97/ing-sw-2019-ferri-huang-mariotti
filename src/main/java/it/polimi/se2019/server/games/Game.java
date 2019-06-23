package it.polimi.se2019.server.games;

import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.cards.ammocrate.AmmoCrate;
import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.exceptions.PlayerNotFoundException;
import it.polimi.se2019.server.games.board.Board;
import it.polimi.se2019.server.games.command.Command;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.util.CommandConstants;
import it.polimi.se2019.util.Observable;
import it.polimi.se2019.util.Response;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

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
	private List<Targetable> cumulativeDamageTargetList;
	private List<Targetable> cumulativeTargetList;
	private boolean frenzy;

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
		this.cumulativeDamageTargetList = new ArrayList<>();
		this.cumulativeTargetList = new ArrayList<>();
		this.frenzy = false;
	}

	public Game(List<Player> playerList) {
		// use this constructor to start a new game
		this.startDate = new Date();
		this.playerList = playerList;
		this.currentPlayer = playerList.get(0);
		this.board = new Board();
		this.killshotTrack = new KillShotTrack(playerList);
		this.weaponDeck = null;
		this.powerupDeck = null;
		this.ammoCrateDeck = null;
		this.currentActionUnitsList = new ArrayList<>();
		this.cumulativeDamageTargetList = new ArrayList<>();
		this.cumulativeTargetList = new ArrayList<>();
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
		this.cumulativeDamageTargetList = new ArrayList<>();
		this.cumulativeTargetList = new ArrayList<>();
	}

	public GameData generateGameData() {
		return new GameData(getStartDate());
	}

	public void updateTurn() {
		int newIndex = playerList.indexOf(currentPlayer) + 1;
		if(newIndex >= playerList.size()) {newIndex = 0;}
		currentPlayer = playerList.get(newIndex);
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public void setCurrentPlayer(Player currentPlayer) {
		if(currentPlayer.getActive()) {this.currentPlayer = currentPlayer;}
		else {throw new IllegalStateException();}
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

	public void setPlayerList(List<Player> playerList) {
		this.playerList = playerList;
	}

	public List<Player> getRanking() {
		List<Player> ranking = new ArrayList<>();
		ranking.addAll(playerList);
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

	public List<ActionUnit> getCurrentActionUnitsList() {
		return currentActionUnitsList;
	}

	public void setCurrentActionUnitsList(List<ActionUnit> currentActionUnitsList) {
		this.currentActionUnitsList = currentActionUnitsList;
	}

	public void performMove(String action) {
		Response response = new Response(new Game(), true, "");
		notify(response);

	}

	public boolean isFrenzy() {
		return frenzy;
	}

	public void setFrenzy(boolean frenzy) {
		this.frenzy = frenzy;
	}

	@Override
	public void update(Response response) {

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

	public List<Targetable> getCumulativeDamageTargetList() {
		return cumulativeDamageTargetList;
	}

	public void setCumulativeDamageTargetList(List<Targetable> cumulativeDamageTargetList) {
		this.cumulativeDamageTargetList = cumulativeDamageTargetList;
	}

	public List<Targetable> getCumulativeTargetList() {
		return cumulativeTargetList;
	}

	public void setCumulativeTargetList(List<Targetable> cumulativeTargetList) {
		this.cumulativeTargetList = cumulativeTargetList;
	}
}