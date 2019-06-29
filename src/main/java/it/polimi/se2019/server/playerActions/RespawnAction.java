package it.polimi.se2019.server.playerActions;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.cards.Card;
import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.exceptions.TileNotFoundException;
import it.polimi.se2019.server.exceptions.UnpackingException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.util.ErrorResponse;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class RespawnAction extends PlayerAction {

    private static final int POWERUP_POSITION = 0;
    private static final String ERRORMESSAGE = "PowerUp selection failed";

    private PowerUp powerUpToDiscard;

    public RespawnAction(Game game, Player player) {
        super(game, player);
    }
    public RespawnAction(int amount) {
        super(amount);
    }

    @Override
    public void unpack(List<Targetable> params) throws UnpackingException {
        try {
            powerUpToDiscard = (PowerUp) params.get(POWERUP_POSITION);
        } catch (ClassCastException e) {
            throw new UnpackingException();
        }
    }

    /**
     * This method spawns the player on the tile of the same color as that of the power up he/she discarded.
     */
    @Override
    public void run() {

        getPlayer().getCharacterState().removePowerUp(powerUpToDiscard);
        try {
            Tile spawnTile = getGame().getBoard().getTileList().stream()
                    .filter(Objects::nonNull)
                    .filter(Tile::isSpawnTile)
                    .filter(tile -> tile.getRoomColor().getColor().equals(powerUpToDiscard.getPowerUpColor().getColor()))
                    .findFirst().orElseThrow(TileNotFoundException::new);
            getPlayer().getCharacterState().setTile(spawnTile);
            getPlayer().getCharacterState().resetDamageBar();

            if (getGame().isFrenzy()) getPlayer().getCharacterState().swapValueBar(true);

        } catch (TileNotFoundException e) {
            Logger.getGlobal().warning("Mismatch between powerUp's color and spawn tile's color.");
        }
    }

    /**
     * This method checks whether the sender of the message has the power up card that was selected as input.
     * @return true if the sender has the card, false otherwise.
     */
    @Override
    public boolean check() {
        return getPlayer().getCharacterState().getPowerUpBag().contains(powerUpToDiscard);
    }

    @Override
    public ErrorResponse getErrorMessage() {
        return new ErrorResponse(ERRORMESSAGE);
    }

    @Override
    public Card getCard() {
        return powerUpToDiscard;
    }

    @Override
    public String getId() {
        return Constants.RESPAWN;
    }
}
