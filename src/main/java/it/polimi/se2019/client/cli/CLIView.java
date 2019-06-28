package it.polimi.se2019.client.cli;

import it.polimi.se2019.client.View;
import it.polimi.se2019.client.util.Util;
import it.polimi.se2019.server.games.board.Tile;
import java.util.stream.IntStream;

import java.util.Arrays;

public class CLIView extends View {
    CLIUtil utils = new CLIUtil();

    public CLIView() {
        this.setCliTrueGuiFalse(true);
    }

    @Override
    public void askInput() {

    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public void reportError(String error) {

    }

    @Override
    public void showGame() {
        CLIController controller = new CLIController();
        controller.setView(this);
        utils.println("\nCurrent Gameboard:\n");
        controller.handleMapLoading();
        controller.handleCharactersLoading();
    }

    public void showLogin() {
        utils.printBanner();
        String nickname = utils.askUserInput("Nickname");
        this.setNickname(nickname);
        String host = utils.askUserInput("IP Address", "127.0.0.1");
        String type = utils.askUserInput("Connection type", Arrays.asList("RMI", "SOCKET"), true);
        String map = utils.askUserInput("Map", Arrays.asList("0", "1", "2", "3"), false);
        this.connect(nickname, host, type, map);
        utils.println("Waiting for the server to start the game...this can take a while...");
        utils.hold();
    }

    public CLIUtil getUtils() { return utils; }
}
