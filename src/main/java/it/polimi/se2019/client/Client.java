package it.polimi.se2019.client;

import it.polimi.se2019.client.cli.CLIView;
import it.polimi.se2019.client.gui.ClientGui;
import javafx.application.Application;

import java.util.Scanner;

/**
 * This was the generalized client before we decided to split the jars for the gui and the cli
 *
 * @author FF
 *
 */
public class Client {

    /**
     * The switcher component
     *
     * @param input the string
     * @return the boolean status
     *
     */
    public static boolean chooseView(String input) {
        if (input.equalsIgnoreCase("cli")) {
            new CLIView();
        }
        else if (input.equalsIgnoreCase("gui")) {
            Application.launch(ClientGui.class);
        }
        else { return false; }
        return true;
    }

    /**
     * The main
     *
     * @param args optional args
     *
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Adrenaline, the modern frame to play a FPS!");
        System.out.println("Do you want to play on a CLI (Command Line Interface) or on a GUI (Graphic User Interface)?");
        String input = scanner.nextLine();
        while (!chooseView(input)) {
            System.out.println("Please answer with \"cli\" or \"gui\":");
            input = scanner.nextLine();
        }
    }
}
