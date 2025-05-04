package games.talesofvalor.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import games.talesofvalor.TOVGameState;
import games.talesofvalor.TOVPlayer;
import games.talesofvalor.components.*;
import games.talesofvalor.utilities.TOVEvaluation;


import java.util.ArrayList;
import java.util.Objects;

public class TOVPlayerUseCard extends AbstractAction {

    final int cardIndex;
    int playerID;
    int enemyCompID;
    ArrayList<Integer> secondaryEnemyCompIDs;
    String cardName;

    // Constructors that fit the different types of cards that can be used.
    public TOVPlayerUseCard(int cardIndex){
        this.cardIndex = cardIndex;
    }

    public TOVPlayerUseCard(int cardIndex, int targetID, boolean isPlayer){
        this.cardIndex = cardIndex;
        if (isPlayer){
            this.playerID = targetID;
        }
        else{
            this.enemyCompID = targetID;
        }
    }

    public TOVPlayerUseCard (int cardIndex, int primaryEnemyCompID, ArrayList<Integer> SecondayEnemyCompIDs){
        this.cardIndex = cardIndex;
        this.enemyCompID = primaryEnemyCompID;
        this.secondaryEnemyCompIDs = SecondayEnemyCompIDs;
    }

    // Copy constructor
    public TOVPlayerUseCard(int cardIndex, int playerID, int enemyCompID, ArrayList<Integer> secondaryEnemyCompIDs){
        this.cardIndex = cardIndex;
        this.playerID = playerID;
        this.enemyCompID = enemyCompID;
        this.secondaryEnemyCompIDs = secondaryEnemyCompIDs;
    }


    @Override
    public boolean execute(AbstractGameState gs) {
        TOVGameState tovgs = (TOVGameState) gs;
        TOVPlayer caster = tovgs.getTOVPlayerByID(tovgs.getCurrentPlayer());
        TOVCard card;
        try{
            card = caster.getHand().get(cardIndex);
        }
        catch (IndexOutOfBoundsException e){
            System.out.println("Card index out of bounds: " + cardIndex);
            return false;
        }

        cardName = caster.getHand().get(cardIndex).getName();

        boolean success = false;

        if (card instanceof TOVCardHeal) {
            TOVPlayer target = tovgs.getTOVPlayerByID(playerID);
            if (target != null && target.getHealth() > 0 && target.getHealth() < target.getMaxHealth()) {
                card.useCard(caster, target);
                success = true;
            }
        } else if (card instanceof TOVCardLifeTap) {
            TOVPlayer target = tovgs.getTOVPlayerByID(playerID);
            if (target != null && caster.getHealth() >= 10) {
                card.useCard(caster, target);
                success = true;
            }
        } else if (card instanceof TOVCardTaunt) {
            TOVEnemy target = (TOVEnemy) tovgs.getComponentById(enemyCompID);
            if (target != null && !target.isDead()) {
                card.useCard(caster, target);
                success = true;
            }
        } else if (card instanceof TOVCardDazzle) {
            TOVEnemy target = (TOVEnemy) tovgs.getComponentById(enemyCompID);
            if (target != null && !target.isDead()) {
                card.useCard(target);
                success = true;
            }
        } else if (card instanceof TOVCardCleave) {
            TOVEnemy primaryTarget = (TOVEnemy) tovgs.getComponentById(enemyCompID);
            ArrayList<TOVEnemy> secondaryTargets = new ArrayList<>();

            if (secondaryEnemyCompIDs != null) {
                for (int id : secondaryEnemyCompIDs) {
                    TOVEnemy sec = (TOVEnemy) tovgs.getComponentById(id);
                    if (sec != null && !sec.isDead()) secondaryTargets.add(sec);
                }
            }
            if (primaryTarget != null && !primaryTarget.isDead()) {
                card.useCard(caster, primaryTarget, secondaryTargets);
                success = true;
            }
        } else if (card instanceof TOVCardEmpower) {
            if (caster != null && caster.getHealth() > 0) {
                card.useCard(caster);
                success = true;
            }
        }

        // If card was used, remove from hand
        if (success) {
            caster.getHand().remove(card);
        } else {
            System.out.println("Invalid use of card: " + card.getName());
        }

        return success;
    }


    @Override
    public AbstractAction copy() {
        return new TOVPlayerUseCard(cardIndex, playerID, enemyCompID, secondaryEnemyCompIDs);
    }

    @Override
    public boolean equals(Object obj) {
        if (secondaryEnemyCompIDs == null) {
            return obj instanceof TOVPlayerUseCard && ((TOVPlayerUseCard) obj).cardIndex == cardIndex &&
                    ((TOVPlayerUseCard) obj).playerID == playerID && ((TOVPlayerUseCard) obj).enemyCompID == enemyCompID;
        }
        return obj instanceof TOVPlayerUseCard && ((TOVPlayerUseCard) obj).cardIndex == cardIndex &&
                ((TOVPlayerUseCard) obj).playerID == playerID && ((TOVPlayerUseCard) obj).enemyCompID == enemyCompID &&
                ((TOVPlayerUseCard) obj).secondaryEnemyCompIDs.equals(secondaryEnemyCompIDs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardIndex, playerID, enemyCompID, secondaryEnemyCompIDs);
    }

    @Override
    public String getString(AbstractGameState gameState) {
        return "Played the card: " + cardName;
    }
}
