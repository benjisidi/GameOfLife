import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;
import javax.swing.Timer;



/*
        ToDo/Plan:
            Figure out why window is slightly misaligned
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
        initTimer();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0,0, this.canvasWidth, this.canvasHeight);
        DrawCanvas(g, this.GoL);
    }

    private void DrawCanvas(Graphics g, GameOfLife _GoL) {
        Graphics2D g2d = (Graphics2D) g;
        for (int i=0;i<_GoL.getCells().length;i++) {
            for (int j=0;j<_GoL.getCells()[0].length;j++) {
                if (_GoL.getCells()[i][j] == 1) {
                    drawCell(g2d, j * cellDims, i * cellDims, cellDims, cellDims);
                }
            }
        }
    }

    private void initTimer() {
        timer = new Timer(refreshDelay, this);
        timer.start();
    }

    private void drawCell(Graphics g, int x, int y, int width, int height) {
        g.setColor(Color.WHITE); // Main Cell
        g.fillRect(x, y, width, height);
        g.setColor(Color.BLACK); // Nice Border
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

    private final int canvasWidth = 800;
    private final int canvasHeight = 600;
    private int resolution = 40; // # of cells in a row ToDo: make this more intelligent to cope with any res
    private int fps = 2;
    private int refreshDelay = (int) ((1.0/ fps) * 1000);
    private int[][] curBoardState;
    private int[][][] pastBoardStates;
    private int onFrac = 30;
    private GameOfLife GoL = new GameOfLife(canvasWidth, canvasHeight, resolution, onFrac);
    private int cellDims = canvasWidth / resolution;

    public painter() {
        System.out.println(this.refreshDelay);
        initUI();
    }

    private void initUI() {
        add(new Surface(this.canvasWidth, this.canvasHeight, this.cellDims, this.refreshDelay, this.GoL));
        setTitle("Conway's Game of Life in Java");
        setSize(this.canvasWidth, this.canvasHeight);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
