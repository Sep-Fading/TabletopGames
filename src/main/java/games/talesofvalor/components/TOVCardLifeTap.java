package games.talesofvalor.components;

import games.talesofvalor.TOVPlayer;
import games.talesofvalor.utilities.TOVCardTypes;
import games.talesofvalor.utilities.TOVUtilities;

public class TOVCardLifeTap extends TOVCard{
    int damage = 10;
    public TOVCardLifeTap(){
        super("Life Tap", "Deals 10 damage to the caster and draws a card for a target.");
    }

    @Override
    public TOVCard copy() {
        return new TOVCardLifeTap();
    }

    /**
     * Deals 10 damage to the caster and draws a card for a target.
     * @param target the player to draw a card for.
     */
    public void useCard(TOVPlayer caster, TOVPlayer target){
        if (target != null && caster.getHealth() >= 10){
            caster.setHealth(caster.getHealth() - damage);
            TOVUtilities.DrawCard(target);
        }
        else{
            System.out.println("Invalid target. Or Health is too low to use this card.");
        }
    }
}
