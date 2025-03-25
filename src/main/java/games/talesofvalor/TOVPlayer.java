package games.talesofvalor;

import games.talesofvalor.components.*;
import games.talesofvalor.utilities.TOVCardTypes;
import utilities.Vector2D;

import java.util.ArrayList;

public class TOVPlayer{
    Vector2D position;
    private final TOVClasses playerClass; // TODO: Implement parameters for this and make it final.
    private int health = 1000;
    private int dexterity = 0;
    private int strength = 0;
    private int intelligence = 0;
    private int damage = 5;
    private final int id;
    private boolean isDead = false;
    private int damageDealt = 0;
    private int healthHealed = 0;
    private int killingBlows = 0;
    private int deathCount = 0;
    private boolean empowered = false;

    ArrayList<TOVCard> hand = new ArrayList<>();

    public TOVPlayer(int id, TOVClasses playerClass){
        this.id = id;
        position = new Vector2D(0,0);
        this.playerClass = playerClass;

        // Adjust stats as you like here:
        // TODO - Card sets, more stats + special ability.
        if (playerClass != null) {
            switch (playerClass) {
                case DPS:
                    dexterity = 3;
                    strength = 2;
                    intelligence = 1;
                    break;
                case TANK:
                    strength = 3;
                    dexterity = 1;
                    intelligence = 1;
                    break;
                case HEALER:
                    intelligence = 3;
                    dexterity = 2;
                    strength = 1;
                    break;
            }
        }
    }

    public TOVPlayer(int id, Vector2D position, TOVClasses playerClass, int health,
                     int strength, int dexterity, int intelligence, int damage,
                     int damageDealt, int healthHealed, int killingBlows, int deathCount,
                     boolean empowered, ArrayList<TOVCard> hand){
        this.id = id;
        this.position = position;
        this.playerClass = playerClass;
        this.health = health;
        this.dexterity = dexterity;
        this.strength = strength;
        this.intelligence = intelligence;
        this.damage = damage;
        this.damageDealt = damageDealt;
        this.healthHealed = healthHealed;
        this.killingBlows = killingBlows;
        this.deathCount = deathCount;
        this.empowered = empowered;
        this.hand = hand;
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
     * @param enemy - The enemy to attack.
     */
    public void Attack(TOVEnemy enemy){
        if (enemy != null){
            if (empowered) {
                enemy.takeDamage((int) Math.round(damage * 1.25));
            }
            else{
                enemy.takeDamage(damage);
            }
            damageDealt += damage;
            if (enemy.isDead()){
                System.out.println("Enemy is killed.");
                killingBlows++;
            }
        }
        else{
            System.out.println("No enemy to attack. (Passed argument was null)");
        }
    }

    /**
     * Adds a given card to the player's hand.
     * Should be used mainly through TOVUtilities.DrawCard().
     * @param card - The card to add to the player's hand.
     */
    public void drawCard(TOVCardTypes card){
        switch(card)
        {
            case CLEAVE:
                hand.add(new TOVCardCleave());
                break;
            case DAZZLE:
                hand.add(new TOVCardDazzle());
                break;
            case LIFETAP:
                hand.add(new TOVCardLifeTap());
                break;
            case TAUNT:
                hand.add(new TOVCardTaunt());
                break;
            case EMPOWER:
                hand.add(new TOVCardEmpower());
                break;
            case HEAL:
                hand.add(new TOVCardHeal());
                break;
        }
    }

    /**
     * Creates a hard copy of this player.
     * @return copy of this player.
     */
    public TOVPlayer copy(){

        ArrayList<TOVCard> handCopy = new ArrayList<>();

        for (TOVCard card : this.hand){
            handCopy.add(card.copy());
        }

        return new TOVPlayer(getPlayerID(),
                position.copy(), playerClass, health, strength, intelligence, dexterity, damage
                , damageDealt, healthHealed, killingBlows, deathCount, empowered, handCopy);
    }


    /* Getters & Setters */
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

    public int getDamageDealt() {
        return damageDealt;
    }

    public int getHealthHealed() {
        return healthHealed;
    }

    public int getDeathCount(){
        return deathCount;
    }

    public int getKillingBlows(){
        return killingBlows;
    }

    public void setHealth(int newHealth) {
        health = newHealth;
    }
    public void setDead(boolean dead) {
        isDead = dead;
    }

    public void setHealthHealed(int healthHealed) {
        this.healthHealed = healthHealed;
    }

    public void setDeathCount(int count){
        deathCount = count;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public int getDamage() {
        return damage;
    }

    public int getStrength() {
        return strength;
    }

    public void setEmpowered(boolean b) {
        empowered = b;
    }

    public ArrayList<TOVCard> getHand() {
        return hand;
    }

    public void setHand(ArrayList<TOVCard> hand) {
        this.hand = hand;
    }

    public TOVClasses getPlayerClass() {
        return playerClass;
    }
}
