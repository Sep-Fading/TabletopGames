package games.talesofvalor.gui;

import core.AbstractGameState;
import core.AbstractPlayer;
import core.Game;
import games.talesofvalor.TOVPlayer;
import games.talesofvalor.components.TOVCell;
import games.talesofvalor.components.TOVEnemy;
import games.talesofvalor.TOVForwardModel;
import games.talesofvalor.TOVGameState;
import gui.AbstractGUIManager;
import gui.GamePanel;
import gui.IScreenHighlight;
import players.human.ActionController;

import javax.swing.*;
import java.awt.*;
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


    public TOVGUIManager(GamePanel parent, Game game, ActionController ac, Set<Integer> human) {
        super(parent, game, ac, human);
        if (game == null) return;

        TOVGameState gameState = (TOVGameState) game.getGameState();

        DrawGrid(gameState, (TOVForwardModel) game.getForwardModel());
        CreateAndAddActionPanel();
        _update(null, gameState);
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

    @Override
    protected void _update(AbstractPlayer player, AbstractGameState gameState) {
        TOVGameState tovgs = (TOVGameState) gameState;

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
                    for(int k=0; k < cell.GetEncounter().enemies.size(); k++){
                        TOVEnemy enemy = cell.GetEncounter().enemies.get(k);
                        tooltipText.append("Enemy ").append(k+1).append(":<br>");
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
