package games.talesofvalor;

import core.AbstractGameState;
import core.AbstractPlayer;
import core.actions.AbstractAction;
import players.PlayerParameters;
import utilities.Vector2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class TOVPlayer{
    Vector2D position;
    private TOVClasses playerClass; // TODO: Implement parameters for this and make it final.
    private int health = 1000;
    private int dexterity = 0;
    private int damage = 2;
    private int id;
    private boolean isDead = false;
    ArrayList<TOVCard> hand = new ArrayList<TOVCard>();

    public TOVPlayer(int id){
        this.id = id;
        position = new Vector2D(0,0);

        // Adjust stats as you like here:
        // TODO - Card sets, more stats + special ability.
        if (playerClass != null) {
            switch (playerClass) {
                case DPS:
                    dexterity = 6;
                    break;
                case TANK:
                    dexterity = 4;
                    break;
                case HEALER:
                    dexterity = 2;
                    break;
            }
        }
    }

    public TOVPlayer(int id, Vector2D position, TOVClasses playerClass, int health, int dexterity, int damage){
        this.id = id;
        this.position = position;
        this.playerClass = playerClass;
        this.health = health;
        this.dexterity = dexterity;
        this.damage = damage;
    }

    /**
     * Changes the position of the player by a given vector.
     * @param direction - The direction to move the player.
     * @return The new position of the player.
     */
    public Vector2D Move(Vector2D direction){
        Vector2D newPosition = position.add(direction);
        if (newPosition.getX() >= 0 && newPosition.getX() < 12 &&
                newPosition.getY() >= 0 && newPosition.getY() < 12){
            position = newPosition;
        }
        return position;
    }

    /**
     * Moves the player to a given cell.
     * @param cell - The cell to move the player to.
     */
    public void MoveToCell(Vector2D cell){
        position = cell;
    }


    /**
     * Attacks a given enemy with the player's damage.
     * @param enemy
     */
    public void Attack(TOVEnemy enemy){
        if (enemy != null){
            enemy.takeDamage(damage);
        }
        else{
            System.out.println("No enemy to attack. (Passed argument was null)");
        }
    }


    /**
     * Creates a hard copy of this player.
     * @return copy of this player.
     */
    public TOVPlayer copy(){
        // TODO: ENSURE INCLUSION OF PLAYER CLASS AFTER IT IS IMPLEMENTED.
        TOVPlayer copy = new TOVPlayer(getPlayerID(),
                position.copy(), playerClass, health, dexterity, damage);
        for (TOVCard card : hand){
            copy.hand.add(card.copy());
        }
        return copy;
    }


    /* Getters */
    public Vector2D getPosition() {
        return position;
    }

    public int getPlayerID (){
        return id;
    }

    public int getDexterity() {
        return dexterity;
    }

    public int getHealth() {
        return health;
    }
    public boolean isDead() {
        return isDead;
    }

    /* Setters */
    public void setHealth(int newHealth) {
        health = newHealth;
    }
    public void setDead(boolean dead) {
        isDead = dead;
    }
}
