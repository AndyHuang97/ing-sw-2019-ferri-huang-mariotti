package it.polimi.se2019.server.actions.effects;

/**
 *  This abstract class deals with damage calculation on players.
 *
 * @author andreahuang
 */
public abstract class Damage implements Effect {

    protected Integer amount;
    protected String actionUnitName;

    /**
     * Default constructor. It sets up the amount of damage to inflict, and it also has an additional
     * name of action unit for correct targeting.
     *
     * @param amount is the amount of damage to inflict
     * @param actionUnitName the action unit used for correct targeting.
     */
    protected Damage(Integer amount, String actionUnitName) {
        this.amount = amount;
        this.actionUnitName = actionUnitName;
    }

}
