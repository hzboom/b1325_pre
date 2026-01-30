import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;
import java.util.LinkedList;
import java.util.Queue;
import java.util.List;

public class MazeGenerator {

    final static int GRID_SIZE = 70;
    static Cell[][] cells;
    static Stack<Cell> stack;
    static boolean generationComplete;
    static int rows;
    static int cols;
    static Cell current;
    static List<Cell> solutionPath;

    public static void initializeMaze() {
        // Initialize grid of cells that is rows x cols
        cells = new Cell[cols][rows];
        stack = new Stack<Cell>();
        generationComplete = false;
        solutionPath = new ArrayList<>();

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }
        
        current = cells[0][0];
        current.visited = true;
        stack.push(current);
    }

    public static void generateMaze() throws InterruptedException {
        // Recursively generate new maze
        while (!generationComplete) {
            Cell neighbor = checkNeighbors(current);
            if (neighbor.x != -1) {
                neighbor.visited = true;
                removeWalls(current, neighbor);
                current = neighbor;
                stack.push(current);
            } 
            else current = stack.pop();

            if (current == cells[0][0]) generationComplete = true;
        }

        current = new Cell(-1, -1);
    }

    private static void removeWalls(Cell c, Cell n) {
        int xDiff = n.x - c.x;
        int yDiff = n.y - c.y;

        if (xDiff == 1) {
            c.right = false;
            n.left = false;
        } else if (xDiff == -1) {
            c.left = false;
            n.right = false;
        }
        if (yDiff == 1) {
            c.down = false;
            n.up = false;
        } else if (yDiff == -1) {
            c.up = false;
            n.down = false;
        }
    }

    public static Cell checkNeighbors(Cell c) {
        ArrayList<Cell> neighbors = new ArrayList<Cell>();

        if (c.y != 0) {
            if (!cells[c.x][c.y-1].visited) neighbors.add(cells[c.x][c.y-1]);
        }
        if (c.y != rows - 1) {
            if (!cells[c.x][c.y+1].visited) neighbors.add(cells[c.x][c.y+1]);
        }
        if (c.x != 0) {
            if (!cells[c.x-1][c.y].visited) neighbors.add(cells[c.x-1][c.y]);
        }
        if (c.x != cols - 1) {
            if (!cells[c.x+1][c.y].visited) neighbors.add(cells[c.x+1][c.y]);
        }

        if (neighbors.size() > 0) {
            int randomNum = ThreadLocalRandom.current().nextInt(0, neighbors.size());
            return neighbors.get(randomNum);
        }
        return new Cell(-1, -1);
    }

    public static void findSolution() {
        // Breadth-first search to find the shortest path (solution)
        solutionPath.clear();
        if (cells == null || cells.length == 0) return;
      
        // Track parent to reconstruct the path
        Cell[][] parent = new Cell[cols][rows];
        boolean[][] visited = new boolean[cols][rows];  
        Queue<Cell> queue = new LinkedList<>();

        Cell start = cells[0][0];
        queue.add(start);
        visited[0][0] = true;
        
        boolean found = false;
        while (!queue.isEmpty()) {
            Cell currentCell = queue.poll();
            if (currentCell.x == cols - 1 && currentCell.y == rows - 1) {
                found = true;
                break;
            }

            // Check neighbors (up, down, left, right) if no wall and not visited
            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
            for (int[] dir : directions) {
                int nx = currentCell.x + dir[0];
                int ny = currentCell.y + dir[1];
                if (nx >= 0 && nx < cols && ny >= 0 && ny < rows && !visited[nx][ny]) {
                    // Check for walls between currentCell and neighbor
                    if ((dir[0] == -1 && !currentCell.left) || (dir[0] == 1 && !currentCell.right) ||
                        (dir[1] == -1 && !currentCell.up) || (dir[1] == 1 && !currentCell.down)) {
                        visited[nx][ny] = true;
                        parent[nx][ny] = currentCell;
                        queue.add(cells[nx][ny]);
                    }
                }
            }
        }

        // Reconstruct the path when found
        if (found) {
            Cell step = cells[cols - 1][rows - 1];
            while (step != null) {
                solutionPath.add(0, step);
                step = parent[step.x][step.y];
            }
        }
    }
    
    public static void setMaze(Cell[][] selectedMaze) {
        cells = selectedMaze;
    }
}