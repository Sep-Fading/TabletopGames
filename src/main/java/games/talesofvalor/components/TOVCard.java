package games.talesofvalor.components;

import core.components.Card;
import games.talesofvalor.TOVPlayer;
import games.talesofvalor.utilities.TOVCardTypes;

import java.util.ArrayList;

// A very basic implementation of the card for
// encounter combats, to be fleshed out later with unique capabilities.
public abstract class TOVCard extends Card {
    String desc;
    String name;

    public TOVCard(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    /**
     * Creates a hard copy of this card.
     * @return copy of this card.
     */
    public abstract TOVCard copy();

    /**
     * Should remove the card after use.
     */
    public void useCard(){};
    public void useCard(TOVEnemy target){};
    public void useCard(TOVPlayer target){};
    public void useCard(TOVPlayer caster, TOVEnemy primary, ArrayList<TOVEnemy> secondary){};
    public void useCard(TOVPlayer caster, TOVPlayer target){};
    public void useCard(TOVPlayer caster, TOVEnemy target){};

    public String getName() {
        return name;
    }

    public String getDescription() {
        return desc;
    }
}
