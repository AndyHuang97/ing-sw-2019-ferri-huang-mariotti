package it.polimi.se2019.server.controller;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.server.playeractions.PlayerAction;

import java.util.List;

public class EndGameState extends ControllerState {

    @Override
    public void sendSelectionMessage(CommandHandler commandHandler) {

    }

    @Override
    public ControllerState nextState(List<PlayerAction> playerActions, Game game, Player player) {

        return null;
    }

}
