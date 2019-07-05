package it.polimi.se2019.client.cli;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.games.player.AmmoColor;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * The cli util is a bit more than just a util file, it also manages the console. Then it provides useful methods to make life easier.
 *
 * @author FF
 *
 */
public class CLIUtil {
    private static final Logger logger = Logger.getLogger(CLIUtil.class.getName());
    private Scanner in = new Scanner(System.in);
    private PrintStream out = System.out;
    private int inputTimeout;

    /**
     * The constructor loads settings from file
     *
     */
    public CLIUtil() {
        try (InputStream input = CLIUtil.class.getClassLoader().getResource("config.properties").openStream()) {
            Properties prop = new Properties();
            prop.load(input);
            inputTimeout = Integer.parseInt(prop.getProperty("game.input_timeout_seconds"));
        } catch(IOException e) {
            Logger.getGlobal().warning(e.toString());
        }
    }

    /**
     * This is the core to get an answer to a question from the user. It has a timeout not to block the console forever.
     *
     * @param printString the question to ask
     * @return the answer of the user
     *
     */
    private String basicUserInput(String printString) {
        try {
            out.print(printString);
            FutureTask<String> task = new FutureTask<>(() -> { return in.nextLine(); });
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
            return task.get(inputTimeout, TimeUnit.SECONDS);
        } catch (InterruptedException | TimeoutException | ExecutionException ex) {
            logger.info(ex.getMessage());
            out.println("Input timeout reached");
            return Constants.NOP;
        }
    }

    /**
     * This is one of the many ways to ask the user a question, here you just provide a question
     *
     * @param question the question to ask
     * @return the answer of the user
     *
     */
    public synchronized String askUserInput(String question) {
        return basicUserInput(question + "\n> ");
    }

    /**
     * This is one of the many ways to ask the user a question, here you just provide a question and a default answer
     * that is convenient for the user
     *
     * @param question the question to ask
     * @param defaultResponse the default answer
     * @return the answer of the user
     *
     */
    public synchronized String askUserInput(String question, String defaultResponse) {
        String response = basicUserInput(question + " (" + defaultResponse + ")\n> ");
        if (response.equals("")) response = defaultResponse;
        return response;
    }

    /**
     * This is an helper to build a niche question layout
     *
     * @param question the question to ask
     * @param validResponses the responses to choose from
     * @return the beautiful string to print
     *
     */
    private String columnStringBuilder(String question, List<String> validResponses) {
        StringBuilder printString = new StringBuilder();
        printString.append(question + "\n");
        for (int i = 0; i < validResponses.size(); i++) printString.append(i + ") " + validResponses.get(i) + "\n");
        printString.append("> ");
        return printString.toString();
    }

    /**
     * This is an helper to get an answer from a list by string
     *
     * @param indexResponse the string index of the response
     * @param validResponses the responses to choose from
     * @return the real answer
     *
     */
    private String processIndexResponse(String indexResponse, List<String> validResponses) {
        try {
            return validResponses.get(Integer.parseInt(indexResponse));
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * This is one of the many ways to ask the user a question, here you just provide a question and the valid responses
     * to choose from, the print layout can be numbered or not.
     *
     * @param question the question to ask
     * @param validResponses the responses to choose from
     * @param numbered the layout style
     * @return the answer of the user
     *
     */
    public synchronized String askUserInput(String question, List<String> validResponses, Boolean numbered) {
        if (numbered) {
            String indexResponse, response;
            do {
                indexResponse = basicUserInput(columnStringBuilder(question, validResponses));
                if (indexResponse.equals(Constants.NOP)) return Constants.NOP;
                response = processIndexResponse(indexResponse, validResponses);
            } while (!validResponses.contains(response));
            return response;
        } else {
            String response;
            do {
                response = basicUserInput(question + " [" + String.join("/", validResponses) + "]\n> ");
                if (response.equals(Constants.NOP)) return Constants.NOP;
            } while (!validResponses.contains(response));
            return response;
        }
    }

    /**
     * This is one of the many ways to ask the user a question, here you just provide a question, the valid responses
     * to choose from, the print layout can be numbered or not and the default answer
     *
     * @param question the question to ask
     * @param defaultResponse the default response
     * @param validResponses the responses to choose from
     * @param numbered the layout style
     * @return the answer of the user
     *
     */
    public synchronized String askUserInput(String question, String defaultResponse, List<String> validResponses, Boolean numbered) {
        if (!validResponses.contains(defaultResponse)) validResponses.add(0, defaultResponse);
        Collections.replaceAll(validResponses, defaultResponse, "(" + defaultResponse + ")");
        if (numbered) {
            String indexResponse, response;
            do {
                indexResponse = basicUserInput(columnStringBuilder(question, validResponses));
                if (indexResponse.equals(Constants.NOP)) return Constants.NOP;
                if (indexResponse.equals("")) return defaultResponse;
                response = processIndexResponse(indexResponse, validResponses);
            } while (!validResponses.contains(response));
            return response;
        } else {
            String response;
            do {
                response = basicUserInput(question + " [" + String.join("/", validResponses) + "]\n> ");
                if (response.equals(Constants.NOP)) return Constants.NOP;
                if (response.equals("")) return defaultResponse;
            } while (!validResponses.contains(response));
            return response;
        }
    }

    /**
     * A piece of art, the game logo for the cli
     *
     */
    public synchronized void printBanner() {
        out.println("      ___    ____  ____  _______   _____    __    _____   _____ ");
        out.println("     /   |  / __ \\/ __ \\/ ____/ | / /   |  / /   /  _/ | / /   |");
        out.println("    / /| | / / / / /_/ / __/ /  |/ / /| | / /    / //  |/ / /| |");
        out.println("   / ___ |/ /_/ / _, _/ /___/ /|  / ___ |/ /____/ // /|  / ___ |");
        out.println("  /_/  |_/_____/_/ |_/_____/_/ |_/_/  |_/_____/___/_/ |_/_/  |_|");
        out.println("                                                                ");
    }

    /**
     * This is used to block the cli and do nothing
     *
     */
    public void hold(){
        try {
            Thread.currentThread().join();
        } catch (InterruptedException ex) {
            logger.info(ex.getMessage());
        }
    }

    /**
     * This is an helper to check if a user can reload
     *
     * @param reloadCost the cost to reload
     * @param ammoBag the ammo I have
     * @param powerUpBag the powerups I have ( to be used as ammo)
     * @return yes or no
     *
     */
    public Boolean canReload(List<AmmoColor> reloadCost, Map<AmmoColor, Integer> ammoBag, List<PowerUp> powerUpBag) {
        return !reloadCost.stream().anyMatch(c -> (!ammoBag.containsKey(c) || ammoBag.get(c) <= 0) && !powerUpBag.stream().anyMatch(u -> u.getPowerUpColor().equals(c)));
    }

    /**
     * This is the map loader from file
     *
     * @param mapNumber the id of the board
     * @return the string containing the map
     *
     */
    public String loadMapString(String mapNumber) {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(CLIUtil.class.getClassLoader().getResource("text/maps/map" + mapNumber + ".txt").toURI())))) {
            return br.lines().collect(Collectors.joining("\n"));
        } catch (IOException | URISyntaxException ex) {
            logger.info(ex.toString());
            return "";
        }
    }

    /**
     * This is to format the cards in a n column pattern using scanners
     *
     * @param n how many columns
     * @param scanners the list of scanners to pick the lines from
     * @return the string containing the desired pattern
     *
     */
    public String nColumnsFormatter(int n, Scanner[] scanners) {
        // print the cards in a n columns pattern
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < scanners.length; i += n) {
            while (scanners[i].hasNext()) {
                result.append(scanners[i].nextLine());
                for (int j = i + 1; j < scanners.length; j++) {
                    if (scanners[j].hasNext()) result.append(" " + scanners[j].nextLine());
                }
                result.append("\n");
            }
        }
        return result.toString();
    }

    /**
     * This is just a map used as a lookup table, in this case for each ammocrate we have the correct string
     *
     * @param name the key
     * @return the value
     *
     */
    public List<String> getUpperLowerCrate(String name){
        Map<String, List<String>> crateDict = new HashMap<>();
        crateDict.put("1_Blue_2_Red", Arrays.asList(Colors.BLACK_BACKGROUND + Colors.BLUE + " ■ " + Colors.RESET, Colors.BLACK_BACKGROUND + Colors.RED + "■ ■" + Colors.RESET));
        crateDict.put("1_Blue_2_Yellow", Arrays.asList(Colors.BLACK_BACKGROUND + Colors.BLUE + " ■ " + Colors.RESET, Colors.BLACK_BACKGROUND + Colors.YELLOW + "■ ■" + Colors.RESET));
        crateDict.put("1_PowerUp_1_Red_1_Blue", Arrays.asList(Colors.BLACK_BACKGROUND + " " + Colors.BLACK + Colors.WHITE_BACKGROUND + "◇" + Colors.RESET + Colors.BLACK_BACKGROUND + " " + Colors.RESET, Colors.BLACK_BACKGROUND + Colors.RED + "■ "+ Colors.RESET + Colors.BLACK_BACKGROUND + Colors.BLUE +"■" + Colors.RESET));
        crateDict.put("1_PowerUp_1_Yellow_1_Blue", Arrays.asList(Colors.BLACK_BACKGROUND + " " + Colors.BLACK + Colors.WHITE_BACKGROUND + "◇" + Colors.RESET + Colors.BLACK_BACKGROUND + " " + Colors.RESET, Colors.BLACK_BACKGROUND + Colors.YELLOW + "■ "+ Colors.RESET + Colors.BLACK_BACKGROUND + Colors.BLUE +"■" + Colors.RESET));
        crateDict.put("1_PowerUp_1_Yellow_1_Red", Arrays.asList(Colors.BLACK_BACKGROUND + " " + Colors.BLACK + Colors.WHITE_BACKGROUND + "◇" + Colors.RESET + Colors.BLACK_BACKGROUND + " " + Colors.RESET, Colors.BLACK_BACKGROUND + Colors.YELLOW + "■ "+ Colors.RESET + Colors.BLACK_BACKGROUND + Colors.RED +"■" + Colors.RESET));
        crateDict.put("1_PowerUp_2_Blue", Arrays.asList(Colors.BLACK_BACKGROUND + " " + Colors.BLACK + Colors.WHITE_BACKGROUND + "◇" + Colors.RESET + Colors.BLACK_BACKGROUND + " " + Colors.RESET, Colors.BLACK_BACKGROUND + Colors.BLUE + "■ "+ Colors.RESET + Colors.BLACK_BACKGROUND + Colors.BLUE +"■" + Colors.RESET));
        crateDict.put("1_PowerUp_2_Red", Arrays.asList(Colors.BLACK_BACKGROUND + " " + Colors.BLACK + Colors.WHITE_BACKGROUND + "◇" + Colors.RESET + Colors.BLACK_BACKGROUND + " " + Colors.RESET, Colors.BLACK_BACKGROUND + Colors.RED + "■ "+ Colors.RESET + Colors.BLACK_BACKGROUND + Colors.RED +"■" + Colors.RESET));
        crateDict.put("1_PowerUp_2_Yellow", Arrays.asList(Colors.BLACK_BACKGROUND + " " + Colors.BLACK + Colors.WHITE_BACKGROUND + "◇" + Colors.RESET + Colors.BLACK_BACKGROUND + " " + Colors.RESET, Colors.BLACK_BACKGROUND + Colors.YELLOW + "■ "+ Colors.RESET + Colors.BLACK_BACKGROUND + Colors.YELLOW +"■" + Colors.RESET));
        crateDict.put("1_Red_2_Blue", Arrays.asList(Colors.BLACK_BACKGROUND + Colors.RED + " ■ " + Colors.RESET, Colors.BLACK_BACKGROUND + Colors.BLUE + "■ "+ Colors.RESET + Colors.BLACK_BACKGROUND + Colors.BLUE +"■" + Colors.RESET));
        crateDict.put("1_Red_2_Yellow", Arrays.asList(Colors.BLACK_BACKGROUND + Colors.RED + " ■ " + Colors.RESET, Colors.BLACK_BACKGROUND + Colors.YELLOW + "■ "+ Colors.RESET + Colors.BLACK_BACKGROUND + Colors.YELLOW +"■" + Colors.RESET));
        crateDict.put("1_Yellow_2_Blue", Arrays.asList(Colors.BLACK_BACKGROUND + Colors.YELLOW + " ■ " + Colors.RESET, Colors.BLACK_BACKGROUND + Colors.BLUE + "■ "+ Colors.RESET + Colors.BLACK_BACKGROUND + Colors.BLUE +"■" + Colors.RESET));
        crateDict.put("1_Yellow_2_Red", Arrays.asList(Colors.BLACK_BACKGROUND + Colors.YELLOW + " ■ " + Colors.RESET, Colors.BLACK_BACKGROUND + Colors.RED + "■ "+ Colors.RESET + Colors.BLACK_BACKGROUND + Colors.RED +"■" + Colors.RESET));
        return crateDict.get(name);
    }

    /**
     * This is just a map used as a lookup table, in this case for each player color we have the color
     *
     * @param color the key
     * @return the value
     *
     */
    public String getPrintablePlayerColor(String color){
        Map<String, String> colorDict = new HashMap<>();
        colorDict.put("Blue", Colors.BLUE);
        colorDict.put("Green", Colors.GREEN);
        colorDict.put("Grey", Colors.WHITE);
        colorDict.put("Purple", Colors.PURPLE);
        colorDict.put("Yellow", Colors.YELLOW);
        return colorDict.get(color);
    }

    /**
     * This is just a map used as a lookup table, in this case for each room color we have the color
     *
     * @param color the key
     * @return the value
     *
     */
    public String getPrintableRoomColor(String color){
        Map<String, String> colorDict = new HashMap<>();
        colorDict.put("BLUE", Colors.BLUE);
        colorDict.put("RED", Colors.RED);
        colorDict.put("YELLOW", Colors.YELLOW);
        return colorDict.get(color);
    }

    /**
     * This is just a map used as a lookup table, in this case for each player color we have the background color
     *
     * @param color the key
     * @return the value
     *
     */
    public String getPrintableBackgroudPlayerColor(String color){
        Map<String, String> colorDict = new HashMap<>();
        colorDict.put("Blue", Colors.BLUE_BACKGROUND);
        colorDict.put("Green", Colors.GREEN_BACKGROUND);
        colorDict.put("Grey", Colors.WHITE_BACKGROUND);
        colorDict.put("Purple", Colors.PURPLE_BACKGROUND);
        colorDict.put("Yellow", Colors.YELLOW_BACKGROUND);
        return colorDict.get(color);
    }

    /**
     * This is to print to console
     *
     * @param content the key
     *
     */
    public synchronized void print(String content) { out.print(content); }

    /**
     * This is to print to console with newline
     *
     * @param content the key
     *
     */
    public synchronized void println(String content) { out.println(content); }
}
