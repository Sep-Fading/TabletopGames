package games.talesofvalor;

import core.CoreConstants;
import core.components.Component;

public class TOVEnemy extends Component {
    int attack;
    int health;
    boolean isDead = false;

    /**
     * Creates an enemy, which should be placed within an encounter.
     * @param attack - How hard the enemy hits.
     * @param health - Health of this enemy.
     */
    public TOVEnemy(int attack, int health) {
        super(CoreConstants.ComponentType.TOVENEMY, "Enemy");
        this.attack = attack;
        this.health = health;
    }

    /**
     * Alternative constructor, used primarily in the copy method to
     * instantiate hard copies with ease.
     * @param componentID
     * @param attack
     * @param health
     */
    public TOVEnemy(int componentID, int attack, int health) {
        super(CoreConstants.ComponentType.TOVENEMY, "Enemy", componentID);
        this.attack = attack;
        this.health = health;
    }

    /**
     * When the method is called, the enemy takes damage.
     * If the health of the enemy is less than or equal to 0, the enemy is dead.
     * e.g. if param damage is 5, health is 10, then health will be 5 via (10 - 5).
     * @param damage
     */
    public void takeDamage(int damage){
        if (isDead){
            return;
        }
        health -= damage;
        if (health <= 0){
            isDead = true;
        }
    }

    /**
     * Creates a hard copy of this enemy.
     * @return - copy of this enemy.
     */
    public TOVEnemy copy() {
        return new TOVEnemy(componentID, attack, health);
    }
}
