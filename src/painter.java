import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/*
        ToDo:
            Add seeding
            Add controls - space to play/pause, arrow keys for one step back/forward, up/down for rate
            Add status bar with rate, generation etc
            Add mouse control for toggling cells
            Make resolution/cell dims intelligent to cope with any combination
            Nicer colour scheme/styling
*/


class Surface extends JPanel implements ActionListener {
    private int canvasWidth;
    private int canvasHeight;
    private int cellDims;
    private GameOfLife GoL;
    private Timer timer;
    private int refreshDelay;


    public Surface(int width, int height, int cellDims, int refreshDelay, GameOfLife _GoL)  {
        this.canvasWidth = width;
        this.canvasHeight = height;
        this.cellDims = cellDims;
        this.GoL = _GoL;
        this.refreshDelay = refreshDelay;
        this.setPreferredSize(new Dimension(this.canvasWidth, this.canvasHeight));
        initTimer();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        DrawCanvas(g, this.GoL);
    }

    private void DrawCanvas(Graphics g, GameOfLife _GoL) {
        Graphics2D g2d = (Graphics2D) g;
        g.setColor(Color.BLACK);
        g.fillRect(0,0, this.canvasWidth, this.canvasHeight);
        for (int i=0;i<_GoL.getCells().length;i++) {
            for (int j=0;j<_GoL.getCells()[0].length;j++) {
                if (_GoL.getCells()[i][j] == 1) {
                    drawCell(g2d, j * this.cellDims, i * this.cellDims, this.cellDims, this.cellDims);
                }
            }
        }
    }

    private void initTimer() {
        this.timer = new Timer(refreshDelay, this);
        timer.start();
    }

    private void drawCell(Graphics g, int x, int y, int width, int height) {
        g.setColor(Color.WHITE); // Main Cell
        g.fillRect(x, y, width, height);
        g.setColor(Color.DARK_GRAY); // Nice Border
        g.drawRect(x, y, width, height);
    }

    private void update() {
        this.GoL.updateCells();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }
}

public class painter extends JFrame {

    private final int canvasWidth = 2560;
    private final int canvasHeight = 1440;
    private int resolution = 160;
    private int fps = 20;
    private int refreshDelay = (int) ((1.0/ fps) * 1000);
    private int[][] curBoardState;
    private int[][][] pastBoardStates;
    private int onFrac = 30;
    private GameOfLife GoL = new GameOfLife(canvasWidth, canvasHeight, resolution, onFrac);
    private int cellDims = canvasWidth / resolution;

    public painter() {
        super("Conway's Game of Life in Java");
        initUI();
    }

    private void initUI() {
        add(new Surface(this.canvasWidth, this.canvasHeight, this.cellDims, this.refreshDelay, this.GoL));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
    }


    public static void main(String[] argv) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                painter test = new painter();
                test.setVisible(true);
            }
        });
    }
}
