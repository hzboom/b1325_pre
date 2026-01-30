import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Graphics extends JPanel {

    static final int BORDER_SIDE = 25;
    static final int BORDER_TOP = 25;
    static final int BORDER_BOTTOM = 50;
    private BufferedImage mouseImage;
    private int gridSize;

    public Graphics() {
        gridSize = MazeGenerator.GRID_SIZE;
        try {
            mouseImage = ImageIO.read(new File("src/assets/mouse.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
            mouseImage = null;
        }
        setLayout(null);
        int totalWidth = MazeGenerator.cols * gridSize + 2 * BORDER_SIDE;
        int totalHeight = MazeGenerator.rows * gridSize + BORDER_TOP + BORDER_BOTTOM;
        setPreferredSize(new Dimension(totalWidth + 30, totalHeight));
    }
    
    public void updatePreferredSizeForMaze(int rows, int cols) {
        // Update the grid size and recalculate dimensions for new maze
        gridSize = MazeGenerator.GRID_SIZE;
        int totalWidth = cols * gridSize + 2 * BORDER_SIDE;
        int totalHeight = rows * gridSize + BORDER_TOP + BORDER_BOTTOM;
        setPreferredSize(new Dimension(totalWidth + 30, totalHeight));
        revalidate();
        repaint();
    }
    
    public void paintMazeLabels(java.awt.Graphics g) {
        // Draw Start label at the beginning of the maze
        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.BOLD, gridSize/5));
        g.drawString("START", BORDER_SIDE + gridSize/5, BORDER_TOP + gridSize/3);
        
        // Draw cheese at bottom-right corner
        if (MazeGenerator.cells != null && MazeGenerator.cells.length > 0 && MazeGenerator.cells[0].length > 0) {
            int endX = MazeGenerator.cols - 1;
            int endY = MazeGenerator.rows - 1;

            g.setColor(Color.yellow);
            int[] xPoints = {
                BORDER_SIDE + endX * gridSize + gridSize/4,
                BORDER_SIDE + endX * gridSize + gridSize/2,
                BORDER_SIDE + endX * gridSize + 3*gridSize/4
            };
            int[] yPoints = {
                BORDER_TOP + endY * gridSize + 3*gridSize/4,
                BORDER_TOP + endY * gridSize + gridSize/4,
                BORDER_TOP + endY * gridSize + 3*gridSize/4
            };
            g.fillPolygon(xPoints, yPoints, 3);
            
            g.setColor(Color.orange);
            int ovalSize = gridSize/10;
            g.fillOval(xPoints[0] + ovalSize, yPoints[0] - 3*ovalSize/2, ovalSize, ovalSize);
            g.fillOval(xPoints[1] - ovalSize/2, yPoints[1] + 3*ovalSize/2, ovalSize, ovalSize);
            g.fillOval(xPoints[2] - 2*ovalSize, yPoints[2] - 3*ovalSize/2, ovalSize, ovalSize);
        }
    }

    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        paintMazeLabels(g);
        Graphics2D g2d = (Graphics2D) g;

        // Paint all cells in MazeGenerator.cells
        for (int i = 0; i < MazeGenerator.cols; i++) {
            for (int j = 0; j < MazeGenerator.rows; j++) {
                Cell c = MazeGenerator.cells[i][j];
                g.setColor(Color.black);
                int cellX = BORDER_SIDE + c.x * gridSize;
                int cellY = BORDER_TOP + c.y * gridSize;
                
                if (c.left) g.drawLine(cellX, cellY, cellX, cellY + gridSize);
                if (c.right) g.drawLine(cellX + gridSize, cellY, cellX + gridSize, cellY + gridSize);
                if (c.up) g.drawLine(cellX, cellY, cellX + gridSize, cellY);
                if (c.down) g.drawLine(cellX, cellY + gridSize, cellX + gridSize, cellY + gridSize);
            }
        }
        
        // Draw solution path if enabled
        if (MouseTrap.showSolution) {
            g2d.setColor(new Color(0, 0, 255, 180));
            g2d.setStroke(new BasicStroke(10));
            
            for (int i = 0; i < MazeGenerator.solutionPath.size() - 1; i++) {
                Cell current = MazeGenerator.solutionPath.get(i);
                Cell next = MazeGenerator.solutionPath.get(i + 1);
                
                // Draw line from center of current cell to center of next cell
                int x1 = BORDER_SIDE + current.x * gridSize + gridSize / 2;
                int y1 = BORDER_TOP + current.y * gridSize + gridSize / 2;
                int x2 = BORDER_SIDE + next.x * gridSize + gridSize / 2;
                int y2 = BORDER_TOP + next.y * gridSize + gridSize / 2;
                g2d.drawLine(x1, y1, x2, y2);
            }
        }

        if (MouseTrap.isPlayable && MouseTrap.mousePos != null && mouseImage != null && !MouseTrap.hasWon) {
            // Draw mouse image at mousePos with scaling and direction-based transformation
            int mouseSize = (int) (gridSize * 0.8);
            int offsetX = BORDER_SIDE + MouseTrap.mousePos.x * gridSize + (gridSize - mouseSize) / 2;
            int offsetY = BORDER_TOP + MouseTrap.mousePos.y * gridSize + (gridSize - mouseSize) / 2;

            // Apply transformation to show mouse direction
            AffineTransform transform = new AffineTransform();
            transform.translate(offsetX, offsetY);
            double scaleWidth = (double) mouseSize / mouseImage.getWidth();
            double scaleHeight = (double) mouseSize / mouseImage.getHeight();
            if ("left".equals(MouseTrap.currentDirection)) {
                transform.scale(scaleWidth, scaleHeight);
            } else {
                // Flip horizontally/mirror for right-facing
                transform.scale(-scaleWidth, scaleHeight);
                transform.translate(-mouseImage.getWidth(), 0);
            }
            g2d.drawImage(mouseImage, transform, null);
        } else if (MouseTrap.hasWon) {
            // Draw semi-transparent overlay (60% opacity)
            double alpha = 0.6;
            g.setColor(new Color(0, 0, 0, (int) (255 * alpha)));
            g.fillRect(0, 0, getWidth(), getHeight());
            
            g.setColor(Color.yellow);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            String winMessage = "YOU WIN!";
            FontMetrics fm = g.getFontMetrics();
            int messageWidth = fm.stringWidth(winMessage);
            g.drawString(winMessage, (getWidth() - messageWidth) / 2, getHeight() / 2 - 120);
        }
    }
}