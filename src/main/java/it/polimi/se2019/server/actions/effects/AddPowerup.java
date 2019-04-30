package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.cards.Card;
import it.polimi.se2019.server.games.player.Player;

public class AddPowerup implements Effect {

    private Card targetCard;
    private Player player;

    public AddPowerup(Card targetCard) {
        this.targetCard = targetCard;
    }

    public Card getTargetCard() {
        return targetCard;
    }

    public void setTargetCard(Card targetCard) {
        this.targetCard = targetCard;
    }

    @Override
    public void run() {

    }
}
