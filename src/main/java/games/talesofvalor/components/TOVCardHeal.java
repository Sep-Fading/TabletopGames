package games.talesofvalor.components;

import games.talesofvalor.TOVPlayer;
import games.talesofvalor.utilities.TOVCardTypes;

public class TOVCardHeal extends TOVCard{
    public TOVCardHeal(){
        super("Heal", "Heals a friendly target for 100% of the caster's intelligence.");
    }
    @Override
    public TOVCard copy() {
        return new TOVCardHeal();
    }

    @Override
    public void useCard(TOVPlayer caster, TOVPlayer target) {
        if (target != null && target.getHealth() > 0){
            target.setHealth(target.getHealth() + caster.getIntelligence());
        }
        else{
            System.out.println("Invalid target.");
        }
    }
}
