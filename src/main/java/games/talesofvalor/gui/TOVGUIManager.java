package games.talesofvalor.gui;

import com.google.gson.JsonParseException;
import core.AbstractGameState;
import core.AbstractPlayer;
import core.Game;
import games.talesofvalor.TOVPlayer;
import games.talesofvalor.TOVRoundTypes;
import games.talesofvalor.actions.TOVPlayerAttack;
import games.talesofvalor.actions.TOVPlayerMove;
import games.talesofvalor.actions.TOVPlayerUseCard;
import games.talesofvalor.components.*;
import games.talesofvalor.TOVForwardModel;
import games.talesofvalor.TOVGameState;
import gui.AbstractGUIManager;
import gui.GamePanel;
import gui.IScreenHighlight;
import players.human.ActionController;
import utilities.Vector2D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TOVGUIManager extends AbstractGUIManager {
    // Visual Board
    JPanel gridPanel;

    // Board Settings
    private static final int defaultDisplayWidth = 800;
    private static final int defaultDisplayHeight = 800;
    private static final Color defaultTextColor = Color.WHITE;
    private static final Color defaultEncounterColor = Color.RED;
    private static final Color defaultPlayerColor = Color.BLUE;
    private static final Color defaultEPColor = Color.MAGENTA;

    // View Flag
    private boolean combatView = false;

    // Cards
    private TOVCard selectedCard = null;
    private int selectedCardIndex = -1;
    private JPanel selectedCardPanel = null;

    // Default Attack
    private boolean attackModeActive = false;
    private JButton attackButton; // for UI reference

    public TOVGUIManager(GamePanel parent, Game game, ActionController ac, Set<Integer> human) {
        super(parent, game, ac, human);
        if (game == null) return;

        TOVGameState gameState = (TOVGameState) game.getGameState();

        DrawGrid(gameState, (TOVForwardModel) game.getForwardModel());
        CreateAndAddActionPanel();
        _update(null, gameState);

        //highlightTraversableCells();
        //SwingUtilities.invokeLater(this::highlightTraversableCells);
    }

    // Initial Grid Setup
    private void DrawGrid(TOVGameState tovgs, TOVForwardModel tovfm) {
        // Tooltip configuration (optional styling)
        UIManager.put("ToolTip.background", Color.YELLOW);
        UIManager.put("ToolTip.foreground", Color.BLACK);
        UIManager.put("ToolTip.font", new Font("SansSerif", Font.BOLD, 12));

        // Grid dimensions
        int gridWidth = tovgs.grid.getWidth();
        int gridHeight = tovgs.grid.getHeight();

        // Set layout for the grid
        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(gridHeight, gridWidth));

        // Populate the grid with JLabels
        for (int i = 0; i < gridHeight; i++) {
            for (int j = 0; j < gridWidth; j++) {
                TOVCell cell = tovgs.grid.getElement(j, i);
                JLabel cellLabel = new JLabel("", SwingConstants.CENTER);

                if (cell.hasEncounter) {
                    cellLabel.setText(String.valueOf(cell.GetEncounter().enemies.size())); // Display number of enemies
                    cellLabel.setOpaque(true);
                    cellLabel.setBackground(defaultEncounterColor);
                    cellLabel.setForeground(defaultTextColor);

                    // Generate tooltip text with detailed enemy info
                    StringBuilder tooltipText = new StringBuilder("<html>"); // HTML allows multiline tooltips
                    tooltipText.append("Encounter!<br>");
                    List<TOVEnemy> enemies = cell.GetEncounter().enemies; // Assuming `enemies` is a List<Enemy>
                    for (int k = 0; k < enemies.size(); k++) {
                        TOVEnemy enemy = enemies.get(k);
                        tooltipText.append("Enemy ").append(k + 1).append(":<br>");
                        tooltipText.append("&nbsp;&nbsp;Health: ").append(enemy.getAttack()).append("<br>");
                        tooltipText.append("&nbsp;&nbsp;Attack: ").append(enemy.getHealth()).append("<br>");
                    }
                    tooltipText.append("</html>");
                    cellLabel.setToolTipText(tooltipText.toString());
                } else {
                    cellLabel.setOpaque(true);
                    cellLabel.setBackground(Color.LIGHT_GRAY);
                    cellLabel.setToolTipText("No encounter in this cell.");
                }

                // Add border for visual clarity
                cellLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                gridPanel.add(cellLabel);
            }
        }

        // Add the grid panel to the frame and make it visible
        parent.setLayout(new BorderLayout());
        parent.add(gridPanel, BorderLayout.CENTER);
        parent.revalidate();
        parent.repaint();
    }

    private void enableCellClicking() {
        TOVGameState tovgs = (TOVGameState) game.getGameState();
        for (int i = 0; i < tovgs.grid.getHeight(); i++) {
            for (int j = 0; j < tovgs.grid.getWidth(); j++) {
                final int x = j;
                final int y = i;

                JLabel cellLabel = (JLabel) gridPanel.getComponent(i * tovgs.grid.getWidth() + j);

                // Remove any old listeners
                for (MouseListener ml : cellLabel.getMouseListeners()) {
                    cellLabel.removeMouseListener(ml);
                }

                cellLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        handleCellClick(x, y);
                    }
                });
            }
        }
    }

    private void disableCellClicking() {
        TOVGameState tovgs = (TOVGameState) game.getGameState();
        for (int i = 0; i < tovgs.grid.getHeight(); i++) {
            for (int j = 0; j < tovgs.grid.getWidth(); j++) {
                JLabel cellLabel = (JLabel) gridPanel.getComponent(i * tovgs.grid.getWidth() + j);

                for (MouseListener ml : cellLabel.getMouseListeners()) {
                    cellLabel.removeMouseListener(ml);
                }
            }
        }
    }

    private void handleCellClick(int x, int y) {
        TOVGameState tovgs = (TOVGameState) game.getGameState();
        TOVPlayer currentPlayer = tovgs.getTOVPlayerByID(tovgs.getCurrentPlayer());

        Vector2D playerPos = currentPlayer.getPosition();
        Vector2D direction = new Vector2D(x - playerPos.getX(), y - playerPos.getY());

        int budget = tovgs.d6f.getFinalVal();
        double distance = Math.abs(direction.getX()) + Math.abs(direction.getY());

        if (distance <= budget){
            TOVPlayerMove moveAction = new TOVPlayerMove(direction);
            game.getForwardModel().next(tovgs, moveAction);
            _update(null, tovgs);
        }
        else{
            System.out.println("Invalid move.");
        }
    }

    @Override
    protected void _update(AbstractPlayer player, AbstractGameState gameState) {
        TOVGameState tovgs = (TOVGameState) gameState;

        if (tovgs.getRoundType() == TOVRoundTypes.IN_COMBAT){
            if (!combatView) {
                combatView = true;
                switchToCombatView();
            }
        }
        else {
            if (combatView) {
                switchToGridView();
                combatView = false;
            }
            // Update the grid with the new game state
            for (int i = 0; i < tovgs.grid.getHeight(); i++) {
                for (int j = 0; j < tovgs.grid.getWidth(); j++) {
                    TOVCell cell = tovgs.grid.getElement(j, i);
                    JLabel cellLabel = (JLabel) gridPanel.getComponent(i * tovgs.grid.getWidth() + j);

                    StringBuilder labelText = new StringBuilder();
                    Color backgroundColor = Color.LIGHT_GRAY;

                    // Build out encounter info and color the cell Red
                    // If the cell has an encounter.
                    if (cell.hasEncounter) {
                        labelText.append(cell.GetEncounter().getEnemyCount()).append("E");
                        backgroundColor = defaultEncounterColor;

                        StringBuilder tooltipText = new StringBuilder("<html>Encounter:<br>");
                        for (int k = 0; k < cell.GetEncounter().enemies.size(); k++) {
                            TOVEnemy enemy = cell.GetEncounter().enemies.get(k);
                            tooltipText.append("Enemy ").append(k + 1).append(":<br>");
                            tooltipText.append("Health: ").append(enemy.getHealth()).append("<br>");
                            tooltipText.append("Attack: ").append(enemy.getAttack()).append("<br>");
                        }
                        tooltipText.append("</html>");
                        cellLabel.setToolTipText(tooltipText.toString());
                    }
                    // Else resort to default tooltip message.
                    else {
                        cellLabel.setToolTipText("No encounter in this cell.");
                    }

                    // Build out player info and color the cell Blue
                    // If the cell has players.
                    if (cell.GetPlayerCount() > 0) {

                        if (labelText.length() > 0) labelText.append(" | ");

                        labelText.append(cell.GetPlayerCount()).append("P");
                        backgroundColor = cell.hasEncounter ? defaultEPColor : defaultPlayerColor;
                        cellLabel.setForeground(defaultTextColor);
                    }

                    cellLabel.setText(labelText.toString());
                    cellLabel.setBackground(backgroundColor);
                }
            }

            //if(humanPlayerId.contains(tovgs.getCurrentPlayer())) {
                highlightTraversableCells();
                enableCellClicking();
            /*else{
                clearHighlighting();
                disableCellClicking();
            }*/
        }
    }

    private void CreateAndAddActionPanel() {
        JComponent actionPanel = createActionPanel(new IScreenHighlight[0],
                defaultDisplayWidth, 120);
        parent.add(actionPanel, BorderLayout.SOUTH);
    }

    @Override
    protected JComponent createActionPanel(IScreenHighlight[] highlights, int width, int height){
        JPanel actionPanel = new JPanel();
        actionPanel.setOpaque(false);
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        actionButtons = new ActionButton[getMaxActionSpace()];

        for (int i = 0; i < getMaxActionSpace(); i++) {
            ActionButton ab = new ActionButton(ac, highlights);
            actionButtons[i] = ab;
            actionButtons[i].setVisible(false);
            actionPanel.add(actionButtons[i]);
        }

        for (ActionButton actionButton : actionButtons){
            actionButton.informAllActionButtons(actionButtons);
        }

        JScrollPane pane = new JScrollPane(actionPanel);
        pane.setOpaque(false);
        pane.getViewport().setOpaque(false);
        pane.setPreferredSize(new Dimension(width, height));
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);



        return pane;
    }

    private void highlightTraversableCells(){
        TOVGameState tovgs = (TOVGameState) game.getGameState();
        TOVPlayer currentPlayer = tovgs.getTOVPlayerByID(tovgs.getCurrentPlayer());

        // Reset highlights
        for (int i = 0; i < tovgs.grid.getHeight(); i++){
            for (int j = 0; j < tovgs.grid.getWidth(); j++){
                JLabel cellLabel = (JLabel) gridPanel.getComponent(i * tovgs.grid.getWidth() + j);
                cellLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            }
        }

        if (tovgs.getRoundType() == TOVRoundTypes.OUT_OF_COMBAT){
            int budget = tovgs.d6f.getFinalVal();
            for (int i = 0; i < tovgs.grid.getHeight(); i++){
                for (int j = 0; j < tovgs.grid.getWidth(); j++){
                    // Calculate the distance from player and the cell
                    // to see if its within budget and highlight it.
                    Vector2D playerPos = currentPlayer.getPosition();
                    Vector2D cellPos = new Vector2D(j, i);

                    double distance = Math.abs(playerPos.getX() - cellPos.getX()) + Math.abs(playerPos.getY() - cellPos.getY());

                    if (distance <= budget){
                        JLabel cellLabel = (JLabel) gridPanel.getComponent(i * tovgs.grid.getWidth() + j);
                        cellLabel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
                    }
                }
            }
        }
    }

    private void clearHighlighting() {
        TOVGameState tovgs = (TOVGameState) game.getGameState();
        for (int i = 0; i < tovgs.grid.getHeight(); i++) {
            for (int j = 0; j < tovgs.grid.getWidth(); j++) {
                JLabel cellLabel = (JLabel) gridPanel.getComponent(i * tovgs.grid.getWidth() + j);
                cellLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            }
        }
    }

    private JPanel createCombatViewPanel(TOVGameState tovgs) {
        JPanel combatPanel = new JPanel();
        combatPanel.setLayout(new GridLayout(2, 1));
        combatPanel.setBackground(Color.WHITE);

        // ENEMY PANEL
        JPanel enemyPanel = new JPanel();
        enemyPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 20)); // Spacing between enemies
        enemyPanel.setBackground(Color.WHITE);

        ArrayList<TOVEnemy> enemies = tovgs.grid
                .getElement(tovgs.getTOVPlayerByID(tovgs.getCurrentPlayer()).getPosition())
                .GetEncounter().enemies;

        for (TOVEnemy enemy : enemies) {
            JPanel enemyContainer = new JPanel();
            enemyContainer.setLayout(new BorderLayout());
            enemyContainer.setBackground(Color.WHITE);

            JLabel enemyBox = new JLabel(); // Placeholder for enemy image/icon
            enemyBox.setOpaque(true);
            enemyBox.setBackground(Color.RED);
            enemyBox.setPreferredSize(new Dimension(60, 60));
            enemyContainer.add(enemyBox, BorderLayout.CENTER);

            JLabel infoLabel = new JLabel(
                    "<html><div style='text-align: center;'>Enemy " + enemy.getComponentID() +
                            "<br>HP: " + enemy.getHealth() +
                            "<br>ATK: " + enemy.getAttack() + "</div></html>",
                    SwingConstants.CENTER);
            enemyContainer.add(infoLabel, BorderLayout.SOUTH);

            // Listener for Human GUI

            //if (humanPlayerId.contains(tovgs.getCurrentPlayer())) {
                enemyBox.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        System.out.print("ATTACK MODE :" + attackModeActive);
                        if (attackModeActive) {
                            System.out.println("Attacking enemy " + enemy.getComponentID());
                            game.getForwardModel().next(tovgs, new TOVPlayerAttack(enemy.getComponentID()));
                            attackModeActive = false;
                            attackButton.setBackground(Color.LIGHT_GRAY);
                            switchToCombatView();
                            _update(null, game.getGameState());
                        }
                        else if (selectedCard != null) {
                            System.out.println("Using card " + selectedCard.getName() + " on enemy " + enemy.getComponentID());
                            TOVPlayerUseCard cardAction = null;
                            TOVCard card = selectedCard;
                            if (card instanceof TOVCardCleave){
                                ArrayList<Integer> secondaryTargets = new ArrayList<>();
                                for (TOVEnemy enem : enemies){
                                    if (enem.getComponentID() != enemy.getComponentID()){
                                        secondaryTargets.add(enem.getComponentID());
                                    }
                                }
                                cardAction = new TOVPlayerUseCard(selectedCardIndex,
                                        enemy.getComponentID(), secondaryTargets);
                            }
                            else if (card instanceof TOVCardDazzle) {
                                cardAction = new TOVPlayerUseCard(selectedCardIndex,
                                        enemy.getComponentID(), true);
                            }
                            else if (card instanceof TOVCardTaunt){
                                cardAction = new TOVPlayerUseCard(selectedCardIndex,
                                        enemy.getComponentID(), true);
                            }
                            else if (card instanceof TOVCardHeal){
                                System.out.println("Can't use heal on enemy.");
                            }
                            else if (card instanceof TOVCardLifeTap){
                                System.out.println("Can't use lifetap on enemy.");
                            }
                            else if (card instanceof TOVCardEmpower){
                                System.out.println("Can't use empower on enemy.");
                            }
                            else{
                                System.out.println("Unexpected human gui card error");
                            }

                            if (cardAction != null) {
                                game.getForwardModel().next(tovgs, cardAction);
                                tovgs.getTOVPlayerByID(tovgs.getCurrentPlayer()).getHand().remove(selectedCard);
                                selectedCardIndex = -1;
                                selectedCardPanel = null;
                                selectedCard = null;
                                switchToCombatView();
                            }
                            else{
                                System.out.println("Something went wrong!");
                            }
                            _update(null, game.getGameState()); // Refresh screen
                        }
                    }
                });
            //}

            enemyPanel.add(enemyContainer);
        }

        // PLAYER PANEL
        JPanel playerPanel = new JPanel();
        playerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 20)); // Spacing between players
        playerPanel.setBackground(Color.WHITE);

        for (TOVPlayer player : tovgs.getAlivePlayers()) {
            JPanel playerContainer = new JPanel();
            playerContainer.setLayout(new BorderLayout());
            playerContainer.setBackground(Color.WHITE);

            JLabel playerBox = new JLabel(); // Placeholder for player image/icon
            playerBox.setOpaque(true);
            playerBox.setBackground(Color.BLUE);
            playerBox.setPreferredSize(new Dimension(60, 60));
            playerContainer.add(playerBox, BorderLayout.CENTER);

            StringBuilder handString = new StringBuilder();
            for (TOVCard card : player.getHand()) {
                handString.append(card.getName()).append(" ");
            }

            JLabel infoLabel = new JLabel(
                    "<html><div style='text-align: center;'>Player " + player.getPlayerID() +
                            "<br>Class: " + player.getPlayerClass() +
                            "<br>HP: " + player.getHealth() +
                            "<br>ATK: " + player.getDamage() +
                            "<br>Hand: " + handString + "</div></html>",
                    SwingConstants.CENTER);
            playerContainer.add(infoLabel, BorderLayout.SOUTH);

            // Listener for Human GUI
            //if (humanPlayerId.contains(tovgs.getCurrentPlayer())) {
                playerBox.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (selectedCard != null) {
                            System.out.println("Using card " + selectedCard.getName() + " on player " + player.getPlayerID());
                            TOVPlayerUseCard cardAction = null;
                            TOVCard card = selectedCard;
                            if (card instanceof TOVCardCleave){
                                System.out.println("Can't use cleave on player.");
                            }
                            else if (card instanceof TOVCardDazzle) {
                                System.out.println("Can't use dazzle on player.");
                            }
                            else if (card instanceof TOVCardTaunt){
                                System.out.println("Can't use taunt on player.");
                            }
                            else if (card instanceof TOVCardHeal){
                                cardAction = new TOVPlayerUseCard(selectedCardIndex,
                                        player.getPlayerID(), true);
                            }
                            else if (card instanceof TOVCardLifeTap){
                                cardAction = new TOVPlayerUseCard(selectedCardIndex,
                                        player.getPlayerID(), true);
                            }
                            else if (card instanceof TOVCardEmpower){
                                cardAction = new TOVPlayerUseCard(selectedCardIndex,
                                        player.getPlayerID(), true);
                            }
                            else{
                               System.out.println("Unexpected human gui card error");
                            }

                            if (cardAction != null) {
                                game.getForwardModel().next(tovgs, cardAction);
                                tovgs.getTOVPlayerByID(tovgs.getCurrentPlayer()).getHand().remove(selectedCard);
                                selectedCard = null;
                                selectedCardIndex = -1;
                                selectedCardPanel = null;
                                switchToCombatView();
                            }
                            else{
                                System.out.println("Something went wrong!");
                            }
                            _update(null, game.getGameState());
                        }
                    }
                });
            //}

            playerPanel.add(playerContainer);
        }

        JPanel centerPanel = new JPanel(new GridLayout(2, 1));
        centerPanel.add(enemyPanel);
        centerPanel.add(playerPanel);

        combatPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel currentPlayerHandPanel = createCardHandPanel(tovgs.getTOVPlayerByID(tovgs.getCurrentPlayer()));
        combatPanel.add(currentPlayerHandPanel, BorderLayout.SOUTH);
        return combatPanel;
    }

    private JPanel createCardHandPanel(TOVPlayer player) {
        JPanel fullPanel = new JPanel(new BorderLayout());
        fullPanel.setBackground(Color.DARK_GRAY);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        attackButton = new JButton("Default Attack");
        attackButton.setFocusable(false);
        attackButton.setBackground(Color.LIGHT_GRAY);
        attackButton.addActionListener(e -> {
            switchToCombatView();
            attackModeActive = true;
            selectedCard = null;
            selectedCardIndex = -1;
            highlightSelectedCard(null);
            attackButton.setBackground(Color.YELLOW);
            attackModeActive = true;
            System.out.println("Attack mode activated.");
        });

        topPanel.add(attackButton);
        fullPanel.add(topPanel, BorderLayout.NORTH);

        JPanel handPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        handPanel.setBackground(Color.DARK_GRAY);

        List<TOVCard> hand = player.getHand();

        for (int i = 0; i < hand.size(); i ++) {
            JPanel cardPanel = new JPanel();
            cardPanel.setPreferredSize(new Dimension(120, 100));
            cardPanel.setBackground(Color.WHITE);
            cardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));

            JLabel nameLabel = new JLabel(hand.get(i).getName(), SwingConstants.CENTER);
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel descLabel = new JLabel("<html><body style='text-align:center;'>" +
                    hand.get(i).getDescription() + "</body></html>", SwingConstants.CENTER);
            descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            cardPanel.add(nameLabel);
            cardPanel.add(descLabel);

            int finalI = i;
            cardPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (selectedCardPanel == cardPanel){
                        cardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                        selectedCard = null;
                        selectedCardIndex = -1;
                        selectedCardPanel = null;
                    }
                    else {
                        selectedCard = hand.get(finalI); // Store globally
                        selectedCardIndex = finalI;
                        highlightSelectedCard(cardPanel);
                    }
                }
            });

            handPanel.add(cardPanel);
        }

        fullPanel.add(handPanel, BorderLayout.CENTER);
        return fullPanel;
    }

    private void highlightSelectedCard(JPanel cardPanel) {
        if (attackButton != null) {
            attackButton.setBackground(Color.LIGHT_GRAY);
        }
        attackModeActive = false;
        if (selectedCardPanel != null) {
            selectedCardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        }
        if (cardPanel != null) {
            cardPanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
            selectedCardPanel = cardPanel;
        }
        else{
            selectedCardPanel = null;
        }
    }

    private void switchToCombatView() {
        TOVGameState tovgs = (TOVGameState) game.getGameState();
        JPanel combatViewPanel = createCombatViewPanel(tovgs);

        parent.removeAll();
        parent.setLayout(new BorderLayout());
        parent.add(combatViewPanel, BorderLayout.CENTER);
        parent.revalidate();
        parent.repaint();
    }

    private void switchToGridView() {
        TOVGameState gameState = (TOVGameState) game.getGameState();

        parent.removeAll();
        DrawGrid(gameState, (TOVForwardModel) game.getForwardModel()); // Recreate grid
        CreateAndAddActionPanel();
        parent.revalidate();
        parent.repaint();
    }

    @Override
    public int getMaxActionSpace() {
        //int maxMoveActions = 12;
        // Really they have 12 but since we just want
        // to have 1 move button we keep it as 1.

        int maxMoveActions = 1;
        int maxAttackActions = 3;
        return maxMoveActions + maxAttackActions;
    }
}
