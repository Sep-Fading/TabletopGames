package games.talesofvalor;

import games.talesofvalor.components.TOVEnemy;

import java.util.Objects;

/**
 * Wrapper class for the player and their initiative.
 * This is used to hold on to the initiative rolls of players and enemies
 * during combat, and set up a turn order for them.
 */
public class TOVOrderWrapper {
    private int initiative;
    public TOVPlayer player;
    public TOVEnemy enemy;

    /**
     * Constructor used for players
     * @param initiative - The initiative roll of the player.
     * @param player - The player.
     */
    public TOVOrderWrapper(int initiative, TOVPlayer player){
        this.initiative = initiative;
        this.player = player;
    }

    /**
     * Constructor used for enemies
     * @param initiative - The initiative roll of the enemy.
     * @param enemy - The enemy.
     */
    public TOVOrderWrapper(int initiative, TOVEnemy enemy){
        this.initiative = initiative;
        this.enemy = enemy;
    }

    public int getInitiative(){
        return initiative;
    }

    public TOVPlayer getPlayer(){
        return player;
    }

    public TOVEnemy getEnemy(){
        return enemy;
    }

    public boolean isPlayer(){
        return player != null;
    }

    public boolean isEnemy(){
        return enemy != null;
    }

    public TOVOrderWrapper copy(){
        if (isPlayer()){
            return new TOVOrderWrapper(initiative, player);
        } else {
            return new TOVOrderWrapper(initiative, enemy);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TOVOrderWrapper that = (TOVOrderWrapper) o;
        return getInitiative() == that.getInitiative() && Objects.equals(isPlayer(), that.isPlayer()) && Objects.equals(isEnemy(), that.isEnemy()
        && Objects.equals(player, that.player) && Objects.equals(enemy, that.enemy));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInitiative(), isPlayer(), isEnemy(), player, enemy);
    }
}
