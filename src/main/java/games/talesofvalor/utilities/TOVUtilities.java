package games.talesofvalor.utilities;

import games.talesofvalor.TOVClasses;
import games.talesofvalor.TOVPlayer;
import games.talesofvalor.components.*;

import java.util.ArrayList;

public class TOVUtilities {

    /**
     * Draws a random card for the player.
     * @param player the player to draw a card for.
     */
    public static void DrawCard(TOVPlayer player){
        TOVClasses playerClass = player.getPlayerClass();

        // Healer Deck
        ArrayList<TOVCardTypes> HealerDeck = new ArrayList<>();
        HealerDeck.add(TOVCardTypes.HEAL);
        HealerDeck.add(TOVCardTypes.LIFETAP);

        // Tank Deck
        ArrayList<TOVCardTypes> TankDeck = new ArrayList<>();
        TankDeck.add(TOVCardTypes.TAUNT);
        TankDeck.add(TOVCardTypes.DAZZLE);

        // DPS Deck
        ArrayList<TOVCardTypes> DPSDeck = new ArrayList<>();
        DPSDeck.add(TOVCardTypes.CLEAVE);
        DPSDeck.add(TOVCardTypes.EMPOWER);

        switch (playerClass){
            case HEALER:
                player.drawCard(HealerDeck.get((int)(Math.random() * HealerDeck.size())));
                break;
            case TANK:
                player.drawCard(TankDeck.get((int)(Math.random() * TankDeck.size())));
                break;
            case DPS:
                player.drawCard(DPSDeck.get((int)(Math.random() * DPSDeck.size())));
                break;
        }
    }
}
