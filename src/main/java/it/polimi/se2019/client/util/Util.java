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

import java.util.Arrays;
import java.util.Optional;

/**
 * This Util class provides some common helper methods to all the other classes in the view package.
 *
 * @author andreahuang
 */
public class Util {

    /**
     * The convertToCoords method converts an integer i to coordinates.
     *
     * @param i the integer to convert.
     * @return the converted integer in coordinates.
     */
    public static int[] convertToCoords(int i) {
        int[] coords = new int[2];

        coords[0] = i % 4;
        coords[1] = i / 4;

        return coords;
    }

    /**
     * The convertToIndex method converts two integers in one index.
     *
     * @param x the x coordinate.
     * @param y the y coordinate.
     * @return the x and y conversion to an index integer.
     */
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
     * The ifFirstSelection method checks whether the selections is the first in th sequence, if true it enables
     * the confirm button.
     *
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
     * The updateCircle method colors the first white circle to green.
     *
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

    /**
     * The isLastSelection method checks whether the progress bar has only green circles.
     *
     * @param progressBar is the grid pane containing the progress bar.
     * @return the predicate that the last color is colored or not
     */
    public static boolean isLastSelection(GridPane progressBar) {
        return progressBar.getChildren().stream()
                .map(n -> (Circle) n)
                .filter(Node::isVisible)
                .allMatch(c -> c.getFill() == Paint.valueOf("green"));
    }

    /**
     * The getCorrectPlayerBoardMode method retrieves the value bar of a player, then it returns the game mode.
     *
     * @param player the player for which the player player board is going to be shown.
     * @return a string indicating the game mode.
     */
    public static String getCorrectPlayerBoardMode(Player player) {

        return Arrays.equals(player.getCharacterState().getValueBar(),CharacterState.NORMAL_VALUE_BAR) ?
                Constants.NORMAL : Constants.FRENZY;
    }

    /**
     * The setLabelColor method sets a label's color according to the player color input.
     *
     * @param label the label that is to be set the player color.
     * @param playerColor the color used to set the label.
     */
    public static void setLabelColor(Label label, PlayerColor playerColor) {
        if (playerColor != PlayerColor.GREY) {
            label.setTextFill(Paint.valueOf(playerColor.getColor()));
        }
        else {
            label.setTextFill(Color.BLACK);
        }
    }

    /**
     * The getPlayerToken method returns the correct color token to add in damage and/or marker bar.
     *
     * @param color is the player color.
     * @return an image of the player color token.
     */
    public static Image getPlayerToken(PlayerColor color) {
        String path = Constants.TOKEN_PATH + color.getColor().toLowerCase() + ".png";
        return new NamedImage(path, Constants.TOKEN_PATH);
    }
}
