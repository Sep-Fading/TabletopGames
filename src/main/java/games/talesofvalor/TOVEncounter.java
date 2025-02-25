package games.talesofvalor;

import core.CoreConstants;
import core.components.Component;
import games.talesofvalor.components.TOVEnemy;

import java.util.ArrayList;
import java.util.Objects;

public class TOVEncounter extends Component {
    int encounterLevel;
    public ArrayList<TOVEnemy> enemies;

    /**
     * Creates an encounter, which should be placed within a cell.
     * @param encounterLevel - The level of the encounter (Determines difficulty).
     * @param enemyCount     - Number of enemies in the encounter.
     */
    public TOVEncounter(int encounterLevel, int enemyCount) {
        super(CoreConstants.ComponentType.BOARD_NODE, "Encounter");
        this.encounterLevel = encounterLevel;
        this.enemies = new ArrayList<>();
        InitializeEnemies(enemyCount);
    }

    /**
     * Alternative constructor, used primarily in the copy method to
     * instantiate hard copies with ease.
     * @param componentID
     * @param encounterLevel
     */
    public TOVEncounter(int componentID, int encounterLevel, int enemyCount) {
        super(CoreConstants.ComponentType.BOARD_NODE, "Encounter", componentID);
        this.encounterLevel = encounterLevel;
        this.enemies = new ArrayList<>();
    }

    /**
     * Creates a hard copy of this encounter.
     * @return - copy of this encounter.
     */
    public TOVEncounter copy() {
        ArrayList<TOVEnemy> copyEnemies = new ArrayList<TOVEnemy>();
        // Get information from enemies to clone them.
        for (TOVEnemy enemy : enemies){
            copyEnemies.add(enemy.copy());
        }
        TOVEncounter encounterCopy = new TOVEncounter(componentID, encounterLevel, copyEnemies.size());
        encounterCopy.enemies = copyEnemies;
        return encounterCopy;
    }

    /**
     * Initializes the enemies in the encounter via an implemented algorithm.
     * It should decide how many enemies should exist in the encounter and generate them.
     * @param enemyCount - Number of enemies in the encounter.
     */
    private void InitializeEnemies(int enemyCount){
        for (int i = 0; i < enemyCount; i++){
            enemies.add(new TOVEnemy((int) (Math.random()*10)+1, (int) (Math.random()*10)+1));
        }
    }

    public boolean isCleared(){
        for (TOVEnemy enemy : enemies){
            if (!enemy.isDead){
                return false;
            }
        }
        return true;
    }

    /* Hashcode and equals */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TOVEncounter that = (TOVEncounter) o;
        return encounterLevel == that.encounterLevel && Objects.equals(enemies, that.enemies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(encounterLevel, enemies);
    }

    /* Getters & Setters */
    public int getEnemyCount() {
        return enemies.size();
    }
}