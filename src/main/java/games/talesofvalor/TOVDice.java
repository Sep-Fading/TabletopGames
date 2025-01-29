package games.talesofvalor;

import core.components.Dice;

import java.util.Random;

public class TOVDice extends Dice {
    public TOVDice(int faces) {
        super(faces);
    }

    /**
     * Alternative constructor for the TOVDice class.
     * Used primarily in the copy method to instantiate hard copies with ease.
     * @param faces - The number of faces on the dice.
     * @param id - The id of the dice.
     */
    public TOVDice(int faces, int id) {
        super(faces);
    }

    /**
     * Rolls the dice and adds a modifier to the result.
     * @param modifier
     */
    public void Roll(int modifier){
        Random rand = new Random();
        roll(rand);
        setValue(getValue() + modifier);
    }


    @Override
    public TOVDice copy() {
        TOVDice copyDie = new TOVDice(nSides, componentID);
        copyDie.setValue(getValue());
        return copyDie;
    }

}
