import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WireWorld extends JPanel {
    private static final int CELL_SIZE = 10; // Cell size in pixels
    private static final int GRID_WIDTH = 60; // Grid width (number of cells)
    private static final int GRID_HEIGHT = 40; // Grid height (number of cells)
    private static final int UPDATE_INTERVAL = 200; // Update interval in milliseconds

    private int[][] grid; // Current grid state
    private int[][] nextGrid; // Temporary grid for the next state
    private boolean running = false; // Simulation running flag

    public WireWorld() {
        grid = new int[GRID_WIDTH][GRID_HEIGHT];
        nextGrid = new int[GRID_WIDTH][GRID_HEIGHT];
        initializePattern(); // Set the initial pattern

        // Add mouse interaction
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX() / CELL_SIZE;
                int y = e.getY() / CELL_SIZE;
                if (x >= 0 && x < GRID_WIDTH && y >= 0 && y < GRID_HEIGHT) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        grid[x][y] = (grid[x][y] + 1) % 4; // Cycle through states (0->1->2->3->0)
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        grid[x][y] = 0; // Cancel (set to empty)
                    }
                    repaint();
                }
            }
        });
    }

    // Set an initial pattern
    private void initializePattern() {
        grid[10][10] = 1; // Conductor
        grid[11][10] = 2; // Electron Head
        grid[12][10] = 3; // Electron Tail
        grid[13][10] = 1; // Conductor
        grid[13][11] = 1; // Conductor
        grid[13][12] = 1; // Conductor
        grid[12][12] = 2; // Electron Head
        grid[11][12] = 3; // Electron Tail
    }

    // Compute the next generation of the grid
    private void nextGeneration() {
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                nextGrid[x][y] = applyRules(x, y);
            }
        }

        // Swap current and next grids
        int[][] temp = grid;
        grid = nextGrid;
        nextGrid = temp;

        repaint();
    }

    // Apply rules to determine the next state of a cell
    private int applyRules(int x, int y) {
        int state = grid[x][y];
        if (state == 0) { // Empty cell
            return 0;
        } else if (state == 1) { // Conductor
            int electronHeads = countNeighbors(x, y, 2);
            return (electronHeads == 1 || electronHeads == 2) ? 2 : 1;
        } else if (state == 2) { // Electron Head
            return 3; // Becomes Electron Tail
        } else if (state == 3) { // Electron Tail
            return 1; // Becomes Conductor
        }
        return 0; // Default
    }

    // Count the number of neighbors in the target state
    private int countNeighbors(int x, int y, int targetState) {
        int count = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue; // Skip the cell itself
                int nx = x + dx;
                int ny = y + dy;
                if (nx >= 0 && nx < GRID_WIDTH && ny >= 0 && ny < GRID_HEIGHT && grid[nx][ny] == targetState) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                g.setColor(getStateColor(grid[x][y]));
                g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                g.setColor(Color.GRAY);
                g.drawRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    // Get the color corresponding to the cell state
    private Color getStateColor(int state) {
        switch (state) {
            case 0: return Color.BLACK; // Empty
            case 1: return Color.YELLOW; // Conductor
            case 2: return Color.BLUE; // Electron Head
            case 3: return Color.RED; // Electron Tail
            default: return Color.BLACK;
        }
    }

    // Start the simulation
    public void startSimulation() {
        running = true;
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                while (running) {
                    nextGeneration();
                    try {
                        Thread.sleep(UPDATE_INTERVAL);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                return null;
            }
        }.execute();
    }

    // Stop the simulation
    public void stopSimulation() {
        running = false;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("WireWorld - Interactive Editing");
        WireWorld wireWorld = new WireWorld();

        // Control buttons
        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");

        startButton.addActionListener(e -> wireWorld.startSimulation());
        stopButton.addActionListener(e -> wireWorld.stopSimulation());

        JPanel controlPanel = new JPanel();
        controlPanel.add(startButton);
        controlPanel.add(stopButton);

        frame.setLayout(new BorderLayout());
        frame.add(wireWorld, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);

        wireWorld.setPreferredSize(new Dimension(GRID_WIDTH * CELL_SIZE, GRID_HEIGHT * CELL_SIZE));
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
