package it.polimi.se2019.client.cli;

import it.polimi.se2019.client.View;

public class ClientCli {
    private static CLIView view = new CLIView();

    public static void main(String args[]){
        view.showLogin();
    }
}
