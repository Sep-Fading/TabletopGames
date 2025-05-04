package games.talesofvalor.components;

import games.talesofvalor.TOVPlayer;
import games.talesofvalor.utilities.TOVCardTypes;

public class TOVCardEmpower extends TOVCard{
    public TOVCardEmpower() {
        super("Empower", "Increases the caster's damage by 25% for the next turn.");
    }

    @Override
    public TOVCard copy() {
        return new TOVCardEmpower();
    }

    public void useCard(TOVPlayer target){
        if (!target.isDead()){
            target.setEmpowered(true);
        }
    }
}
