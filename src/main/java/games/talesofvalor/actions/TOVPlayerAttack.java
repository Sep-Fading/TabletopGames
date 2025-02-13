package games.talesofvalor.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import games.talesofvalor.TOVEnemy;
import games.talesofvalor.TOVGameState;

import java.util.Objects;

public class TOVPlayerAttack extends AbstractAction {

    TOVEnemy target;

    public TOVPlayerAttack(TOVEnemy target) {
        this.target = target;
    }

    @Override
    public boolean execute(AbstractGameState gs) {
        System.out.println(getString(gs));
        TOVGameState tovgs = (TOVGameState) gs;
        if (target.isDead()) {
            System.out.println("Target is already dead.");
            return false;
        }
        tovgs.getTOVPlayerByID(tovgs.getCurrentPlayer()).Attack(target);
        System.out.println("Player attacked" + target.getHealth());
        return true;
    }


    /**
     * Creates a hard copy of this action.
     * @return - copy of this action.
     */
    @Override
    public AbstractAction copy() {
        return new TOVPlayerAttack(target.copy());
    }

    /* Hashcode and equals */
    @Override
    public int hashCode() {
        return Objects.hash(target);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TOVPlayerAttack that = (TOVPlayerAttack) obj;
        return Objects.equals(target, that.target);
    }

    @Override
    public String getString(AbstractGameState gameState) {
        // TODO - More sophisticated string representation with name/id of the enemy targeted.
        TOVGameState tovgs = (TOVGameState) gameState;
        return "Attempting to attack for player " + tovgs.getCurrentPlayer();
    }
}
