package it.polimi.se2019.server.games;

import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.games.board.Board;
import it.polimi.se2019.server.games.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Observable;

/**
 * 
 */
public class Game extends Observable {

	private String id;
	private Date startDate;
	private List<Player> playerList;
	private Player currentPlayer;
	private Board board;
	private Integer killshotTrack;
	private Integer deaths;
	private List<Weapon> weaponDeck;
	private List<PowerUp> powerupDeck;

	/**
	 * Default constructor
	 */
	public Game() {
		this.id = "";
		this.startDate = new Date();
		this.playerList = new ArrayList<>();
		this.currentPlayer = null;
		this.board = new Board();
		this.killshotTrack = 0;
		this.deaths = 0;
		this.weaponDeck = new ArrayList<>();
		this.powerupDeck = new ArrayList<>();
	}

	public Game(String id, Date startDate, List<Player> playerList, Player currentPlayer, Board board, Integer killshotTrack, Integer deaths, List<Weapon> weaponDeck, List<PowerUp> powerupDeck) {
		this.id = id;
		this.startDate = startDate;
		this.playerList = playerList;
		this.currentPlayer = currentPlayer;
		this.board = board;
		this.killshotTrack = killshotTrack;
		this.deaths = deaths;
		this.weaponDeck = weaponDeck;
		this.powerupDeck = powerupDeck;
	}

	public GameData generateGameData() {
		return new GameData(getId(), getStartDate());
	}

	public void updateTurn() {

	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public void setCurrentPlayer(Player currentPlayer) {
		if(currentPlayer.getActive())
		 this.currentPlayer = currentPlayer;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public Integer getKillshotTrack() {
		return killshotTrack;
	}

	public void setKillshotTrack(Integer killshotTrack) {
		this.killshotTrack = killshotTrack;
	}

	public Integer getDeaths() {
		return deaths;
	}

	public void setDeaths(Integer deaths) {
		this.deaths = deaths;
	}

	public List<Weapon> getWeaponDeck() {
		return weaponDeck;
	}

	public void setWeaponDeck(List<Weapon> weaponDeck) {
		this.weaponDeck = weaponDeck;
	}

	public List<PowerUp> getPowerupDeck() {
		return powerupDeck;
	}

	public void setPowerupDeck(List<PowerUp> powerupDeck) {
		this.powerupDeck = powerupDeck;
	}

}