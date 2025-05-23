package games.talesofvalor.components;

import core.CoreConstants;
import core.components.Component;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

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
            // Random attack value between 1 and 8 and random health value between 10 and 20.
            enemies.add(new TOVEnemy(ThreadLocalRandom.current().nextInt(1, 9),
                    ThreadLocalRandom.current().nextInt(10, 21)));
        }
    }

    public boolean isCleared(){
        for (TOVEnemy enemy : enemies){
            if (!enemy.isDead()){
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