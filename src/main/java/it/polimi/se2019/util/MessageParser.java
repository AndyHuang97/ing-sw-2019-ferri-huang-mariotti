package it.polimi.se2019.util;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.exceptions.MessageParseException;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.playerActions.PlayerAction;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Transform a Message in a List of PlayerActions using reflection so that the Controller can check/setUp them.
 */
public class MessageParser {

    public List<PlayerAction> parse(InternalMessage message, Game game, Player player) throws MessageParseException, UnpackingException {
        List<PlayerAction> playerActions = new ArrayList<>();
        //for (String k : message.getCommands().keySet()) {
        for (Targetable t : message.getCommands().get(Constants.KEY_ORDER)){
            PlayerAction pa = (PlayerAction) t;
            System.out.println(pa.getId());
            //TODO use keyOrder
            List<Targetable> params = message.getCommandParams(pa.getClass().getName());
            try {
                Class<PlayerAction> classType = (Class<PlayerAction>) Class.forName(pa.getClass().getName());

                PlayerAction playerAction = classType.getConstructor(Game.class, Player.class)
                        .newInstance(game, player);

                playerAction.unpack(params);

                playerActions.add(playerAction);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new MessageParseException();
            } catch (UnpackingException e) {
                throw new UnpackingException();
            }
        }

        return playerActions;
    }
}
