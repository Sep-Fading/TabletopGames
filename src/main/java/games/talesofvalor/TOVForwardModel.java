package games.talesofvalor;

import core.AbstractForwardModel;
import core.AbstractGameState;
import core.StandardForwardModel;
import core.actions.AbstractAction;
import core.components.GridBoard;
import tech.tablesaw.plotly.components.Grid;

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
                    tovgs.encountersRemaining++;
                }
            }
        }
    }

    @Override
    protected List<AbstractAction> _computeAvailableActions(AbstractGameState gameState) {
        return List.of();
    }
}
