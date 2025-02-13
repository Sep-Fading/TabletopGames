package games.talesofvalor;

import core.CoreConstants;
import core.components.Component;

import java.util.Objects;

public class TOVEnemy extends Component {
    int attack;
    int health;
    int dexterity = 0;
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
    public TOVEnemy(int componentID, int attack, int health, int dexterity) {
        super(CoreConstants.ComponentType.TOVENEMY, "Enemy", componentID);
        this.attack = attack;
        this.health = health;
        this.dexterity = dexterity;
    }

    /**
     * When the method is called, the enemy takes damage.
     * If the health of the enemy is less than or equal to 0, the enemy is dead.
     * e.g. if param damage is 5, health is 10, then health will be 5 via (10 - 5).
     * @param damage
     */
    public void takeDamage(int damage){
        if (isDead){
            System.out.println("Enemy is already dead.");
            return;
        }
        health = health - damage;
        if (health <= 0){
            isDead = true;
            System.out.println("Enemy is dead from taking damage.");
        }
    }

    /**
     * Creates a hard copy of this enemy.
     * @return - copy of this enemy.
     */
    public TOVEnemy copy() {
        return new TOVEnemy(componentID, attack, health, dexterity);
    }

    /* Getters & Setters */
    public int getAttack() {
        return attack;
    }

    public int getHealth() {
        return health;
    }

    public boolean isDead() {
        return isDead;
    }

    public int getDexterity() {
        return dexterity;
    }

    /* Hashcode and equals */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TOVEnemy tovEnemy = (TOVEnemy) o;
        return getAttack() == tovEnemy.getAttack() && getHealth() == tovEnemy.getHealth() &&
                getDexterity() == tovEnemy.getDexterity() && isDead() == tovEnemy.isDead();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getAttack(), getHealth(), getDexterity(), isDead());
    }
}
