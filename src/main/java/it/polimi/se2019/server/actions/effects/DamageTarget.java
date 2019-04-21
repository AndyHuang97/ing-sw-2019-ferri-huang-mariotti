package it.polimi.se2019.server.actions.effects;

/**
 * 
 */
public class DamageTarget implements Effect {
    int amount;

	/**
	 * Default constructor
	 */
	public DamageTarget(int amount) {
	    this.amount = amount;
	}

	@Override
	public void run() {

	}
}