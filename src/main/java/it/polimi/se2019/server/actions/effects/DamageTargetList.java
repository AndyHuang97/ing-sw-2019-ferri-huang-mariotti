package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;

import java.util.List;

public class DamageTargetList implements Effect {

    private List<Player> damageTargetList;
    private PlayerColor attackerColor;
    private Integer amount;

    public DamageTargetList(List<Player> damageTargetList, PlayerColor attackerColor, Integer amount) {
        this.damageTargetList = damageTargetList;
        this.attackerColor = attackerColor;
        this.amount = amount;
    }

    @Override
    public void run() {

    }
}
