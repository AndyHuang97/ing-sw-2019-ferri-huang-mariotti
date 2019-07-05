package it.polimi.se2019.client.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * The cli is started from here, it initializes a view from here. This is very important to have the app work correctly
 *
 * @author FF
 *
 */
public class ClientCli {
    private static CLIView view;
    public static Properties prop = new Properties();

    /**
     * The main
     *
     * @param args the eventual args we pass to the program
     *
     */
    public static void main(String args[]){
        if(args.length > 0) {
            File file = new File(args[0]);
            try (InputStream input = new FileInputStream(file)) {
                prop.load(input);
            } catch (IOException ex) {
                Logger.getGlobal().info("Please provide a valid config file");
                System.exit(0);
            }
            // Work with your 'file' object here
        } else {
            Logger.getGlobal().info("Please provide a config file");
            System.exit(0);
        }
        view = new CLIView();
        view.showLogin();
    }
}
