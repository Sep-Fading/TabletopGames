package games.talesofvalor;

import core.AbstractGameState;
import core.AbstractParameters;
import core.components.Component;
import core.components.Dice;
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
    private TOVRoundTypes previousRoundType;

    TOVDice d6f = new TOVDice(Dice.Type.d6);
    ArrayList<TOVOrderWrapper> combatTurnOrder = new ArrayList<>();

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
        ArrayList<Component> returnList = new ArrayList<>();
        returnList.add(grid);
        for (int i = 0; i < grid.getWidth(); i++){
            for (int j = 0; j < grid.getHeight(); j++){
                returnList.add(grid.getElement(i,j));
                if (grid.getElement(i,j).hasEncounter){
                    returnList.add(grid.getElement(i,j).encounter);
                    returnList.addAll(grid.getElement(i, j).encounter.enemies);
                }
            }
        }
        return returnList;
    }

    @Override
    protected AbstractGameState _copy(int playerId) {
        System.out.println("Copying GameState...");
        TOVGameState copy = new TOVGameState(gameParameters, getNPlayers());
        copy.grid = deepCopyGrid();
        copy.players = copyPlayers();
        copy.setRoundType(roundType);
        copy.setPreviousRoundType(previousRoundType);
        copy.d6f = d6f.copy();
        copy.combatTurnOrder = copyTurnOrder();

        return copy;
    }

    private ArrayList<TOVOrderWrapper> copyTurnOrder(){
        ArrayList<TOVOrderWrapper> copyOrder = new ArrayList<>();
        for (TOVOrderWrapper order : combatTurnOrder){
            copyOrder.add(order.copy());
        }
        return copyOrder;
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



    /**
     * Roll some initiative scores for the players and enemies in combat.
     * And wrap them in TOVOrderWrapper instances for ForwardModel to use.
     *
     * @param players - The players in combat.
     * @param enemies - The enemies in combat.
     */
    public ArrayList<TOVOrderWrapper> SetupCombatTurnOrder(ArrayList<TOVPlayer> players,
                                                         ArrayList<TOVEnemy> enemies){
        combatTurnOrder.clear();
        for (int i = 0; i < players.size(); i++){
            d6f.Roll(players.get(i).getDexterity());
            combatTurnOrder.add(new TOVOrderWrapper(d6f.getValue(), players.get(i)));
        }

        for (int i = 0; i < enemies.size(); i++){
            d6f.Roll(enemies.get(i).getDexterity());
            combatTurnOrder.add(new TOVOrderWrapper(d6f.getValue(), enemies.get(i)));
        }

        combatTurnOrder.sort((o1, o2) -> o2.getInitiative() - o1.getInitiative());
        return combatTurnOrder;
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
            grid.getElement(player.getPosition()).updateHasEncounter();
            if (grid.getElement(player.position).hasEncounter){
                System.out.println("Player " + player.getPlayerID() + " is in combat.");
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

    public TOVRoundTypes getPreviousRoundType() {
        return previousRoundType;
    }

    public void setRoundType(TOVRoundTypes roundType) {
        this.roundType = roundType;
    }

    public void setPreviousRoundType(TOVRoundTypes tovRoundTypes) {
        this.previousRoundType = tovRoundTypes;
    }

    @Override
    public double getGameScore(int playerId) {
        return 0; // TODO
    }

    public ArrayList<TOVOrderWrapper> getCombatOrder(){
        return combatTurnOrder;
    }
}
