package games.talesofvalor.utilities;

import core.components.GridBoard;
import games.talesofvalor.TOVClasses;
import games.talesofvalor.TOVPlayer;
import games.talesofvalor.components.*;
import utilities.Vector2D;

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

    /**
     * Applies the effect of a shrine to the player.
     * The effect is to heal all players for 75% of their missing health.
     * @param players the list of players to apply the effect to.
     */
    public static void ApplyShrineEffect(ArrayList<TOVPlayer> players) {
        for (TOVPlayer player : players) {
            if (player.getHealth() < player.getMaxHealth()) {
                int healAmount = (int) ((player.getMaxHealth()-player.getHealth()) * 0.75);
                player.setHealth(player.getHealth() + healAmount);
            }
        }
    }

    /**
     * Calculates the distance to the nearest encounter from a given position.
     * @param pos the position to calculate the distance from.
     * @param grid the grid to search for encounters.
     * @return the distance to the nearest encounter.
     */
    public static int distanceToNearestEncounter(Vector2D pos, GridBoard<TOVCell> grid) {
        int minDist = Integer.MAX_VALUE;
        boolean foundAtLeastOne = false;
        for (int i = 0; i < grid.getWidth(); i++) {
            for (int j = 0; j < grid.getHeight(); j++) {
                TOVCell cell = grid.getElement(i, j);
                if (cell.hasEncounter) {
                    foundAtLeastOne = true;
                    int dist = Math.abs(pos.getX() - i) + Math.abs(pos.getY() - j);
                    if (dist < minDist) minDist = dist;
                }
            }
        }
        if (!foundAtLeastOne) {
            return 0;
        }

        return minDist;
    }

    /**
     * Calculates the distance to the nearest jester from a given position.
     * @param pos the position to calculate the distance from.
     * @param grid the grid to search for jesters.
     * @return the distance to the nearest jester.
     */
    public static int distanceToNearestShrine(Vector2D pos, GridBoard<TOVCell> grid) {
        int minDist = Integer.MAX_VALUE;
        boolean foundAtLeastOne = false;
        for (int i = 0; i < grid.getWidth(); i++) {
            for (int j = 0; j < grid.getHeight(); j++) {
                TOVCell cell = grid.getElement(i, j);
                if (cell.hasShrine) {
                    foundAtLeastOne = true;
                    int dist = Math.abs(pos.getX() - i) + Math.abs(pos.getY() - j);
                    if (dist < minDist) minDist = dist;
                }
            }
        }
        if (!foundAtLeastOne) {
            return 0;
        }
        return minDist;
    }

    /**
     * Calculates the distance to the nearest jester from a given position.
     * @param pos the position to calculate the distance from.
     * @param grid the grid to search for jesters.
     * @return the distance to the nearest jester.
     */
    public static int distanceToNearestJester(Vector2D pos, GridBoard<TOVCell> grid) {
        int minDist = Integer.MAX_VALUE;
        boolean foundAtLeastOne = false;
        for (int i = 0; i < grid.getWidth(); i++) {
            for (int j = 0; j < grid.getHeight(); j++) {
                TOVCell cell = grid.getElement(i, j);
                if (cell.hasJester) {
                    foundAtLeastOne = true;
                    int dist = Math.abs(pos.getX() - i) + Math.abs(pos.getY() - j);
                    if (dist < minDist) minDist = dist;
                }
            }
        }
        if (!foundAtLeastOne) {
            return 0;
        }
        return minDist;
    }

}
