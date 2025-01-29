package games.talesofvalor.gui;

import core.AbstractGameState;
import core.AbstractPlayer;
import core.Game;
import games.talesofvalor.TOVCell;
import games.talesofvalor.TOVEnemy;
import games.talesofvalor.TOVForwardModel;
import games.talesofvalor.TOVGameState;
import gui.AbstractGUIManager;
import gui.GamePanel;
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
        _update(null, gameState);
    }

    // Initial Grid Setup
    private void DrawGrid(TOVGameState tovgs, TOVForwardModel tovfm) {
        // Tooltip configuration (optional styling)
        UIManager.put("ToolTip.background", Color.YELLOW);
        UIManager.put("ToolTip.foreground", Color.BLACK);
        UIManager.put("ToolTip.font", new Font("SansSerif", Font.BOLD, 12));

        // Create JFrame for the grid visualization
        JFrame frame = new JFrame("Tales of Valor - Test UI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(defaultDisplayWidth, defaultInfoPanelHeight);

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
                    cellLabel.setText(String.valueOf(cell.encounter.enemies.size())); // Display number of enemies
                    cellLabel.setOpaque(true);
                    cellLabel.setBackground(defaultEncounterColor);
                    cellLabel.setForeground(defaultTextColor);

                    // Generate tooltip text with detailed enemy info
                    StringBuilder tooltipText = new StringBuilder("<html>"); // HTML allows multiline tooltips
                    tooltipText.append("Encounter!<br>");
                    List<TOVEnemy> enemies = cell.encounter.enemies; // Assuming `enemies` is a List<Enemy>
                    for (int k = 0; k < enemies.size(); k++) {
                        TOVEnemy enemy = enemies.get(k);
                        tooltipText.append("Enemy ").append(k + 1).append(":<br>");
                        tooltipText.append("&nbsp;&nbsp;Health: ").append(enemy.attack).append("<br>");
                        tooltipText.append("&nbsp;&nbsp;Attack: ").append(enemy.health).append("<br>");
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
        frame.add(gridPanel);
        frame.setVisible(true);
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
                    labelText.append(cell.encounter.enemyCount).append("E");
                    backgroundColor = defaultEncounterColor;

                    StringBuilder tooltipText = new StringBuilder("<html>Encounter:<br>");
                    for(int k=0; k < cell.encounter.enemies.size(); k++){
                        TOVEnemy enemy = cell.encounter.enemies.get(k);
                        tooltipText.append("Enemy ").append(k+1).append(":<br>");
                        tooltipText.append("Health: ").append(enemy.health).append("<br>");
                        tooltipText.append("Attack: ").append(enemy.attack).append("<br>");
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

    @Override
    public int getMaxActionSpace() {
        return 0;
    }
}
