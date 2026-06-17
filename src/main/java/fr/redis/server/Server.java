package fr.redis.server;

import fr.redis.engine.CommandExecutor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class Server {
    private final CommandExecutor executor;
    private boolean running;

    public Server() {
        this.executor = new CommandExecutor();
        this.running = false;
    }

    public void start() {
        running = true;
        ColorConsole.printWelcome();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (running) {
            ColorConsole.printPrompt();
            try {
                String line = reader.readLine();
                if (line == null) break;

                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.equalsIgnoreCase("EXIT")) {
                    running = false;
                    ColorConsole.info("Au revoir !");
                    continue;
                }

                if (line.equalsIgnoreCase("HELP")) {
                    ColorConsole.printHelp();
                    continue;
                }

                if (line.toUpperCase().startsWith("LOAD ")) {
                    String filename = line.substring(5).trim();
                    loadFile(filename);
                    continue;
                }

                String result = executor.execute(line);
                printResult(result);

            } catch (IOException e) {
                ColorConsole.error("Erreur de lecture: " + e.getMessage());
                running = false;
            }
        }
    }

    private void loadFile(String filename) {
        try (BufferedReader fileReader = new BufferedReader(new FileReader(filename))) {
            ColorConsole.info("Chargement du fichier: " + filename);
            int count = 0;
            String line;
            while ((line = fileReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String result = executor.execute(line);
                System.out.println("  " + line);
                printResult(result);
                count++;
            }
            ColorConsole.success("  " + count + " commandes executees.");
        } catch (IOException e) {
            ColorConsole.error("Erreur: fichier non trouve -> " + filename);
        }
    }

    private void printResult(String result) {
        if (result.startsWith("ERR") || result.startsWith("(error)")) {
            ColorConsole.error(result);
        } else if (result.equals("OK")) {
            ColorConsole.success(result);
        } else {
            System.out.println(result);
        }
    }

    public void stop() {
        running = false;
    }

    public CommandExecutor getExecutor() {
        return executor;
    }
}
