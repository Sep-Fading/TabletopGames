package games.talesofvalor;

import core.AbstractGameState;
import core.CoreConstants;
import core.StandardForwardModel;
import core.actions.AbstractAction;
import core.components.GridBoard;
import games.talesofvalor.actions.TOVPlayerAttack;
import games.talesofvalor.actions.TOVPlayerMove;
import games.talesofvalor.actions.TOVPlayerUseCard;
import games.talesofvalor.components.*;
import games.talesofvalor.utilities.TOVUtilities;
import utilities.Vector2D;

import java.util.*;

public class TOVForwardModel extends StandardForwardModel {
    @Override
    protected void _setup(AbstractGameState firstState) {
        TOVGameState tovgs = (TOVGameState) firstState;
        TOVParameters tovp = (TOVParameters) tovgs.getGameParameters();

        // Create the map, counting the encounters as we go.
        tovgs.totalEncounters = 0;
        tovgs.grid = new GridBoard<>(tovp.gridWidth, tovp.gridHeight);
        for (int i = 0; i < tovp.gridHeight; i++) {
            for (int j = 0; j < tovp.gridWidth; j++) {
                tovgs.grid.setElement(j, i, new TOVCell(j, i));
                if (tovgs.grid.getElement(j, i).hasEncounter) {
                    tovgs.totalEncounters++;
                }
            }
        }
        tovgs.encountersRemaining = tovgs.totalEncounters;

        // Create & place TOVPlayer instances for each player.
        tovgs.players.clear();
        TOVClasses[] classes = TOVClasses.values();
        for (int i = 0; i < tovgs.getNPlayers(); i++) {
            TOVPlayer player = new TOVPlayer(i, classes[i]);
            TOVUtilities.DrawCard(player);
            TOVUtilities.DrawCard(player);
            tovgs.players.add(player);
            tovgs.grid.getElement(0, 0).SetPlayerCount(
                    tovgs.grid.getElement(0, 0).GetPlayerCount() + 1);
        }

        // Initialize the roundType.
        tovgs.setRoundType(TOVRoundTypes.OUT_OF_COMBAT);
        tovgs.setPreviousRoundType(TOVRoundTypes.OUT_OF_COMBAT);
    }

    @Override
    protected List<AbstractAction> _computeAvailableActions(AbstractGameState gameState) {
        List<AbstractAction> actions = new ArrayList<>();
        TOVGameState tovgs = (TOVGameState) gameState;
        TOVPlayer currentPlayer = tovgs.getTOVPlayerByID(tovgs.getCurrentPlayer());
        Vector2D currentPosition = currentPlayer.getPosition();
        int x = currentPosition.getX();
        int y = currentPosition.getY();

        //System.out.println("Current round type: " + tovgs.getRoundType());

        // If out of combat, roll a d6 and calculate all reachable cells.
        // else if in combat, add possible combat actions to the list.
        if (tovgs.getRoundType() == TOVRoundTypes.OUT_OF_COMBAT) {

            int d6Roll = tovgs.d6f.getFinalVal();
            System.out.println("Player " + tovgs.getCurrentPlayer() + " rolled a " + d6Roll);

            // A BFS implementation to calculate all reachable cells to multi-directional movement.
            boolean[][] visited = new boolean[tovgs.grid.getWidth()][tovgs.grid.getHeight()];
            int[][] distance = new int[tovgs.grid.getWidth()][tovgs.grid.getHeight()];

            // Initialise all possible movements.
            for (int i = 0; i < tovgs.grid.getWidth(); i++) {
                for (int j = 0; j < tovgs.grid.getHeight(); j++) {
                    visited[i][j] = false;
                    distance[i][j] = Integer.MAX_VALUE;
                }
            }

            Queue<Vector2D> moveQueue = new LinkedList<>();
            // Starting point
            moveQueue.add(new Vector2D(x, y));
            visited[x][y] = true;
            distance[x][y] = 0;

            // BFS on the grid.
            while (!moveQueue.isEmpty()){
                Vector2D current = moveQueue.poll();
                int currentX = current.getX();
                int currentY = current.getY();
                int dist = distance[currentX][currentY];

                // Move to neighbours if distance is within movement budget.
                if (dist < d6Roll){
                    for (Vector2D dir : new Vector2D[]{
                        new Vector2D(1,0), new Vector2D(-1,0),
                        new Vector2D(0,1), new Vector2D(0,-1)
                    }){
                        int newX = currentX + dir.getX();
                        int newY = currentY + dir.getY();
                        if (newX >= 0 && newX < tovgs.grid.getWidth() &&
                                newY >= 0 && newY < tovgs.grid.getHeight() &&
                                !visited[newX][newY]){
                            visited[newX][newY] = true;
                            distance[newX][newY] = dist + 1;
                            moveQueue.add(new Vector2D(newX, newY));
                        }
                    }
                }
            }

            // Creating an action for all reachable cells.
            for (int i = 0; i < tovgs.grid.getWidth(); i++) {
                for (int j = 0; j < tovgs.grid.getHeight(); j++) {
                    if (visited[i][j]) {
                        int dist = distance[i][j];
                        if (dist <= d6Roll){
                            if (dist > 0){
                                int dx = i - x;
                                int dy = j - y;
                                actions.add(new TOVPlayerMove(new Vector2D(dx, dy)));
                            }
                        }
                    }
                }
            }
        }
        else if (tovgs.getRoundType() == TOVRoundTypes.IN_COMBAT && tovgs.grid.getElement(x, y).hasEncounter){
            System.out.println(tovgs.grid.getElement(x, y).hasEncounter);
            // Default attack options
            for (TOVEnemy target : tovgs.grid.getElement(x, y).encounter.enemies){
                System.out.println("COMPONENT ID: " + target.getComponentID());
                System.out.println("TARGET SET TO COMPONENT ID: " + target.getComponentID() + " " + target.isDead());
                actions.add(new TOVPlayerAttack(target.getComponentID()));
            }
            // TODO:Playing cards if the player has any.
            if (!currentPlayer.getHand().isEmpty()){
                for (int i = 0; i < currentPlayer.getHand().size(); i++){
                    TOVCard card = currentPlayer.getHand().get(i);

                    if (card instanceof TOVCardCleave){
                        for (TOVEnemy target : tovgs.grid.getElement(x, y).encounter.enemies){
                            if (!target.isDead()){
                                ArrayList<Integer> secondaryTargets = new ArrayList<>();
                                for (TOVEnemy secondaryTarget : tovgs.grid.getElement(x, y).encounter.enemies){
                                    if (!secondaryTarget.isDead() && secondaryTarget != target){
                                        secondaryTargets.add(secondaryTarget.getComponentID());
                                    }
                                }
                                actions.add(new TOVPlayerUseCard(i, target.getComponentID(), secondaryTargets));
                            }
                        }
                    }

                    else if (card instanceof TOVCardEmpower) {
                        for (TOVPlayer player : tovgs.players){
                            if (player != currentPlayer){
                                actions.add(new TOVPlayerUseCard(i, player.getPlayerID(), true));
                            }
                        }
                    }

                    else if (card instanceof TOVCardHeal){
                        for (TOVPlayer player : tovgs.players) {
                            if (!player.isDead()){
                                actions.add(new TOVPlayerUseCard(i, player.getPlayerID(), true));
                            }
                        }
                    }

                    else if (card instanceof TOVCardLifeTap){
                        for (TOVPlayer player : tovgs.players) {
                            if (!player.isDead()){
                                actions.add(new TOVPlayerUseCard(i, player.getPlayerID(), true));
                            }
                        }
                    }

                    else if (card instanceof TOVCardTaunt){
                        for (TOVEnemy target : tovgs.grid.getElement(x, y).encounter.enemies){
                            if (!target.isDead()){
                                actions.add(new TOVPlayerUseCard(i, target.getComponentID(), false));
                            }
                        }
                    }

                    else if (card instanceof TOVCardDazzle){
                        for (TOVEnemy target : tovgs.grid.getElement(x, y).encounter.enemies){
                            if (!target.isDead()){
                                actions.add(new TOVPlayerUseCard(i, target.getComponentID(), false));
                            }
                        }
                    }
                }
            }

        }
        else{
            System.out.println("No actions available.");
            actions.add(new TOVPlayerMove(new Vector2D(0, 0)));
        }

        return actions;
    }

    /* --- ROUND START --- */
    /**
     * OUT_OF_COMBAT:
     * 1. Dice roll to determine the current player's movement capabilities. Dexterity affects the roll.
     * ----------------------------------------
     * IN_COMBAT:
     *  1. if it's the first round of combat, calculate the turn order.
     *  2. Move all players into the encounter cell.
     *  3. If the encounter is defeated, update the round type and end the player's turn.
     */
    @Override
    protected void _beforeAction(AbstractGameState gameState, AbstractAction action) {
        TOVGameState tovgs = (TOVGameState) gameState;
        TOVRoundTypes round = tovgs.getRoundType();
        switch (round){
            case OUT_OF_COMBAT:
                tovgs.d6f.Roll(tovgs.getTOVPlayerByID(tovgs.getCurrentPlayer()).getDexterity());
                break;
            case IN_COMBAT:
                // TODO
            default:
                //throw new IllegalStateException("Unexpected value: " + round);
        }
    }

    /* -------------------- */

    /* --- ROUND END --- */
    /**
     * 1. Update roundType in TOVGameState.
     * 2. If in combat, check if encounter is defeated and update state accordingly.
     */
    @Override
    protected void _afterAction(AbstractGameState gameState, AbstractAction action) {
        //System.out.println("After action");
        TOVGameState tovgs = (TOVGameState) gameState;
        TOVCell combatCell = tovgs.grid.getElement(tovgs.getTOVPlayerByID(tovgs.getCurrentPlayer()).getPosition());
        tovgs.setPreviousRoundType(tovgs.getRoundType());
        // Check if the encounter is defeated.
        combatCell.updateHasEncounter();
        tovgs.UpdateRoundType();

        // If in combat, find the next player to act according to the custom turn order.
        // Else, use the regular turn order.
        if (tovgs.getRoundType() == TOVRoundTypes.IN_COMBAT){
            if (tovgs.getPreviousRoundType() == TOVRoundTypes.OUT_OF_COMBAT){
                ArrayList<TOVPlayer> players = tovgs.players;
                ArrayList<TOVEnemy> enemies = tovgs.grid.getElement(
                        tovgs.getTOVPlayerByID(tovgs.getCurrentPlayer()).getPosition()).encounter.enemies;
                tovgs.SetupCombatTurnOrder(players, enemies);
                Vector2D encounterPos = tovgs.getTOVPlayerByID(tovgs.getCurrentPlayer()).getPosition();

                // Move all players to the encounter.
                for (TOVPlayer player : tovgs.players){
                    //System.out.println("Player " + player.getPlayerID() + " same pos as encounter: " +
                    //player.getPosition().equals(encounterPos));
                    //System.out.println("Player " + player.getPlayerID() + " pos: " + player.getPosition());
                    if (!player.getPosition().equals(encounterPos)){
                        tovgs.grid.getElement(player.getPosition()).SetPlayerCount(
                                tovgs.grid.getElement(player.getPosition()).GetPlayerCount() - 1);
                        player.MoveToCell(encounterPos);
                        tovgs.grid.getElement(player.getPosition()).SetPlayerCount(
                                tovgs.grid.getElement(player.getPosition()).GetPlayerCount() + 1);
                        System.out.println("Player " + player.getPlayerID() + " moved to encounter cell." +
                                " Position: " + player.getPosition());
                    }
                }

                tovgs.UpdateRoundType();
            }

            // Turn order
            ArrayList<TOVOrderWrapper> turnOrder = tovgs.getCombatOrder();
            if (turnOrder.isEmpty()){
                tovgs.SetupCombatTurnOrder(tovgs.players, tovgs.grid.getElement(
                        tovgs.getTOVPlayerByID(tovgs.getCurrentPlayer()).getPosition()).encounter.enemies);
            }

            ArrayList<TOVOrderWrapper> completedTurns = new ArrayList<>();
            for (TOVOrderWrapper tovOrderWrapper : turnOrder) {
                if (tovOrderWrapper.isPlayer()) {
                    endPlayerTurn(tovgs, tovOrderWrapper.getPlayer().getPlayerID());
                    completedTurns.add(tovOrderWrapper);
                    //System.out.println("Player " + tovOrderWrapper.getPlayer().getPlayerID() + " turn. In combat.");
                    break;
                }
                else if (tovOrderWrapper.isEnemy()) {
                    ArrayList<TOVPlayer> alivePlayers = tovgs.getAlivePlayers();
                    if (alivePlayers.isEmpty()){
                        endGame(tovgs);
                    }

                    // Check to see if the enemy is stunned.
                    if (tovOrderWrapper.getEnemy().getStunned()){
                        //System.out.println("Stunned enemy.");
                        continue;
                    }

                    // Pick a random player to attack, if any left, if one player alive just pick that player.
                    TOVPlayer target = null;
                    if (alivePlayers.size() == 1){
                        target = alivePlayers.get(0);
                    }
                    else if (alivePlayers.size() > 1){
                        //System.out.println(alivePlayers.size() + " Players left alive");

                            int randomIndex = (int) (Math.random() * alivePlayers.size());
                            //System.out.println("Random index: " + randomIndex);
                            target = alivePlayers.get(randomIndex);

                    }

                    // Check to see if the enemy is taunted.
                    if (tovOrderWrapper.getEnemy().getTauntedBy() != null){

                        //System.out.println("Taunted by player" +
                        //tovOrderWrapper.getEnemy().getTauntedBy().getPlayerID());

                        target = tovOrderWrapper.getEnemy().getTauntedBy();
                    }

                    if (target == null){
                        //System.out.println("No players to attack.");
                        endGame(tovgs);
                    }
                    else {
                        tovOrderWrapper.getEnemy().Attack(target);
                        completedTurns.add(tovOrderWrapper);
                        // Reset the taunt after the enemy has attacked.
                        tovOrderWrapper.getEnemy().SetTauntedBy(null);
                        //System.out.println("Enemy turn.");
                    }
                }
            }
            turnOrder.removeAll(completedTurns);
        }
        else if (tovgs.getRoundType() == TOVRoundTypes.OUT_OF_COMBAT &&
                tovgs.getPreviousRoundType() == TOVRoundTypes.IN_COMBAT){
            tovgs.encountersRemaining --;
            System.out.println("Encounter defeated. Remaining: " + tovgs.encountersRemaining);
            endPlayerTurn(tovgs);
            if (tovgs.encountersRemaining == 0){
                endGame(tovgs);
            }
        }
        else{
            endPlayerTurn(tovgs);
            System.out.println("Player " + tovgs.getCurrentPlayer() + " turn. Out of combat.");
        }
    }

    /* -------------------- */
    @Override
    protected void endGame(AbstractGameState gs) {
        TOVGameState tovgs = (TOVGameState) gs;
        tovgs.setGameStatus(CoreConstants.GameResult.GAME_END);
        for (int p = 0; p < tovgs.getNPlayers(); p++) {
            int o = tovgs.encountersRemaining == 0 && !tovgs.getAlivePlayers().isEmpty() ? 1 : 0;
            if (o == 1)
                tovgs.setPlayerResult(CoreConstants.GameResult.WIN_GAME, p);
            else
                tovgs.setPlayerResult(CoreConstants.GameResult.LOSE_GAME, p);
        }

        System.out.println("Game over : [" + tovgs.getGameStatus() + "]");
        System.out.println("Results : " + Arrays.toString(tovgs.getPlayerResults()));
    }
}
