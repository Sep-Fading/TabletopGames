package games.talesofvalor;

import core.CoreConstants;
import core.components.Component;
import org.checkerframework.checker.units.qual.C;
import utilities.Vector2D;

import java.util.ArrayList;
import java.util.Objects;

public class TOVCell extends Component {
    boolean hasEncounter; // To determine if the cell has an encounter.
    private int playerCount;
    Vector2D position; // The position of the cell.
    TOVEncounter encounter;

    /**
     * Constructor for the TOVCell class:
     * @param x - x coordinate of the cell.
     * @param y - y coordinate of the cell.
     */
    public TOVCell(int x, int y) {
        super(CoreConstants.ComponentType.BOARD_NODE, "Tile");
        // Completely random right now, but supposed to use some more nuanced generation later.

        if (x != 0 && y != 0) {
            hasEncounter = setRandomEncounter();
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
     * @param hasEncounter - Whether the cell has an encounter.
     * @param encounter - The encounter in the cell.
     */
    private TOVCell(int componentID, Vector2D position, boolean hasEncounter, TOVEncounter encounter) {
        super(CoreConstants.ComponentType.BOARD_NODE, "Tile", componentID);
        this.position = position;
        this.hasEncounter = hasEncounter;
        this.encounter = encounter;
    }

    // Randomly assigns an encounter to the cell.
    private boolean setRandomEncounter(){
        return Math.random() < 0.2;
    }

    // Initializes an encounter for the cell.
    private TOVEncounter InitializeEncounter(){
        return new TOVEncounter(1, (int) (Math.random()*3)+1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TOVCell tovCell = (TOVCell) o;
        return hasEncounter == tovCell.hasEncounter && Objects.equals(position, tovCell.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hasEncounter, position);
    }

    public TOVCell copy(){
        TOVEncounter encounterCopy;
        if (hasEncounter){
            encounterCopy = encounter.copy();
        } else {
            encounterCopy = null;
        }
        TOVCell copyCell = new TOVCell(componentID, position.copy(), hasEncounter, encounterCopy);
        copyCell.SetPlayerCount(playerCount);
        return copyCell;
    }

    public void SetPlayerCount(int n){
        if (n >= 0){
            playerCount = n;
        }
    }

    public int GetPlayerCount(){
        return playerCount;
    }
}
