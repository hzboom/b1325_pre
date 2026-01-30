import javax.swing.*;
import java.awt.*;

/**
 * 主应用窗口：全程只存在一个 JFrame，避免切换迷宫时销毁窗口导致录屏中断。
 *
 * 创建者：Kilo Code
 * 创建日期：2026-01-28
 */
public class MainApplication extends JFrame {

    private static final String CARD_MAZE_SELECTION = "mazeSelection";
    private static final String CARD_GAME = "game";

    private final JPanel rootPanel;
    private final CardLayout cardLayout;

    private final MazeSelectionMenu mazeSelectionPanel;
    private Frame gamePanel;

    public MainApplication() {
        setTitle("Mouse Trap Maze");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        cardLayout = new CardLayout();
        rootPanel = new JPanel(cardLayout);

        // 先创建一个默认迷宫尺寸，避免 Graphics 构造时使用未初始化的 rows/cols
        MazeGenerator.rows = 5;
        MazeGenerator.cols = 5;
        MazeGenerator.initializeMaze();
        try {
            MazeGenerator.generateMaze();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MazeGenerator.findSolution();

        mazeSelectionPanel = new MazeSelectionMenu(this);
        gamePanel = new Frame(MazeGenerator.rows, MazeGenerator.cols);

        // 让 MouseTrap 复用同一个游戏面板实例
        MouseTrap.setFrame(gamePanel);
        MouseTrap.setMainApplication(this);

        rootPanel.add(mazeSelectionPanel, CARD_MAZE_SELECTION);
        rootPanel.add(gamePanel, CARD_GAME);

        setContentPane(rootPanel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void showMazeSelection() {
        cardLayout.show(rootPanel, CARD_MAZE_SELECTION);
        repaint();
    }

    public void showGame() {
        // 组件复用策略：不销毁游戏面板，只更新布局和尺寸
        gamePanel.reloadLayoutForMaze(MazeGenerator.rows, MazeGenerator.cols);
        MouseTrap.setFrame(gamePanel);

        cardLayout.show(rootPanel, CARD_GAME);

        // 确保键盘事件有效
        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();
        repaint();
    }
}