package games.talesofvalor.components;

import core.components.Dice;

import java.util.Random;

public class TOVDice extends Dice {
    public TOVDice(Type type) {
        super(type);
    }

    /**
     * Alternative constructor for the TOVDice class.
     * Used primarily in the copy method to instantiate hard copies with ease.
     * @param faces - The number of faces on the dice.
     * @param id - The id of the dice.
     */
    public TOVDice(Type type, int id) {
        super(type);
    }

    /**
     * Rolls the dice and adds a modifier to the result.
     * @param modifier
     */
    public void Roll(int modifier){
        Random rand = new Random();
        roll(rand);
        System.out.println("Rolled a " + getValue());
        setValue(getValue() + modifier);
    }


    @Override
    public TOVDice copy() {
        TOVDice copyDie = new TOVDice(this.type, componentID);
        int val = getValue();
        if (val != 0){
            copyDie.setValue(val);
        }
        else{
            copyDie.setValue(1);
        }
        return copyDie;
    }

}
