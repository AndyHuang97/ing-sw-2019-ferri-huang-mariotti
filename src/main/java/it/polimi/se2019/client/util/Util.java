package it.polimi.se2019.client.util;

import it.polimi.se2019.server.games.board.Board;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.util.Optional;

public class Util {

    public static int[] convertToCoords(int i) {
        int[] coords = new int[2];

        coords[0] = i % 4;
        coords[1] = i / 4;

        return coords;
    }

    public static int convertToIndex(int x, int y) {
        return x + y*4;
    }

    public static int getMapId(Board board) {
        int[][] lookupTable = {{2, 3}, {1, 0}};
        int bottomRight = board.getTile(0, 2) == null ? 1 : 0;
        int topLeft = board.getTile(3, 0) == null ? 1 : 0;
        return lookupTable[bottomRight][topLeft];
    }

    /**
     * Checks whether the selections is the first in th sequence, if true it enables the confirm button.
     * @param confirmButton is the confirm button.
     * @param progressBar is the progress bar.
     */
    public static void ifFirstSelection(Button confirmButton, GridPane progressBar) {
        boolean first = progressBar.getChildren().stream()
                .map(n -> (Circle) n)
                .filter(Node::isVisible)
                .allMatch(c -> c.getFill() == Paint.valueOf("white"));

        if (first) {
            confirmButton.setDisable(false);
        }
    }

    /**
     * Colors the first white circle to green.
     * @param progressBar is the progress bar in the selection phase.
     */
    public static void updateCircle(GridPane progressBar) {
        Optional<Circle> circle = progressBar.getChildren().stream()
                .map(n -> (Circle) n)
                .filter(c ->  c.getFill() == Paint.valueOf("white"))
                .filter(Node::isVisible)
                .findFirst();

        circle.ifPresent(value -> value.setFill(Paint.valueOf("green")));
    }

    public static boolean isLastSelection(GridPane progressBar) {
        return progressBar.getChildren().stream()
                .map(n -> (Circle) n)
                .filter(Node::isVisible)
                .allMatch(c -> c.getFill() == Paint.valueOf("green"));
    }

    public static String getCorrectPlayerBoardMode(Player player) {
        return player.getCharacterState().getValueBar() == CharacterState.NORMAL_VALUE_BAR ?
                Constants.NORMAL : Constants.FRENZY;
    }

    public static void setLabelColor(Label label, PlayerColor playerColor) {
        if (playerColor != PlayerColor.GREY) {
            label.setTextFill(Paint.valueOf(playerColor.getColor()));
        }
        else {
            label.setTextFill(Color.BLACK);
        }
    }

    /**
     * Gets the correct color token to add in damage and/or marker bar.
     * @param color is the player color.
     * @return an image of the player color token.
     */
    public static Image getPlayerToken(PlayerColor color) {
        String path = Constants.TOKEN_PATH + color.getColor().toLowerCase() + ".png";
        return new NamedImage(path, Constants.TOKEN_PATH);
    }
}
