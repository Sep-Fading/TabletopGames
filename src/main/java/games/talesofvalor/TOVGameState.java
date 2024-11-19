package games.talesofvalor;

import core.AbstractGameState;
import core.AbstractParameters;
import core.components.Component;
import core.components.GridBoard;
import games.GameType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TOVGameState extends AbstractGameState {

    GridBoard<TOVCell> grid;

    /**
     * @param gameParameters - game parameters.
     * @param nPlayers - Number of players.
     */
    public TOVGameState(AbstractParameters gameParameters, int nPlayers) {
        super(gameParameters, nPlayers);
    }

    @Override
    protected GameType _getGameType() {
        return GameType.TalesOfValor;
    }

    @Override
    protected List<Component> _getAllComponents() {
        return new ArrayList<Component>() {{add(grid);}};
    }

    @Override
    protected AbstractGameState _copy(int playerId) {
        TOVGameState copy = new TOVGameState(gameParameters, getNPlayers());
        copy.grid = deepCopyGrid();
        return copy;
    }

    private GridBoard<TOVCell> deepCopyGrid(){
        GridBoard<TOVCell> gridCopy = grid.copy();
        for (int i = 0; i < grid.getHeight(); i++){
            for (int j = 0; j < grid.getWidth(); j++){
                gridCopy.setElement(j,i, grid.getElement(j,i).copy());
            }
        }
        return gridCopy;
    }

    @Override
    protected double _getHeuristicScore(int playerId) {
        return 0; // TODO
    }

    @Override
    public double getGameScore(int playerId) {
        return 0; // TODO
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), grid);
    }

    @Override
    protected boolean _equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TOVGameState that = (TOVGameState) o;
        return Objects.equals(grid, that.grid);
    }
}
