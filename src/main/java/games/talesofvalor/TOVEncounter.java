package games.talesofvalor;

import core.CoreConstants;
import core.components.Component;

import java.util.ArrayList;
import java.util.Objects;

public class TOVEncounter extends Component {
    int encounterLevel;
    int enemyCount;
    ArrayList<TOVEnemy> enemies = new ArrayList<TOVEnemy>();

    /**
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

    public TOVEncounter copy() {
        ArrayList<TOVEnemy> copyEnemies = new ArrayList<TOVEnemy>();
        // Get information from enemies to clone them.
        for (TOVEnemy enemy : enemies){
            copyEnemies.add(enemy.copy());
        }
        return new TOVEncounter(componentID, encounterLevel, enemyCount, copyEnemies);
    }

    // Initializes the enemies for the encounter.
    // Currently, assigns them random health and attack values from 0-10 to be expanded later.
    // TODO: Implement a scaling function based on the encounter level.
    private void InitializeEnemies(int enemyCount){
        for (int i = 0; i < enemyCount; i++){
            enemies.add(new TOVEnemy((int) (Math.random()*10)+1, (int) (Math.random()*10)+1));
        }
    }

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
}