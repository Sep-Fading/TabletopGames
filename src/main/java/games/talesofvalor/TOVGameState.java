package games.talesofvalor;

import core.AbstractGameState;
import core.AbstractParameters;
import core.components.Component;
import core.components.GridBoard;
import games.GameType;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TOVGameState extends AbstractGameState {
    ArrayList<TOVPlayer> players;
    public GridBoard<TOVCell> grid;
    int encountersRemaining; // Used to determine a win condition.
    int totalEncounters; // Total encounters in the map when initialized.
    private TOVRoundTypes roundType;

    /**
     * @param gameParameters - game parameters.
     * @param nPlayers - Number of players.
     */
    public TOVGameState(AbstractParameters gameParameters, int nPlayers) {
        super(gameParameters, nPlayers);
        players = new ArrayList<>();
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
        copy.players = copyPlayers();
        copy.setRoundType(roundType);
        return copy;
    }

    private ArrayList<TOVPlayer> copyPlayers(){
        ArrayList<TOVPlayer> copyPlayers = new ArrayList<>();
        for (TOVPlayer player : players){
            copyPlayers.add(player.copy());
        }
        return copyPlayers;
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

    // Assigns a heuristic score based on the number of encounters completed by the player(s).
    @Override
    protected double _getHeuristicScore(int playerId) {
        if (isNotTerminal()){
            return (double) (totalEncounters - encountersRemaining) / totalEncounters;
        }
        else{
            if (encountersRemaining == 0){
                return 1;
            }
            else{
                return 0;
            }
        }
    }

    /**
     * Sets a round type after checking for conditions:
     * If players entered a cell with an encounter -> IN_COMBAT_INITIAL - TODO
     * If players are in a cell with an encounter -> IN_COMBAT
     * If players are not in/defeated a cell with an encounter -> OUT_OF_COMBAT
     */
    public void UpdateRoundType(){
        TOVRoundTypes newRoundType = TOVRoundTypes.OUT_OF_COMBAT;
        for (TOVPlayer player : players){
            if (grid.getElement(player.position).hasEncounter){
                newRoundType = TOVRoundTypes.IN_COMBAT;
            }
        }
        setRoundType(newRoundType);
    }

    /* HashCode and Equals */
    @Override
    public int hashCode() {
        return Objects.hash(grid);
    }

    @Override
    protected boolean _equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TOVGameState that = (TOVGameState) o;
        return Objects.equals(grid, that.grid);
    }


    /* Getters and Setters */
    public GridBoard<TOVCell> getGridBoard() {
        return grid;
    }

    /**
     * Returns the player with the given ID.
     * @param playerID
     * @return - Corresponding TOVPlayer.
     */
    public TOVPlayer getTOVPlayerByID(int playerID) {
        for (TOVPlayer player : players) {
            if (player.getPlayerID() == playerID) {
                return player;
            }
        }
        return null;
    }

    public TOVRoundTypes getRoundType() {
        return roundType;
    }

    public void setRoundType(TOVRoundTypes roundType) {
        this.roundType = roundType;
    }

    @Override
    public double getGameScore(int playerId) {
        return 0; // TODO
    }
}
