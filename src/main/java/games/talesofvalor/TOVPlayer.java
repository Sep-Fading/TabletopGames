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
    int health = 100;
    int id;
    ArrayList<TOVCard> hand = new ArrayList<TOVCard>();

    public TOVPlayer(int id){
        this.id = id;
        position = new Vector2D(0,0);
    }

    public Vector2D Move(Vector2D direction){
        Vector2D newPosition = position.add(direction);
        if (newPosition.getX() >= 0 && newPosition.getX() < 10 &&
                newPosition.getY() >= 0 && newPosition.getY() < 10){
            position = newPosition;
        }
        return position;
    }

    public TOVPlayer copy(){
        TOVPlayer copy = new TOVPlayer(getPlayerID());
        copy.position = position.copy();
        copy.health = health;
        for (TOVCard card : hand){
            copy.hand.add(card.copy());
        }
        return copy;
    }

    public Vector2D getPosition() {
        return position;
    }

    public int getPlayerID(){
        return id;
    }
}
