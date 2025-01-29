package games.talesofvalor;

import core.CoreConstants;
import core.components.Component;

import java.util.ArrayList;
import java.util.Objects;

public class TOVEncounter extends Component {
    int encounterLevel;
    int enemyCount;
    public ArrayList<TOVEnemy> enemies = new ArrayList<TOVEnemy>();

    /**
     * Creates an encounter, which should be placed within a cell.
     * @param encounterLevel - The level of the encounter (Determines difficulty).
     * @param enemyCount     - Number of enemies in the encounter.
     */
    public TOVEncounter(int encounterLevel, int enemyCount) {
        super(CoreConstants.ComponentType.BOARD_NODE, "Encounter");
        this.encounterLevel = encounterLevel;
        this.enemyCount = enemyCount;
        InitializeEnemies(enemyCount);
    }

    /**
     * Alternative constructor, used primarily in the copy method to
     * instantiate hard copies with ease.
     * @param componentID
     * @param encounterLevel
     * @param enemyCount
     */
    public TOVEncounter(int componentID, int encounterLevel, int enemyCount,
                        ArrayList<TOVEnemy> enemies) {
        super(CoreConstants.ComponentType.BOARD_NODE, "Encounter", componentID);
        this.encounterLevel = encounterLevel;
        this.enemyCount = enemyCount;
        this.enemies = enemies;
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
        return new TOVEncounter(componentID, encounterLevel, enemyCount, copyEnemies);
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

    /* Hashcode and equals */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TOVEncounter that = (TOVEncounter) o;
        return encounterLevel == that.encounterLevel && enemyCount == that.enemyCount && Objects.equals(enemies, that.enemies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(encounterLevel, enemyCount, enemies);
    }

    /* Getters & Setters */
    public int getEnemyCount() {
        return enemyCount;
    }
}