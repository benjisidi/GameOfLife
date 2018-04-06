import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.*;
import java.util.ArrayList;
import java.lang.Math;

/*
        ToDo:
            Bug fixing
                Fix "new future" behaviour
                Fix stepping breaking FPS

            Add seeding
            Add controls:
                sp  play/pause
                c   clear board
                g   show/hide grid
                m1  toggle cell
                []  increment/decrement random %
                r   randomise board
                LR  step forward back (remember 10/100/1000 states)
                UD  control fps - x
            Add status bar with rate, generation etc
            Add mouse control for toggling cells
            Make resolution/cell dims intelligent to cope with any combination
            Nicer colour scheme/styling
*/


class Surface extends JPanel implements ActionListener {
    Timer timer;
    private state status;

    Surface(state status)  {
        this.status = status;
        this.setPreferredSize(new Dimension(this.status.canvasWidth, this.status.canvasHeight));
        initTimer();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        DrawCanvas(g, this.status.GoL);
    }

    private void DrawCanvas(Graphics g, GameOfLife _GoL) {
        Graphics2D g2d = (Graphics2D) g;
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0,0, this.status.canvasWidth, this.status.canvasHeight);
        for (int i=0;i<_GoL.getCells()[0].length;i++) {
            for (int j=0;j<_GoL.getCells()[0][0].length;j++) {
                if (_GoL.getCells()[this.status.displayIndex][i][j] == 1) {
                    drawCell(g2d, j * this.status.cellDims, i * this.status.cellDims,
                                                this.status.cellDims, this.status.cellDims);
                }
            }
        }
    }

    private void initTimer() {
        this.timer = new Timer(this.status.refreshDelay, this);
        //timer.start();
    }


    private void drawCell(Graphics g, int x, int y, int width, int height) {
        g.setColor(Color.WHITE); // Main Cell
        g.fillRect(x, y, width, height);
        g.setColor(Color.DARK_GRAY); // Nice Border
        g.drawRect(x, y, width, height);
    }

    void update() {
        this.status.GoL.updateCells();
        repaint();
    }

    void setIndex(int index) {
        this.status.displayIndex = index;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
    }
}

class state {
    final int canvasWidth;
    final int canvasHeight;
    int refreshDelay;
    int cellDims;
    int displayIndex;
    GameOfLife GoL;


    state(int width, int height, int cellDims, int refreshDelay, int displayIndex, GameOfLife _GoL) {
        this.canvasWidth = width;
        this.canvasHeight = height;
        this.cellDims = cellDims;
        this.refreshDelay = refreshDelay;
        this.displayIndex = displayIndex;
        this.GoL = _GoL;
    }

    void setDisplayIndex(int i){
        this.displayIndex = i;
    }
}

public class painter extends JFrame {

    private final int canvasWidth = 2560;
    private final int canvasHeight = 1440;
    private int resolution = 160;
    private int fps = 20;
    private int refreshDelay = (int) ((1.0/ fps) * 1000);
    private int onFrac = 15;
    private GameOfLife GoL = new GameOfLife(canvasWidth, canvasHeight, resolution, onFrac);
    private int cellDims = canvasWidth / resolution;
    private Surface paintSurface;
    private boolean running = false;
    private int displayIndex = 0;
    private state status = new state(canvasWidth, canvasHeight, cellDims, refreshDelay, displayIndex, GoL);



    private painter() {
        super("Conway's Game of Life in Java");
        initUI();
    }

    private void initUI() {
        paintSurface = new Surface(this.status);
        add(paintSurface);
        addKeyListener(new KeyController());
        MouseController mouseControls = new MouseController();
        addMouseListener(mouseControls);
        addMouseMotionListener(mouseControls);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
    }


    private void toggleTimer() {
        if (this.paintSurface.timer.isRunning()) {
            this.paintSurface.timer.stop();
            this.running = false;
        }
        else {
            this.paintSurface.timer.start();
            this.running = true;
            this.paintSurface.setIndex(this.displayIndex);
        }

    }


    private void newBranch() {
        int [][][] currCells = this.GoL.getCells();
        int cellDimsM = currCells[0].length;
        int cellDimsX = currCells[1].length;
        int cellDimsY = currCells[2].length;
        int[][][] dummyMem = new int[cellDimsM][cellDimsX][cellDimsY];

        System.arraycopy(currCells, this.displayIndex, dummyMem,
                this.displayIndex, cellDimsM - this.displayIndex);
        this.GoL.setCells(dummyMem);
    }

    private boolean isRunning() {
        return this.running;
    }

    private void updateBoard() {
        this.paintSurface.update();
    }

    private void stepBack() {
        if (this.displayIndex + 1 < this.GoL.getMemory()) {
            int[][] candidateFrame = this.GoL.getCells()[this.displayIndex + 1];
            if (candidateFrame[0][0] != 999) {
                this.displayIndex += 1;//
                this.paintSurface.setIndex(this.displayIndex);
                this.paintSurface.repaint();
            }
        }
        System.out.print(this.displayIndex + " ");
    }

    private void stepForward() {
        if (this.displayIndex > 0) {
            this.displayIndex -= 1;
            this.paintSurface.setIndex(this.displayIndex);
            this.paintSurface.repaint();
        }
        System.out.print(this.displayIndex + " ");
    }

    private void setFPS (int val) {
        if (val <= 0) val = 1;
        this.fps = val;
        this.refreshDelay =  (int) ((1.0/ this.fps) * 1000);
        this.paintSurface.timer.setDelay(refreshDelay);
    }

    private void updateStack() {
        int[][][] stack = this.GoL.getCells();
        int[][][] newStack = new int[stack.length][stack[0].length][stack[0][0].length];
        // Initialize unused frames with 999 so they can be identified.
        for (int i = 0; i < stack.length; i++) {
            for (int col = 0; (col < stack[0][0].length); col++) {
                for (int row = 0; (row < stack[0].length); row++) {
                    newStack[i][row][col] = 999;
                }
            }
        }
        System.arraycopy(stack,this.displayIndex,newStack,0,newStack.length-this.displayIndex);
        this.GoL.setCells(newStack);
        this.displayIndex = 0;
    }//ToDo: finish writing this

    class KeyController extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {

            int keycode = e.getKeyCode();
            switch (keycode) {

                case KeyEvent.VK_SPACE:
                    if (painter.this.displayIndex != 0) painter.this.updateStack();
                    painter.this.toggleTimer();
                    break;

                case KeyEvent.VK_RIGHT:
                    if (! painter.this.isRunning() && painter.this.displayIndex == 0) painter.this.updateBoard();
                    else painter.this.stepForward();
                    break;

                case KeyEvent.VK_LEFT:
                    if (! painter.this.isRunning()) painter.this.stepBack();
                    break;

                case KeyEvent.VK_UP:
                    setFPS(painter.this.fps + 1);
                    break;

                case KeyEvent.VK_DOWN:
                    setFPS(painter.this.fps - 1);
                    break;

                case KeyEvent.VK_ESCAPE:
                    System.exit(0);
                    break;


                case KeyEvent.VK_R:
                    if (! painter.this.isRunning()) {
                        painter.this.GoL.randomizeCells();
                        painter.this.displayIndex = 0;
                        painter.this.paintSurface.repaint();
                    }
                    break;

                case KeyEvent.VK_C:
                    if (! painter.this.isRunning()) {
                        painter.this.GoL.clearCells();
                        painter.this.paintSurface.repaint();
                        painter.this.displayIndex = 0;
                    }
                    break;
            }
        }
    }


    class MouseController extends MouseInputAdapter {
        boolean mouseInWindow;
        boolean mouseActive;
        ArrayList visited;
        // Register for mouse events on paintSurface
        void MouseController() {
            this.mouseInWindow = false;
            this.mouseActive=false;
            painter.this.paintSurface.addMouseListener(this);
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        // Function to convert mouse pixel values to array coordinates
        int[] pixelsToCell(int x, int y){
            return new int[] {(int)Math.ceil((float)x/painter.this.cellDims), (int)Math.ceil((float)y/painter.this.cellDims)};
        }

        public void mouseEntered(MouseEvent e) {
            this.mouseInWindow = true;
            System.out.println("Mouse entered window");
        }

        public void mouseExited(MouseEvent e) {
            this.mouseInWindow = false;
            System.out.println("Mouse exited window");
        }

        public void mousePressed(MouseEvent e) {
            this.mouseActive = true;
            System.out.println("Mouse pressed");
        }

        public void mouseReleased(MouseEvent e) {
            this.mouseActive = false;
            System.out.println("Mouse released");
        }

        public void mouseClicked(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            int[] coords = pixelsToCell(x, y);
            System.out.printf("\nMouse clicked.\nCoordinates are %d, %d\nSquare side Length is %d", x, y, painter.this.cellDims);
            System.out.printf("\nCalculated coordinates are %d, %d", coords[0], coords[1]);
        }

        public void mouseDragged(MouseEvent e) {
            System.out.println("Mouse is being dragged");
        }
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
