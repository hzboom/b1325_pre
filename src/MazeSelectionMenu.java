import javax.swing.*;
import java.awt.*;

/**
 * 迷宫选择面板（由原 MazeSelectionMenu JFrame 重构为 JPanel）。
 *
 * 创建者：Kilo Code
 * 创建日期：2026-01-28
 */
public class MazeSelectionMenu extends JPanel {

    private final int[][] MAZE_SIZES = {{5, 5}, {9, 9}, {4, 6}, {18, 8}};
    private final String[] MAZE_LABELS = {"Easy Square", "Medium Square", "Easy Rectangle", "Hard Rectangle"};
    private Cell[][][] mazes;
    private JPanel[] mazePanels;

    private final MainApplication app;

    public MazeSelectionMenu(MainApplication app) {
        this.app = app;
        generateMazeOptions();
        setupUI();
    }
    
    private void generateMazeOptions() {
        try {
            mazes = new Cell[MAZE_SIZES.length][2][2];
            for (int i = 0; i < MAZE_SIZES.length; i++) {
                MazeGenerator.rows = MAZE_SIZES[i][1];
                MazeGenerator.cols = MAZE_SIZES[i][0];
                MazeGenerator.initializeMaze();
                MazeGenerator.generateMaze();
                mazes[i] = copyMaze(MazeGenerator.cells, MAZE_SIZES[i][0], MAZE_SIZES[i][1]);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private Cell[][] copyMaze(Cell[][] original, int cols, int rows) {
        Cell[][] copy = new Cell[cols][rows];
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                copy[i][j] = new Cell(original[i][j].x, original[i][j].y);
                copy[i][j].up = original[i][j].up;
                copy[i][j].down = original[i][j].down;
                copy[i][j].left = original[i][j].left;
                copy[i][j].right = original[i][j].right;
                copy[i][j].visited = original[i][j].visited;
            }
        }
        return copy;
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Select a Maze to Play", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // Maze panels in 2x2 grid
        JPanel mazesPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        mazesPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 20, 20));
        
        mazePanels = new JPanel[MAZE_SIZES.length];
        for (int j = 0; j < MAZE_SIZES.length; j++) {
            final int i = j;
            JPanel mazeContainer = new JPanel(new BorderLayout());
            mazeContainer.setBorder(BorderFactory.createTitledBorder(MAZE_LABELS[i]));
            mazePanels[i] = new MazePreviewPanel(mazes[i], MAZE_SIZES[i][0], MAZE_SIZES[i][1]);
            mazeContainer.add(mazePanels[i], BorderLayout.CENTER);
            
            JButton selectMazeButton = new JButton("Play Map (" + MAZE_SIZES[i][0] + "x" + MAZE_SIZES[i][1] + ")");
            selectMazeButton.addActionListener(e -> selectMaze(mazes[i], MAZE_SIZES[i][0], MAZE_SIZES[i][1]));
            mazeContainer.add(selectMazeButton, BorderLayout.SOUTH);
            mazesPanel.add(mazeContainer);
        }
        add(mazesPanel, BorderLayout.CENTER);
        
        revalidate();
        repaint();
    }
    
    private void selectMaze(Cell[][] selectedMaze, int cols, int rows) {
        // Set the selected maze size and maze in MazeGenerator
        MazeGenerator.rows = rows;
        MazeGenerator.cols = cols;
        MazeGenerator.setMaze(selectedMaze);
        // Find solution, and start the game (switch panel in the same frame)
        MazeGenerator.findSolution();

        MouseTrap.startGameWithExistingMaze();
        if (app != null) {
            app.showGame();
        }
    }
    
    private class MazePreviewPanel extends JPanel {
        private Cell[][] maze;
        private int cols;
        private int rows;
        private int previewGridSize;
        
        public MazePreviewPanel(Cell[][] maze, int cols, int rows) {
            this.maze = maze;
            this.cols = cols;
            this.rows = rows;

            // Scale preview size based on maze dimensions
            this.previewGridSize = 200 / Math.max(cols, rows);
            setPreferredSize(new Dimension(cols * previewGridSize, rows * previewGridSize));
            setBackground(Color.WHITE);
        }
        
        protected void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);
            
            // Draw maze walls
            g.setColor(Color.BLACK);
            for (int i = 0; i < cols; i++) {
                for (int j = 0; j < rows; j++) {
                    Cell c = maze[i][j];
                    if (c.left) g.drawLine(c.x * previewGridSize, c.y * previewGridSize, 
                                         c.x * previewGridSize, c.y * previewGridSize + previewGridSize);
                    if (c.right) g.drawLine(c.x * previewGridSize + previewGridSize, c.y * previewGridSize, 
                                          c.x * previewGridSize + previewGridSize, c.y * previewGridSize + previewGridSize);
                    if (c.up) g.drawLine(c.x * previewGridSize, c.y * previewGridSize, 
                                       c.x * previewGridSize + previewGridSize, c.y * previewGridSize);
                    if (c.down) g.drawLine(c.x * previewGridSize, c.y * previewGridSize + previewGridSize, 
                                         c.x * previewGridSize + previewGridSize, c.y * previewGridSize + previewGridSize);
                }
            }
            
            // Draw start indicator
            g.setColor(Color.GREEN);
            g.fillOval(2, 2, previewGridSize - 4, previewGridSize - 4);
            
            // Draw end indicator
            g.setColor(Color.RED);
            int endX = (cols - 1) * previewGridSize;
            int endY = (rows - 1) * previewGridSize;
            g.fillOval(endX + 2, endY + 2, previewGridSize - 4, previewGridSize - 4);
        }
    }
}

