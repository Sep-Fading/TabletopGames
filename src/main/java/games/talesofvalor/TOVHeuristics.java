package games.talesofvalor;

import core.AbstractGameState;
import core.interfaces.IStateHeuristic;
import evaluation.optimisation.TunableParameters;
import games.talesofvalor.utilities.TOVUtilities;
import utilities.Vector2D;

public class TOVHeuristics extends TunableParameters implements IStateHeuristic {

    // Tunable weights
    double deathWeight = -0.25;
    double healWeight = 0.001;
    double damageWeight = 0.001;
    double killWeight = 0.075;
    double tankDamageWeight = 0.005;
    double healthPenaltyWeight = 0.5;
    double encounterProgressWeight = 2.0;
    double distToEncounterWeight = -0.01;
    double shrineBonusWeight = 0.5;
    double shrineAvoidPenalty = 0.25;
    double jesterBonusWeight = -0.001;

    public TOVHeuristics() {
        addTunableParameter("deathWeight", deathWeight);
        addTunableParameter("healWeight", healWeight);
        addTunableParameter("damageWeight", damageWeight);
        addTunableParameter("killWeight", killWeight);
        addTunableParameter("tankDamageWeight", tankDamageWeight);
        addTunableParameter("healthPenaltyWeight", healthPenaltyWeight);
        addTunableParameter("encounterProgressWeight", encounterProgressWeight);
        addTunableParameter("distToEncounterWeight", distToEncounterWeight);
        addTunableParameter("shrineBonusWeight", shrineBonusWeight);
        addTunableParameter("shrineAvoidPenalty", shrineAvoidPenalty);
        addTunableParameter("jesterBonusWeight", jesterBonusWeight);
    }

    @Override
    public void _reset() {
        deathWeight = (double) getParameterValue("deathWeight");
        healWeight = (double) getParameterValue("healWeight");
        damageWeight = (double) getParameterValue("damageWeight");
        killWeight = (double) getParameterValue("killWeight");
        tankDamageWeight = (double) getParameterValue("tankDamageWeight");
        healthPenaltyWeight = (double) getParameterValue("healthPenaltyWeight");
        encounterProgressWeight = (double) getParameterValue("encounterProgressWeight");
        distToEncounterWeight = (double) getParameterValue("distToEncounterWeight");
        shrineBonusWeight = (double) getParameterValue("shrineBonusWeight");
        shrineAvoidPenalty = (double) getParameterValue("shrineAvoidPenalty");
        jesterBonusWeight = (double) getParameterValue("jesterBonusWeight");
    }

    @Override
    public double evaluateState(AbstractGameState gs, int playerId) {
        TOVGameState tovgs = (TOVGameState) gs;
        TOVPlayer player = tovgs.getTOVPlayerByID(playerId);
        double score = 0;

        score += player.getHealthHealed() * healWeight;
        score += player.getDamageDealt() * damageWeight;
        score += player.getKillingBlows() * killWeight;
        score += player.getDeathCount() * deathWeight;
        if (player.getPlayerClass() == TOVClasses.TANK) {
            score += player.getDamageTaken() * tankDamageWeight;
        }

        // Low health penalty
        double healthRatio = player.getHealth() / (double) player.getMaxHealth();
        if (healthRatio < 0.5) {
            score -= Math.pow(1 - healthRatio, 2) * healthPenaltyWeight;
        }

        // Encounter progress
        score += (tovgs.totalEncounters - tovgs.encountersRemaining) * encounterProgressWeight;

        // Proximity bonuses
        Vector2D pos = player.getPosition();
        int distToEncounter = TOVUtilities.distanceToNearestEncounter(pos, tovgs.grid);
        int distToShrine = TOVUtilities.distanceToNearestShrine(pos, tovgs.grid);
        int distToJester = TOVUtilities.distanceToNearestJester(pos, tovgs.grid);

        score += distToEncounter * distToEncounterWeight;

        if (healthRatio < 0.6 && distToShrine < 3) {
            score += shrineBonusWeight;
        } else if (distToShrine < 3) {
            score += distToShrine * shrineAvoidPenalty;
        }

        score += distToJester * jesterBonusWeight;

        // Terminal state
        if (tovgs.getAlivePlayers().isEmpty()) {
            score = -1;
        }

        return score;
    }

    @Override
    public TOVHeuristics _copy() {
        TOVHeuristics copy = new TOVHeuristics();
        copy.deathWeight = this.deathWeight;
        copy.healWeight = this.healWeight;
        copy.damageWeight = this.damageWeight;
        copy.killWeight = this.killWeight;
        copy.tankDamageWeight = this.tankDamageWeight;
        copy.healthPenaltyWeight = this.healthPenaltyWeight;
        copy.encounterProgressWeight = this.encounterProgressWeight;
        copy.distToEncounterWeight = this.distToEncounterWeight;
        copy.shrineBonusWeight = this.shrineBonusWeight;
        copy.shrineAvoidPenalty = this.shrineAvoidPenalty;
        copy.jesterBonusWeight = this.jesterBonusWeight;
        return copy;
    }

    @Override
    public boolean _equals(Object o) {
        if (o instanceof TOVHeuristics other) {
            return this.deathWeight == other.deathWeight &&
                    this.healWeight == other.healWeight &&
                    this.damageWeight == other.damageWeight &&
                    this.killWeight == other.killWeight &&
                    this.tankDamageWeight == other.tankDamageWeight &&
                    this.healthPenaltyWeight == other.healthPenaltyWeight &&
                    this.encounterProgressWeight == other.encounterProgressWeight &&
                    this.distToEncounterWeight == other.distToEncounterWeight &&
                    this.shrineBonusWeight == other.shrineBonusWeight &&
                    this.shrineAvoidPenalty == other.shrineAvoidPenalty &&
                    this.jesterBonusWeight == other.jesterBonusWeight;
        }
        return false;
    }

    @Override
    public TOVHeuristics instantiate() {
        return this._copy();
    }
}
