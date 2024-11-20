package games.talesofvalor;

import core.CoreConstants;
import core.components.Component;

import java.util.ArrayList;

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
    // Currently, assigns them random health and attack values from 0-10;
    // TODO: Implement a scaling function based on the encounter level.
    private void InitializeEnemies(int enemyCount){
        for (int i = 0; i < enemyCount; i++){
            enemies.add(new TOVEnemy((int) (Math.random()*10), (int) (Math.random()*10)));
        }
    }
}