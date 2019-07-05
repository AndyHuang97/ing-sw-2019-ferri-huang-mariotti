package it.polimi.se2019.client.cli;

/**
 * The cli is started from here, it initializes a view from here. This is very important to have the app work correctly
 *
 * @author FF
 *
 */
public class ClientCli {
    private static CLIView view = new CLIView();

    /**
     * The main
     *
     * @param args the eventual args we pass to the program
     *
     */
    public static void main(String args[]){
        view.showLogin();
    }
}
