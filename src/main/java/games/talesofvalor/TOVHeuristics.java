package games.talesofvalor;

import core.AbstractGameState;
import core.interfaces.IStateHeuristic;
import games.talesofvalor.components.TOVCell;

import java.util.ArrayList;

public class TOVHeuristics implements IStateHeuristic {
    @Override
    public double evaluateState(AbstractGameState gs, int playerId) {
        TOVGameState tovgs = (TOVGameState) gs;
        TOVPlayer player = tovgs.getTOVPlayerByID(playerId);
        double score = 0;
        double distanceScore = 0;

        // A loss of 0.1 points per death.
        score -= (player.getDeathCount() * 0.1);

        // 0.001 points per point of healing.
        score += (player.getHealthHealed() * 0.001);

        // 0.001 points per point of damage dealt.
        score += (player.getDamageDealt() * 0.001);

        // 0.075 points per killing blow.
        score += (player.getKillingBlows() * 0.075);

        /* 0.0001 points for getting closer to an encounter.
        double minDistanceToEncounter = Double.MAX_VALUE;
        for (int i = 0; i < tovgs.grid.getHeight(); i++){
            for (int j = 0; j < tovgs.grid.getWidth(); j++){
                TOVCell cell = tovgs.grid.getElement(j, i);
                if (cell.hasEncounter){
                    double distance = Math.sqrt(Math.pow(player.getPosition().getX() - j, 2)
                            + Math.pow(player.getPosition().getY() - i, 2));
                    if (distance <= minDistanceToEncounter) {
                        minDistanceToEncounter = distance;
                    }
                }
            }
        }
        if (minDistanceToEncounter != Double.MAX_VALUE){
            if (minDistanceToEncounter == 0){
                minDistanceToEncounter = 0.0000001;
                score += 1;
            }
            distanceScore = (1 / minDistanceToEncounter) * 0.0001;
        } */

        // If all dead, game is lost.
        ArrayList<TOVPlayer> alivePlayers = tovgs.getAlivePlayers();
        if (alivePlayers.isEmpty()){
            score = -1;
        }

        score += distanceScore;
        System.out.println("Player " + playerId + " has a score of " + score);
        return score;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
