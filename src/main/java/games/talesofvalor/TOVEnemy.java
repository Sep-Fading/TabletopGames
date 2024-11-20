package games.talesofvalor;

import core.CoreConstants;
import core.components.Component;

public class TOVEnemy extends Component {
    int attack;
    int health;

    /**
     * @param attack - How hard the enemy hits.
     * @param health - Health of this enemy.
     */
    public TOVEnemy(int attack, int health) {
        super(CoreConstants.ComponentType.TOVENEMY, "Enemy");
        this.attack = attack;
        this.health = health;
    }

    public TOVEnemy(int componentID, int attack, int health) {
        super(CoreConstants.ComponentType.TOVENEMY, "Enemy", componentID);
        this.attack = attack;
        this.health = health;
    }

    public TOVEnemy copy() {
        return new TOVEnemy(componentID, attack, health);
    }
}
