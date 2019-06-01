package it.polimi.se2019.client.net;

import it.polimi.se2019.client.gui.MainApp;
import it.polimi.se2019.util.Response;

import java.io.BufferedReader;
import java.io.IOException;

public class CommandHandler implements Runnable {
    BufferedReader in;
    MainApp mainApp;

    public CommandHandler(BufferedReader in, MainApp mainApp) {
        this.in = in;
        this.mainApp = mainApp;
    }

    public void run(){
        try {
            String inputLine;
            while ((inputLine = this.in.readLine()) != null) {
                System.out.println(inputLine);
                Response request = (Response) new Response(null, false, "").deserialize(inputLine);
                if (request.getSuccess()) {
                    mainApp.setGame(request.getGame());
                    mainApp.boardDeserialize();
                    mainApp.initRootLayout();
                    mainApp.showGameBoard();

                    mainApp.getPrimaryStage().setResizable(false);
                    mainApp.getPrimaryStage().setFullScreen(true);
                    mainApp.getPrimaryStage().sizeToScene();
                    mainApp.getPrimaryStage().show();
                    // TODO: redraw gameboard
                }
            }
        } catch (IOException e) {
            // do something if connection fails
        }
    }
}
