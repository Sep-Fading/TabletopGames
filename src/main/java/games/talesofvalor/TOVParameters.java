package games.talesofvalor;

import core.AbstractParameters;
import core.AbstractPlayer;
import core.Game;
import games.GameType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TOVParameters extends AbstractParameters {
    // Grid dimensions.
    static int gridWidth = 10;
    static int gridHeight = 10;
    public static final int maxEncounters = ((gridHeight * gridWidth) / 100) * 20;
    public static final int maxShrines = ((gridHeight * gridWidth) / 100) * 10;
    public static final int maxJesters = ((gridHeight * gridWidth) / 100) * 8;

    public TOVParameters() {
    }

    @Override
    protected AbstractParameters _copy() {
        TOVParameters copy = new TOVParameters();
        copy.gridWidth = gridWidth;
        copy.gridHeight = gridHeight;
        return copy;
    }

    @Override
    protected boolean _equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TOVParameters that = (TOVParameters) o;
        return gridWidth == that.gridWidth && gridHeight == that.gridHeight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), gridWidth, gridHeight);
    }
}
