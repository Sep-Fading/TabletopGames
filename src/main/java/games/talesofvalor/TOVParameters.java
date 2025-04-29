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
    int gridWidth = 10;
    int gridHeight = 10;

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
