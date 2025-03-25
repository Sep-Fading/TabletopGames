package games.talesofvalor.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import games.talesofvalor.TOVGameState;
import games.talesofvalor.TOVPlayer;
import org.apache.ivy.osgi.repo.EditableRepoDescriptor;
import utilities.Vector2D;

import java.util.Objects;

public class TOVPlayerMove extends AbstractAction {

    private final Vector2D direction;
    public TOVPlayerMove(Vector2D direction){
        this.direction = direction;
    }
    @Override
    public boolean execute(AbstractGameState gs) {
        TOVGameState tovgs = (TOVGameState) gs;
        TOVPlayer player = tovgs.getTOVPlayerByID(tovgs.getCurrentPlayer());

        Vector2D oldPos = player.getPosition();
        tovgs.grid.getElement(oldPos).SetPlayerCount(tovgs.grid.getElement(oldPos).GetPlayerCount() - 1);
        Vector2D newPos = player.Move(direction);
        tovgs.grid.getElement(newPos).SetPlayerCount(tovgs.grid.getElement(newPos).GetPlayerCount() + 1);

        return !oldPos.equals(newPos);
    }

    @Override
    public AbstractAction copy() {
        return new TOVPlayerMove(direction);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TOVPlayerMove that = (TOVPlayerMove) o;
        return Objects.equals(direction, that.direction);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(direction);
    }

    @Override
    public String getString(AbstractGameState gameState) {
        TOVGameState tovgs = (TOVGameState) gameState;
        return "Move " + direction;
    }

    /* getter for the vector */
    public Vector2D getDirection() {
        return direction;
    }
}
