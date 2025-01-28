package games.talesofvalor;

import core.components.Card;

// A very basic implementation of the card for
// encounter combats, to be fleshed out later with unique capabilities.
public class TOVCard extends Card {
    int attack;
    int armor;
    int health;

    public TOVCard(String name, int attack, int armor, int health) {
        super(name);
        this.attack = attack;
        this.armor = armor;
        this.health = health;
    }

    public TOVCard copy() {
        return new TOVCard(componentName, attack, armor, health);
    }

    // Placeholder for attacks
    // Needs Encounter to contain Enemies. But Perhaps this should be an action?
    public void useCard(TOVEnemy enemy){
        enemy.takeDamage(attack);
    }
}
