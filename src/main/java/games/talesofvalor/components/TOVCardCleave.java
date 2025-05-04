package games.talesofvalor.components;

import games.talesofvalor.TOVPlayer;
import games.talesofvalor.utilities.TOVCardTypes;

import java.util.ArrayList;

public class TOVCardCleave extends TOVCard{
    public TOVCardCleave(){
        super("Cleave", "Deals 100% attack power to main target and " +
                "(1.5*strength) to all other enemies.");
    }


    @Override
    public TOVCard copy() {
        return new TOVCardCleave();
    }

    public void useCard(TOVPlayer caster, TOVEnemy primary, ArrayList<TOVEnemy> secondary){
        if (primary != null && !primary.isDead()){
            primary.setHealth(primary.getHealth() - caster.getDamage());

            for (TOVEnemy enemy : secondary){
                if (!enemy.isDead()){
                    enemy.setHealth(enemy.getHealth() - (int)(1.5 * caster.getStrength()));
                }
            }
        }
        else{
            System.out.println("Invalid target.");
        }
    }
}
