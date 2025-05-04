package games.talesofvalor.components;

import games.talesofvalor.TOVPlayer;
import games.talesofvalor.utilities.TOVCardTypes;

public class TOVCardTaunt extends TOVCard{
    public TOVCardTaunt(){
        super("Taunt", "Forces the target enemy to attack the caster.");
    }

    @Override
    public TOVCard copy() {
        return new TOVCardTaunt();
    }

    public void useCard(TOVPlayer caster, TOVEnemy target){
        if (target != null){
            target.SetTauntedBy(caster);
        }
        else{
            System.out.println("Invalid target.");
        }
    }
}
