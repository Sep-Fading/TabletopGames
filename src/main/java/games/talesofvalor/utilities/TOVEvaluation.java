package games.talesofvalor.utilities;

import core.*;
import games.GameType;
import games.talesofvalor.TOVForwardModel;
import games.talesofvalor.TOVGameState;
import games.talesofvalor.TOVParameters;
import players.basicMCTS.BasicMCTSPlayer;
import players.mcts.MCTSPlayer;
import players.simple.RandomPlayer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TOVEvaluation {

    // Configurations
    private static int nGames = 10; // Number of games played by simulation.
    private static int nPlayers = 3; // Number of players in the games.
    private static String outputDirectory = "./stats/"; // File to write the results to.
    private static String csvFilePath;

    // Game number tracker
    private static int gameNumber;

    static {
        // Ensure the output directory exists
        File dir = new File(outputDirectory);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                System.err.println("Failed to create output directory: " + outputDirectory);
            }
        }

        // Timestamping files so we get separate files per experiment.
        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String ts = LocalDateTime.now().format(dtFormatter);
        csvFilePath = outputDirectory + "action_data_" + ts + ".csv";

        // Headers
        try (FileWriter writer = new FileWriter(csvFilePath)) {
            writer.write("Game,PlayerID,Health,ActionType,ActionString,HeuristicValue\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // A method to write stuff to a csv.
    // Player ID | Player Health | Action Type | Action String | Heuristic Value
    public static void logActionData(String id, String hp, String actionType, String actionString,
                                     String heuristicValue) {
        try (FileWriter writer = new FileWriter(csvFilePath, true)) {
            writer.append(String.join(",", String.valueOf(gameNumber), id, hp, actionType, actionString, heuristicValue));
            writer.append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void CalculateAndPrintMetrics(ArrayList<Integer> wonGames,
                                                 ArrayList<Double> timeTakenToComplete) {
        double winRate = (double) wonGames.size() / nGames * 100;
        double averageTime = timeTakenToComplete.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        System.out.println("Total Games Played: " + nGames);
        System.out.println("Total Wins: " + wonGames.size());
        System.out.println("Win Rate: " + winRate + "%");
        System.out.println("Average Time Taken: " + averageTime + " seconds");
        System.out.println("Games that won:" + wonGames);
    }

    public static void main(String[] args) {
        // Trackers
        gameNumber = 0;
        ArrayList<Integer> wonGames = new ArrayList<>();
        ArrayList<Double> timeTakenToComplete = new ArrayList<>(); // To track time taken per game in seconds.

        for (int i = 0; i < nGames; i++) {
            long startTime = System.nanoTime();

            // Create players
            ArrayList<AbstractPlayer> players = new ArrayList<>();

            players.add(new BasicMCTSPlayer());
            players.add(new BasicMCTSPlayer());
            players.add(new BasicMCTSPlayer());
            //players.add(new MCTSPlayer());
            //players.add(new MCTSPlayer());
            //players.add(new MCTSPlayer());
            //players.add(new RandomPlayer());
            //players.add(new RandomPlayer());
            //players.add(new RandomPlayer());

            TOVParameters params = new TOVParameters();
            AbstractForwardModel forwardModel = new TOVForwardModel();
            AbstractGameState gameState = new TOVGameState(params, nPlayers);

            Game game = new Game(
                    GameType.TalesOfValor,
                    players,
                    forwardModel,
                    gameState
            );

            game.run();

            // Track time taken to complete the game
            long endTime = System.nanoTime();
            double durationSeconds = (endTime - startTime) / 1_000_000_000.0;
            timeTakenToComplete.add(durationSeconds);

            var gameResults = game.getGameState().getPlayerResults();
            if (gameResults != null && (gameResults[0] == CoreConstants.GameResult.WIN_GAME)) {
                wonGames.add(i);
            }
            gameNumber++;
        }

        // Print out the results
        CalculateAndPrintMetrics(wonGames, timeTakenToComplete);
    }
}
