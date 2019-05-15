package it.polimi.se2019.server.controller;


import it.polimi.se2019.server.exceptions.MessageParseException;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.GameManager;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.playerActions.PlayerAction;
import it.polimi.se2019.server.virtualview.VirtualView;
import it.polimi.se2019.util.MessageParser;
import it.polimi.se2019.util.Observer;
import it.polimi.se2019.util.Request;

import java.sql.Struct;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Controller implements Observer<Request> {

    private GameManager gameManager;
    private VirtualView virtualView;
    private TurnHandler turnHandler;
    private MessageParser messageParser;

    public Controller(GameManager activeGames, VirtualView virtualView) {
        this.gameManager = activeGames;
        this.virtualView = virtualView;

        this.messageParser = new MessageParser();
    }

    public void onNotify(Request request) {
        /**
         * Create an Action object by parsing the messageType and params list from
         * the array, then run the action
         */
    }

    public void applyAction(PlayerAction action){

    }

    @Override
    public void update(Request request) {
        /**
         * Parse Request and build a List of PlayerAction
         */
        String nickname = request.getNickname();
        try {
            Game game = gameManager.retrieveGame(nickname);

            Player player = game.getPlayerList().stream()
                    .filter(p->p.getUserData().getNickname().equals(nickname))
                    .collect(Collectors.toList()).get(0);

            List<PlayerAction> playerActions = messageParser.parse(request.getMessage(), game, player);

            /**
             * Check the actions in the List
             */
            boolean canRun = true;
            String errorMessage;

            for (PlayerAction pa : playerActions) {
                if (!pa.check()) {

                }
            }
        } catch (GameManager.GameNotFoundException | MessageParseException | UnpackingException e) {
            e.printStackTrace();
        }


    }

    public VirtualView getVirtualView() {
        return virtualView;
    }

    public void setVirtualView(VirtualView virtualView) {
        this.virtualView = virtualView;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }
}
