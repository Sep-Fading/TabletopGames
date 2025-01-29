package games.talesofvalor;

import core.AbstractForwardModel;
import core.AbstractGameState;
import core.StandardForwardModel;
import core.actions.AbstractAction;
import core.components.GridBoard;
import games.talesofvalor.actions.TOVPlayerMove;
import tech.tablesaw.plotly.components.Grid;
import utilities.Vector2D;

import java.util.ArrayList;
import java.util.List;

public class TOVForwardModel extends StandardForwardModel {

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
    }

    @Override
    protected List<AbstractAction> _computeAvailableActions(AbstractGameState gameState) {
        List<AbstractAction> actions = new ArrayList<>();
        TOVGameState tovgs = (TOVGameState) gameState;
        TOVPlayerMove move = new TOVPlayerMove(new Vector2D(1, 0));
        actions.add(move);
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
        System.out.println("Before action");
    }
    /* -------------------- */

    @Override
    protected void _afterAction(AbstractGameState gameState, AbstractAction action) {
        System.out.println("After action");
        TOVGameState tovgs = (TOVGameState) gameState;
        TOVPlayerMove move = (TOVPlayerMove) action;
        endPlayerTurn(tovgs);
    }
}
