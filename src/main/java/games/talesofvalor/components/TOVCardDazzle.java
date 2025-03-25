package games.talesofvalor.components;

import games.talesofvalor.utilities.TOVCardTypes;

public class TOVCardDazzle extends TOVCard{
    public TOVCardDazzle(){
        super("Dazzle", "Stuns the target for 1 turn.");
    }

    @Override
    public TOVCard copy() {
        return new TOVCardDazzle();
    }

    public void useCard(TOVEnemy target){
        if (target != null && target.getHealth() > 0){
            target.setStunned(true);
        }
        else{
            System.out.println("Invalid target.");
        }
    }
}
