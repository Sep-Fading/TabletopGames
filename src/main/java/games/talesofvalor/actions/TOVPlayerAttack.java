package games.talesofvalor.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import games.talesofvalor.TOVEnemy;
import games.talesofvalor.TOVGameState;

import java.util.Objects;

public class TOVPlayerAttack extends AbstractAction {
    int compid = -1;
    public TOVPlayerAttack(int compid) {
        this.compid = compid;
    }

    @Override
    public boolean execute(AbstractGameState gs) {
        System.out.println(getString(gs));
        TOVGameState tovgs = (TOVGameState) gs;
        System.out.println("ENEMY COMP ID IN EXECUTE FUNCTION: " + compid);
        TOVEnemy target = (TOVEnemy) tovgs.getComponentById(compid);
        if (target == null) {
            System.out.println("No target to attack.");
            return true;
        }
        else if (target.isDead()) {
            System.out.println("Target is already dead.");
            return false;
        }
        System.out.println("Player attacking target with health: " + target.getHealth());
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
        return new TOVPlayerAttack(compid);
    }

    /* Hashcode and equals */
    @Override
    public int hashCode() {
        return Objects.hash(compid);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return compid == ((TOVPlayerAttack) obj).compid;
    }


    @Override
    public String getString(AbstractGameState gameState) {
        // TODO - More sophisticated string representation with name/id of the enemy targeted.
        TOVGameState tovgs = (TOVGameState) gameState;
        return "Attempting to attack for player " + tovgs.getCurrentPlayer();
    }
}
