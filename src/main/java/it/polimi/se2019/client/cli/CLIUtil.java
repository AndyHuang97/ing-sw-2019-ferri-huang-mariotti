package it.polimi.se2019.client.cli;

import java.io.*;
import java.util.Collections;
import java.util.Scanner;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class CLIUtil {
    private Scanner in = new Scanner(System.in);
    private PrintStream out = System.out;

    private String basicUserInput(String printString) {
        out.print(printString);
        return in.nextLine();
    }

    public String askUserInput(String question) {
        return basicUserInput(question + "\n> ");
    }

    public String askUserInput(String question, String defaultResponse) {
        String response = basicUserInput(question + " (" + defaultResponse + ")\n> ");
        if (response.equals("")) response = defaultResponse;
        return response;
    }

    private String columnStringBuilder(String question, List<String> validResponses) {
        StringBuilder printString = new StringBuilder();
        printString.append(question + "\n");
        for (int i = 0; i < validResponses.size(); i++) printString.append(i + ") " + validResponses.get(i) + "\n");
        printString.append(">");
        return printString.toString();
    }

    public String askUserInput(String question, List<String> validResponses, Boolean numbered) {
        if (numbered) {
            String indexResponse, response;
            do {
                indexResponse = basicUserInput(columnStringBuilder(question, validResponses));
                try {
                    response = validResponses.get(Integer.parseInt(indexResponse));
                } catch (Exception ex) {
                    response = null;
                }
            } while (!validResponses.contains(response));
            return response;
        } else {
            String response;
            do {
                response = basicUserInput(question + " [" + String.join("/", validResponses) + "]\n> ");
            } while (!validResponses.contains(response));
            return response;
        }
    }

    public String askUserInput(String question, String defaultResponse, List<String> validResponses, Boolean numbered) {
        if (!validResponses.contains(defaultResponse)) validResponses.add(0, defaultResponse);
        Collections.replaceAll(validResponses, defaultResponse, "(" + defaultResponse + ")");
        if (numbered) {
            String indexResponse, response;
            do {
                indexResponse = basicUserInput(columnStringBuilder(question, validResponses));
                if (indexResponse.equals("")) return defaultResponse;
                try {
                    response = validResponses.get(Integer.parseInt(indexResponse));
                } catch (Exception ex) {
                    response = null;
                }
            } while (!validResponses.contains(response));
            return response;
        } else {
            String response;
            do {
                response = basicUserInput(question + " [" + String.join("/", validResponses) + "]\n> ");
                if (response.equals("")) response = defaultResponse;
            } while (!validResponses.contains(response));
            return response;
        }
    }

    public void printBanner() {
        out.println("      ___    ____  ____  _______   _____    __    _____   _____ ");
        out.println("     /   |  / __ \\/ __ \\/ ____/ | / /   |  / /   /  _/ | / /   |");
        out.println("    / /| | / / / / /_/ / __/ /  |/ / /| | / /    / //  |/ / /| |");
        out.println("   / ___ |/ /_/ / _, _/ /___/ /|  / ___ |/ /____/ // /|  / ___ |");
        out.println("  /_/  |_/_____/_/ |_/_____/_/ |_/_/  |_/_____/___/_/ |_/_/  |_|");
        out.println("                                                                ");
    }

    public void hold(){
        try {
            Thread.currentThread().join();
        } catch (InterruptedException ex) {
            //TODO: Handle this
        }
    }

    public PrintStream getOutStream() { return out; }
}
