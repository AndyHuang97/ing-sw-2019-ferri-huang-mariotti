package it.polimi.se2019.server.cards.ammocrate;

import it.polimi.se2019.server.actions.ActionUnit;
import it.polimi.se2019.server.cards.Card;

import java.util.List;

/**
 * The ammocrate has nothing more than a normal card
 *
 * @author FF
 *
 */
public class AmmoCrate extends Card {
    public AmmoCrate(List<ActionUnit> actionUnitList, String name) {
        super(actionUnitList, name);
    }
}