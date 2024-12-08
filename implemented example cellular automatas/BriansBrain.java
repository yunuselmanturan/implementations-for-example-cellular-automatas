import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class BriansBrain extends JPanel {
    private final int gridSize = 100;
    private final int cellSize = 5;
    private int[][] grid = new int[gridSize][gridSize];
    private int[][] nextGrid = new int[gridSize][gridSize];
    private boolean running = false;
    private Timer timer;

    public BriansBrain() {
        setPreferredSize(new Dimension(gridSize * cellSize, gridSize * cellSize));
        setBackground(Color.BLACK);

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> start());

        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(e -> stop());

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> reset());

        JButton randomizeButton = new JButton("Randomize");
        randomizeButton.addActionListener(e -> randomize());

        JPanel controlPanel = new JPanel();
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        controlPanel.add(resetButton);
        controlPanel.add(randomizeButton);

        JFrame frame = new JFrame("Brian's Brain Automaton");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(this, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);

        timer = new Timer(100, e -> updateGrid());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (grid[i][j] == 1) g.setColor(Color.RED);
                else if (grid[i][j] == 2) g.setColor(Color.YELLOW);
                else g.setColor(Color.BLACK);
                g.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
            }
        }
    }

    private void randomize() {
        Random random = new Random();
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                grid[i][j] = random.nextInt(3);
            }
        }
        repaint();
    }

    private void reset() {
        grid = new int[gridSize][gridSize];
        repaint();
    }

    private void start() {
        running = true;
        timer.start();
    }

    private void stop() {
        running = false;
        timer.stop();
    }

    private void updateGrid() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                int livingNeighbors = countLivingNeighbors(i, j);
                if (grid[i][j] == 0 && livingNeighbors == 2) {
                    nextGrid[i][j] = 1;
                } else if (grid[i][j] == 1) {
                    nextGrid[i][j] = 2;
                } else if (grid[i][j] == 2) {
                    nextGrid[i][j] = 0;
                } else {
                    nextGrid[i][j] = grid[i][j];
                }
            }
        }
        int[][] temp = grid;
        grid = nextGrid;
        nextGrid = temp;
        repaint();
    }

    private int countLivingNeighbors(int x, int y) {
        int count = 0;
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};
        for (int i = 0; i < 8; i++) {
            int nx = (x + dx[i] + gridSize) % gridSize;
            int ny = (y + dy[i] + gridSize) % gridSize;
            if (grid[nx][ny] == 1) count++;
        }
        return count;
    }

    public static void main(String[] args) {
        new BriansBrain();
    }
}
