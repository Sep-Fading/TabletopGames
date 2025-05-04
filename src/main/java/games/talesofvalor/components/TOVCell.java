package games.talesofvalor.components;

import core.CoreConstants;
import core.components.Component;
import games.talesofvalor.TOVParameters;
import utilities.Vector2D;

import java.util.Objects;

public class TOVCell extends Component {
    // --- In-Game Event Flags ---
    public boolean hasEncounter;
    public boolean hasJester;
    public boolean hasShrine;
    // ----------------------------

    private int playerCount;
    Vector2D position; // The position of the cell.
    public TOVEncounter encounter;


    /**
     * Constructor for the TOVCell class:
     * @param x - x coordinate of the cell.
     * @param y - y coordinate of the cell.
     */
    public TOVCell(int x, int y, int totalEncounters, int totalJesters,
                   int totalShrines) {
        super(CoreConstants.ComponentType.BOARD_NODE, "Tile");

        if (x != 0 && y != 0) {
            if (TOVParameters.maxEncounters > totalEncounters) {
                hasEncounter = ChanceEvents(0.3);
            } else {
                hasEncounter = false;
            }

            if (!hasEncounter && TOVParameters.maxJesters > totalJesters) {
                hasJester = ChanceEvents(0.1);
            }
            else {
                hasJester = false;
            }

            if (!hasEncounter && !hasJester && TOVParameters.maxShrines > totalShrines) {
                hasShrine = ChanceEvents(0.1);
            }
            else {
                hasShrine = false;
            }

            System.out.println("Encounter at " + x + ", " + y + ": " + hasEncounter);
            System.out.println("Jester at " + x + ", " + y + ": " + hasJester);
            System.out.println("Shrine at " + x + ", " + y + ": " + hasShrine);
            if (hasEncounter) {
                encounter = InitializeEncounter();
            }
        }
        position = new Vector2D(x, y);
    }

    /**
     * Copy constructor for the TOVCell class:
     * @param componentID - ID of the component.
     * @param position - Position of the cell.
     */
    private TOVCell(int componentID, Vector2D position,
                    TOVEncounter encounter, boolean hasEncounter,
                    boolean hasJester, boolean hasShrine) {
        super(CoreConstants.ComponentType.BOARD_NODE, "Tile", componentID);
        this.position = position;
        this.encounter = encounter;
        this.hasEncounter = hasEncounter;
        this.hasJester = hasJester;
        this.hasShrine = hasShrine;
    }

    /**
     * Decides via an implemented algorithm whether the cell has an encounter.
     * @return - True if the cell has an encounter, false otherwise.
     */
    private boolean ChanceEvents(double chance){
        return Math.random() < chance;
    }

    /**
     * Initializes an encounter for the cell.
     * @return - The initialized encounter.
     */
    private TOVEncounter InitializeEncounter(){
        return new TOVEncounter(1, (int) (Math.random()*3)+1);
    }

    // Creates a hard copy of the cell to return.
    public TOVCell copy(){
        TOVCell copyCell = new TOVCell(componentID, position,
                (encounter != null) ? encounter.copy() : null, hasEncounter,
                hasJester, hasShrine);
        if (encounter != null && copyCell.encounter == null){
            System.out.println("Encounter is lost during copy!!!");
        }
        copyCell.SetPlayerCount(playerCount);
        return copyCell;
    }


    /* Checks to see if the encounter still has enemies that are alive to update hasEncounter */
    public void updateHasEncounter(){
        if (encounter == null){
            //System.out.println("Encounter is null.");
            hasEncounter = false;
            return;
        }
        if (encounter.isCleared()){
            hasEncounter = false;
            System.out.println("Encounter cleared.");
        }
        else{
            hasEncounter = true;
        }
    }

    /* HashCode and Equals */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TOVCell tovCell = (TOVCell) o;
        return hasEncounter == tovCell.hasEncounter &&
                hasJester == tovCell.hasJester &&
                hasShrine == tovCell.hasShrine &&
                Objects.equals(position, tovCell.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hasEncounter, hasJester, hasShrine, position);
    }


    /* Getters and Setters */

    /**
     * Sets the player count for the cell.
     * Update PlayerCount after a player moves into or out of the cell,
     * for an accurate representation of the grid on the GUI.
     * @param n - The number of players in the cell.
     */
    public void SetPlayerCount(int n){
        if (n >= 0){
            playerCount = n;
        }
    }

    public int GetPlayerCount(){
        return playerCount;
    }

    public TOVEncounter GetEncounter(){
        return encounter;
    }
}
