package it.polimi.se2019.server.games;

import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.exceptions.PlayerNotFoundException;
import it.polimi.se2019.server.games.board.Board;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.util.Observable;
import it.polimi.se2019.util.Response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class Game extends Observable<Response> implements Serializable {

	private Date startDate;
	private List<Player> playerList;
	private Player currentPlayer;
	private Board board;
	private Integer killshotTrack;
	private List<Weapon> weaponDeck;
	private List<PowerUp> powerupDeck;
	private List<ActionUnit> currentActionUnitsList;

	public Game() {
		// don't use this constructor
		this.startDate = new Date();
		this.playerList = new ArrayList<>();
		this.currentPlayer = null;
		this.board = new Board();
		this.killshotTrack = 0;
		this.weaponDeck = new ArrayList<>();
		this.powerupDeck = new ArrayList<>();
	}

	public Game(List<Player> playerList) {
		// use this constructor to start a new game
		this.startDate = new Date();
		this.playerList = playerList;
		this.currentPlayer = playerList.get(0);
		this.board = new Board();
		this.killshotTrack = 0;
		this.weaponDeck = new ArrayList<>();
		this.powerupDeck = new ArrayList<>();
	}

	public Game(Date startDate, List<Player> playerList, Player currentPlayer, Board board, Integer killshotTrack, List<Weapon> weaponDeck, List<PowerUp> powerupDeck) {
		// use this one to resume? a current one
		this.startDate = startDate;
		this.playerList = playerList;
		this.currentPlayer = currentPlayer;
		this.board = board;
		this.killshotTrack = killshotTrack;
		this.weaponDeck = weaponDeck;
		this.powerupDeck = powerupDeck;
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
		if(currentPlayer.getActive()) this.currentPlayer = currentPlayer;
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

	/**
	 * TODO may add a PlayerNotFoundException instead of returning null
	 * @param color
	 * @return
	 */
	public Player getPlayerByColor(PlayerColor color) {
		List<Player> players = getPlayerList();

		for(Player p : players) {
			if(p.getColor() == color)
				return p;
		}
		return null;
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

	public Integer getKillshotTrack() {
		return killshotTrack;
	}

	public void setKillshotTrack(Integer killshotTrack) {
		this.killshotTrack = killshotTrack;
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
}