package games.talesofvalor;

import core.AbstractForwardModel;
import core.AbstractGameState;
import core.StandardForwardModel;
import core.actions.AbstractAction;
import core.components.Dice;
import core.components.GridBoard;
import games.talesofvalor.actions.TOVPlayerMove;
import tech.tablesaw.plotly.components.Grid;
import utilities.Vector2D;

import java.util.*;

public class TOVForwardModel extends StandardForwardModel {

    TOVDice d6f = new TOVDice(Dice.Type.d6);

    @Override
    protected void _setup(AbstractGameState firstState) {
        TOVGameState tovgs = (TOVGameState) firstState;
        TOVParameters tovp = (TOVParameters) tovgs.getGameParameters();

        // Create the map, counting the encounters as we go.
        tovgs.grid = new GridBoard<TOVCell>(tovp.gridWidth, tovp.gridHeight);
        for (int i = 0; i < tovp.gridHeight; i++) {
            for (int j = 0; j < tovp.gridWidth; j++) {
                tovgs.grid.setElement(j, i, new TOVCell(j, i));
                if (tovgs.grid.getElement(j, i).hasEncounter) {
                    tovgs.totalEncounters++;
                }
            }
        }
        tovgs.encountersRemaining = tovgs.totalEncounters;

        // Create & place TOVPlayer instances for each player.
        for (int i = 0; i < tovgs.getNPlayers(); i++) {
            TOVPlayer player = new TOVPlayer(i);
            tovgs.players.add(player);
            tovgs.grid.getElement(0, 0).SetPlayerCount(
                    tovgs.grid.getElement(0, 0).GetPlayerCount() + 1);
        }

        // Initialize the roundType.
        tovgs.setRoundType(TOVRoundTypes.OUT_OF_COMBAT);
    }

    @Override
    protected List<AbstractAction> _computeAvailableActions(AbstractGameState gameState) {
        List<AbstractAction> actions = new ArrayList<>();
        TOVGameState tovgs = (TOVGameState) gameState;
        System.out.println("Current round type: " + tovgs.getRoundType());
        if (tovgs.getRoundType() == TOVRoundTypes.OUT_OF_COMBAT) {
            TOVPlayer currentPlayer = tovgs.getTOVPlayerByID(tovgs.getCurrentPlayer());
            Vector2D currentPosition = currentPlayer.getPosition();
            int x = currentPosition.getX();
            int y = currentPosition.getY();
            d6f.Roll(currentPlayer.getDexterity());
            int d6Roll = d6f.getValue();

            System.out.println("Player " + tovgs.getCurrentPlayer() + " rolled a " + d6Roll);

            // A BFS implementation to calculate all reachable cells to multi-directional movement.
            boolean visited[][] = new boolean[tovgs.grid.getWidth()][tovgs.grid.getHeight()];
            int[][] distance = new int[tovgs.grid.getWidth()][tovgs.grid.getHeight()];

            // Initialise all possible movements.
            for (int i = 0; i < tovgs.grid.getWidth(); i++) {
                for (int j = 0; j < tovgs.grid.getHeight(); j++) {
                    visited[i][j] = false;
                    distance[i][j] = Integer.MAX_VALUE;
                }
            }

            Queue<Vector2D> moveQueue = new LinkedList<>();
            // Starting point
            moveQueue.add(new Vector2D(x, y));
            visited[x][y] = true;
            distance[x][y] = 0;

            // BFS on the grid.
            while (!moveQueue.isEmpty()){
                Vector2D current = moveQueue.poll();
                int currentX = current.getX();
                int currentY = current.getY();
                int dist = distance[currentX][currentY];

                // Move to neighbours if distance is within movement budget.
                if (dist < d6Roll){
                    for (Vector2D dir : new Vector2D[]{
                        new Vector2D(1,0), new Vector2D(-1,0),
                        new Vector2D(0,1), new Vector2D(0,-1)
                    }){
                        int newX = currentX + dir.getX();
                        int newY = currentY + dir.getY();
                        if (newX >= 0 && newX < tovgs.grid.getWidth() &&
                                newY >= 0 && newY < tovgs.grid.getHeight() &&
                                !visited[newX][newY]){
                            visited[newX][newY] = true;
                            distance[newX][newY] = dist + 1;
                            moveQueue.add(new Vector2D(newX, newY));
                        }
                    }
                }
            }

            // Creating an action for all reachable cells.
            for (int i = 0; i < tovgs.grid.getWidth(); i++) {
                for (int j = 0; j < tovgs.grid.getHeight(); j++) {
                    if (visited[i][j]) {
                        int dist = distance[i][j];
                        if (dist <= d6Roll){
                            if (dist > 0){
                                int dx = i - x;
                                int dy = j - y;
                                actions.add(new TOVPlayerMove(new Vector2D(dx, dy)));
                            }
                        }
                    }
                }
            }


        }

        // Do nothing option
        actions.add(new TOVPlayerMove(new Vector2D(0, 0)));

        return actions;
    }

    /* --- ROUND START --- */
    /**
     * OUT_OF_COMBAT:
     * 1. Dice roll to determine the current player's movement capabilities. Dexterity affects the roll.
     * ----------------------------------------
     * IN_COMBAT_INITIAL:
     * 1. Dice roll to determine the turn orders. Dexterity affects the roll.
     * ----------------------------------------
     * IN_COMBAT:
     *
     */
    @Override
    protected void _beforeAction(AbstractGameState gameState, AbstractAction action) {
        TOVGameState tovgs = (TOVGameState) gameState;
        TOVRoundTypes round = tovgs.getRoundType();
        switch (round){
            case OUT_OF_COMBAT:
                d6f.Roll(tovgs.getTOVPlayerByID(tovgs.getCurrentPlayer()).getDexterity());
                break;
        }
    }
    /* -------------------- */

    /* --- ROUND END --- */
    /**
     * 1. Update roundType in TOVGameState.
     */
    @Override
    protected void _afterAction(AbstractGameState gameState, AbstractAction action) {
        System.out.println("After action");
        TOVGameState tovgs = (TOVGameState) gameState;
        TOVPlayerMove move = (TOVPlayerMove) action;
        tovgs.UpdateRoundType();
        endPlayerTurn(tovgs);
    }
}
