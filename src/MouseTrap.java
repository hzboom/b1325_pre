import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

public class MouseTrap {

    static Frame f;
    private static KeyAdapter listener;
    static Cell mousePos;
    static String currentDirection = "left";
    static boolean isPlayable = false;
    static boolean hasWon = false;
    static boolean showSolution = false;

    private static void showMazeSelectionMenu() throws InterruptedException {
        new MazeSelectionMenu();
    }

    public static void startGameWithExistingMaze() throws InterruptedException {
        // Create new frame with the already selected maze and begin game
        f = new Frame(MazeGenerator.rows, MazeGenerator.cols);
        setupGameplay();
    }

    private static void setupGameplay() {
        // Begin gameplay mode
        isPlayable = true;
        showSolution = false;
        hasWon = false;
        mousePos = MazeGenerator.cells[0][0];
        currentDirection = "right";
        
        f.toggleWinOverlay(false);
        setupKeyListener();
        f.setFocusable(true);
        f.requestFocusInWindow();
    }

    private static void setupKeyListener() {
        // Arrow keys for player movement
        listener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!isPlayable || hasWon) return;

                int key = e.getKeyCode();
                int newX = mousePos.x;
                int newY = mousePos.y;
                
                // Check for valid move and get new coordinates
                if (key == KeyEvent.VK_UP && !mousePos.up && mousePos.y > 0) newY--;
                else if (key == KeyEvent.VK_DOWN && !mousePos.down && mousePos.y < MazeGenerator.rows - 1) newY++;
                else if (key == KeyEvent.VK_LEFT) {
                    currentDirection = "left";
                    if (!mousePos.left && mousePos.x > 0) newX--;
                }
                else if (key == KeyEvent.VK_RIGHT) {
                    currentDirection = "right";
                    if (!mousePos.right && mousePos.x < MazeGenerator.cols - 1) newX++;
                }

                // Update position if valid move and check win condition
                if (newX != mousePos.x || newY != mousePos.y) {
                    mousePos = MazeGenerator.cells[newX][newY];
                    if (mousePos.x == MazeGenerator.cols - 1 && mousePos.y == MazeGenerator.rows - 1) {
                        hasWon = true;
                        f.toggleWinOverlay(true);
                    }
                }
                f.graphics.repaint();
            }
        };
        f.addKeyListener(listener);
    }

    public static void setShowSolution(boolean newValue) {
        showSolution = newValue;
        f.requestFocusInWindow();
        f.graphics.repaint();
    }

    public static void restartGame() {
        // Reset player position with same maze
        hasWon = false;
        f.removeKeyListener(listener);
        setupGameplay();
        f.resetButtons();
        f.graphics.repaint();
    }

    public static void resetGame() throws InterruptedException {
        // Show maze selection menu for new maze
        f.dispose();
        showMazeSelectionMenu();
    }

    public static void main(String args[]) {
        try {
            showMazeSelectionMenu();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}