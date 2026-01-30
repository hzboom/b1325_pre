import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {

    Graphics graphics = new Graphics();
    private JPanel buttonPanel;
    private JToggleButton solutionButton;
    private JPanel overlayPanel;
    private JButton restartButton;
    private JButton newMazeButton;
    private JButton quitButton;

    public Frame(int rows, int cols) {
        int mazeWidth = cols * MazeGenerator.GRID_SIZE;
        int mazeHeight = rows * MazeGenerator.GRID_SIZE;
        int totalWidth = mazeWidth + 2 * Graphics.BORDER_SIDE;
        int totalHeight = mazeHeight + Graphics.BORDER_TOP + Graphics.BORDER_BOTTOM;
        int panelHeight = 40;
        
        // Create button panel
        buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(totalWidth, panelHeight));
        restartButton = new JButton("Restart");
        newMazeButton = new JButton("New Maze");
        quitButton = new JButton("Quit");
        solutionButton = new JToggleButton("Show Solution");
        
        // Add button action listeners
        restartButton.addActionListener(e -> MouseTrap.restartGame());
        newMazeButton.addActionListener(e -> {
            try {
                MouseTrap.resetGame();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });
        quitButton.addActionListener(e -> System.exit(0));
        solutionButton.addActionListener(e -> {
            MouseTrap.setShowSolution(solutionButton.isSelected());
        });
        
        // Add buttons to panel
        buttonPanel.add(restartButton);
        buttonPanel.add(newMazeButton);
        buttonPanel.add(solutionButton);
        buttonPanel.add(quitButton);

        createOverlayPanel();

        // Increase panelHeight if needed for narrow mazes
        panelHeight = computePanelHeight(totalWidth);
        buttonPanel.setPreferredSize(new Dimension(totalWidth, panelHeight));
        this.setPreferredSize(new Dimension(totalWidth, totalHeight + 30 + panelHeight));
        
        // Set up the frame
        this.setLayout(new BorderLayout());
        this.add(graphics, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);
        this.setVisible(true);
        
        // Force correct margin dimensions using insets 
        Insets insets = this.getInsets();
        int contentHeight = totalHeight + panelHeight;
        this.setSize(totalWidth + insets.left + insets.right, 
                    contentHeight + insets.top + insets.bottom);
        
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void createOverlayPanel() {
        overlayPanel = new JPanel();
        overlayPanel.setLayout(new BoxLayout(overlayPanel, BoxLayout.Y_AXIS));
        overlayPanel.setOpaque(false);
        overlayPanel.setVisible(false);
        
        // Create buttons for overlay with consistent sizing
        JButton overlayRestart = new JButton("Restart");
        JButton overlayNewMaze = new JButton("New Maze");
        JButton overlayQuit = new JButton("Quit");
        
        // Set consistent button sizes
        Dimension buttonSize = new Dimension(120, 30);
        overlayRestart.setPreferredSize(buttonSize);
        overlayRestart.setMaximumSize(buttonSize);
        overlayNewMaze.setPreferredSize(buttonSize);
        overlayNewMaze.setMaximumSize(buttonSize);
        overlayQuit.setPreferredSize(buttonSize);
        overlayQuit.setMaximumSize(buttonSize);
        
        // Action listeners for overlay screen
        overlayRestart.addActionListener(e -> MouseTrap.restartGame());
        overlayNewMaze.addActionListener(e -> {
            try {
                MouseTrap.resetGame();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });
        overlayQuit.addActionListener(e -> System.exit(0));

        // Add vertical spacing and center alignment
        overlayPanel.add(Box.createVerticalGlue());
        overlayPanel.add(Box.createVerticalStrut(20));
        
        overlayRestart.setAlignmentX(Component.CENTER_ALIGNMENT);
        overlayPanel.add(overlayRestart);
        overlayPanel.add(Box.createVerticalStrut(10));
        
        overlayNewMaze.setAlignmentX(Component.CENTER_ALIGNMENT);
        overlayPanel.add(overlayNewMaze);
        overlayPanel.add(Box.createVerticalStrut(10));
        
        overlayQuit.setAlignmentX(Component.CENTER_ALIGNMENT);
        overlayPanel.add(overlayQuit);
        overlayPanel.add(Box.createVerticalGlue());
        
        // Add overlay to graphics panel
        graphics.setLayout(new OverlayLayout(graphics));
        graphics.add(overlayPanel);
    }

    private int computePanelHeight(int mazeWidth) {
        // Dynamically determine height of the button panel
        int height = 40;
        int width = restartButton.getPreferredSize().width;
        if (width > mazeWidth) {
            height += 30;
            width = 0;
        }
        width += newMazeButton.getPreferredSize().width;
        if (width > mazeWidth) {
            height += 30;
            width = newMazeButton.getPreferredSize().width;
        }
        width += solutionButton.getPreferredSize().width;
        if (width > mazeWidth) {
            height += 30;
            width = solutionButton.getPreferredSize().width;
        }
        width += quitButton.getPreferredSize().width;
        if (width > mazeWidth) height += 30;
        return height;
    }

    public void toggleWinOverlay(boolean show) {
        buttonPanel.setVisible(!show);
        overlayPanel.setVisible(show);
        graphics.repaint();
    }

    public void resetButtons() {
        solutionButton.setSelected(false);
    } 
}