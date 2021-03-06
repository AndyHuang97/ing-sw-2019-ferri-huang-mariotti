package it.polimi.se2019.util;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.exceptions.MessageParseException;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.playeractions.PlayerAction;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Transform a Message in a List of PlayerActions using reflection so that the Controller can check/setUp them.
 * The order of the key parsing is determined by the keyOrder key of the message's commands.
 *
 * @author AH
 *
 */
public class MessageParser {

    /**
     * The actual parser
     *
     * @param message the internal message
     * @param game the game
     * @param player the player
     * @throws MessageParseException if problem arises
     * @throws UnpackingException if problems arises during unpacking
     *
     */
    public List<PlayerAction> parse(InternalMessage message, Game game, Player player) throws MessageParseException, UnpackingException {
        List<PlayerAction> playerActions = new ArrayList<>();
        for (Targetable t : message.getCommands().get(Constants.KEY_ORDER)){
            PlayerAction pa = (PlayerAction) t;
            Logger.getGlobal().info(pa.getClass().getSimpleName());
            List<Targetable> params = message.getCommandParams(pa.getClass().getSimpleName());
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
