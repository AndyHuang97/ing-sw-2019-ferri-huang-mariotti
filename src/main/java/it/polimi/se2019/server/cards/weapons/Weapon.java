package it.polimi.se2019.server.cards.weapons;

import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.cards.Card;
import it.polimi.se2019.server.games.player.AmmoColor;

import java.util.List;


public class Weapon extends Card {

	private String name;
	private boolean loaded;
    private List<AmmoColor> pickUpCost;
	private List<AmmoColor> reloadCost;
    private List<ActionUnit> optionalEffectList;

	public Weapon(List<ActionUnit> actionUnitList, String name, List<AmmoColor> pickUpCost,
				  List<AmmoColor> reloadCost, List<ActionUnit> optionalEffectList) {
		super(actionUnitList);
		this.name = name;
		this.loaded = true;
		this.pickUpCost = pickUpCost;
		this.reloadCost = reloadCost;
		this.optionalEffectList = optionalEffectList;
	}

	public String getName() {
		return name;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	public List<AmmoColor> getPickUpCost() {
		return pickUpCost;
	}

	public void setPickUpCost(List<AmmoColor> pickUpCost) {
		this.pickUpCost = pickUpCost;
	}

	public List<AmmoColor> getReloadCost() {
		return reloadCost;
	}

	public void setReloadCost(List<AmmoColor> reloadCost) {
		this.reloadCost = reloadCost;
	}

	public List<ActionUnit> getOptionalEffectList() {
		return optionalEffectList;
	}

	public void setOptionalEffectList(List<ActionUnit> optionalEffectList) {
		this.optionalEffectList = optionalEffectList;
	}
}
