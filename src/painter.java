import java.awt.*;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.*;
import java.lang.Math;

/*
        ToDo:
            Bug fixing
                None! YAY!
            Add seeding
            Make resolution/cell dims intelligent to cope with any combination - x
            Nicer colour scheme/styling - x
*/

public class painter extends JFrame {

    private int canvasWidth, canvasHeight, resolution, fps, cellDims;
    private GameOfLife GoL;
    private Surface paintSurface;
    private boolean running = false;
    private JLabel statusbar;
    private State status;


    private painter() {
        super("Conway's Game of Life in Java");
        ReadIni options = new ReadIni("options.ini");
        options.readOpts();
        this.canvasWidth = options.width;
        this.canvasHeight = options.height;
        this.resolution = options.resolution;
        this.cellDims = canvasWidth / resolution;
        this.canvasWidth = cellDims * resolution;
        this.canvasHeight = cellDims * (canvasHeight/cellDims);
        this.fps = options.fps;
        GoL = new GameOfLife(canvasWidth, canvasHeight, resolution, options.onFrac);
        initUI();
        this.paintSurface.setBgColor(options.bg);
        this.paintSurface.setCellColor(options.cc);
        this.paintSurface.setGridColor(options.gc);
    }

    private void initUI() {
        statusbar = new JLabel("");
        this.status = new State(canvasWidth, canvasHeight, cellDims, fps, 0, GoL, statusbar);
        status.updateStatusBar();
        add(statusbar, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        paintSurface = new Surface(this.status);
        add(paintSurface);
        addKeyListener(new KeyController());
        MouseController mouseControls = new MouseController();
        this.getContentPane().addMouseListener(mouseControls);
        this.getContentPane().addMouseMotionListener(mouseControls);
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
        }
    }


    private void updateTimer() {
        this.paintSurface.timer.setDelay(this.status.refreshDelay);
    }


    private boolean isRunning() {
        return this.running;
    }

    private void updateBoard() {
        this.paintSurface.update();
    }

    private void stepBack() {
        if (this.status.getDisplayIndex() + 1 < this.GoL.getMemory()) {
            int[][] candidateFrame = this.GoL.getCells()[this.status.getDisplayIndex() + 1];
            if (candidateFrame[0][0] != 999) {
                status.addToIndex(1);
                this.paintSurface.repaint();
            }
        }
    }

    private void stepForward() {
        if (this.status.getDisplayIndex() > 0) {
            this.status.addToIndex(-1);
            this.paintSurface.repaint();
        }
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
        System.arraycopy(stack,this.status.getDisplayIndex(),newStack,0,newStack.length-this.status.getDisplayIndex());
        this.GoL.setCells(newStack);
        this.status.setDisplayIndex(0);
    }

    private void toggleCell(int x, int y) {
        int index = this.status.getDisplayIndex();
        int cellValue = this.GoL.getElement(index, x, y);
        int newValue = Math.abs(cellValue - 1); // Gives 1 if 0, 0 if 1
        this.GoL.setElement(newValue, index, x, y);
    }

    private void setCell(int x, int y, int value) {
        int index = this.status.getDisplayIndex();
        this.GoL.setElement(value, index, x, y);
    }



    class KeyController extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {

            int keycode = e.getKeyCode();
            switch (keycode) {

                case KeyEvent.VK_SPACE:
                    if (painter.this.status.getDisplayIndex() != 0) painter.this.updateStack();
                    painter.this.toggleTimer();
                    break;

                case KeyEvent.VK_RIGHT:
                    if (! painter.this.isRunning() && painter.this.status.getDisplayIndex() == 0) painter.this.updateBoard();
                    else painter.this.stepForward();
                    painter.this.status.incrementGeneration(1);
                    painter.this.status.updateStatusBar();
                    break;

                case KeyEvent.VK_LEFT:
                    if (! painter.this.isRunning()){
                        painter.this.stepBack();
                        painter.this.status.incrementGeneration(-1);
                        painter.this.status.updateStatusBar();
                    }
                    break;

                case KeyEvent.VK_UP:
                    painter.this.status.incrementFPS(1);
                    painter.this.updateTimer();
                    painter.this.status.updateStatusBar();
                    break;

                case KeyEvent.VK_DOWN:
                    painter.this.status.incrementFPS(-1);
                    painter.this.updateTimer();
                    painter.this.status.updateStatusBar();
                    break;

                case KeyEvent.VK_ESCAPE:
                    System.exit(0);
                    break;


                case KeyEvent.VK_R:
                    if (! painter.this.isRunning()) {
                        painter.this.GoL.randomizeCells();
                        painter.this.status.setDisplayIndex(0);
                        painter.this.status.setGeneration(0);
                        painter.this.status.updateStatusBar();
                        painter.this.paintSurface.repaint();
                    }
                    break;

                case KeyEvent.VK_C:
                    if (! painter.this.isRunning()) {
                        painter.this.GoL.clearCells();
                        painter.this.paintSurface.repaint();
                        painter.this.status.setGeneration(0);
                        painter.this.status.updateStatusBar();
                        painter.this.status.setDisplayIndex(0);
                    }
                    break;

                case KeyEvent.VK_OPEN_BRACKET:
                    painter.this.status.GoL.incrementOnFrac(-1);
                    painter.this.status.updateStatusBar();
                    break;

                case KeyEvent.VK_CLOSE_BRACKET:
                    painter.this.status.GoL.incrementOnFrac(1);
                    painter.this.status.updateStatusBar();
                    break;

                case KeyEvent.VK_G:
                    painter.this.paintSurface.toggleGrid();
                    break;
            }
        }
    }


    class MouseController extends MouseInputAdapter {
        boolean mouseActive;
        int paintValue;

        // Register for mouse events on paintSurface
        void MouseController() {
            this.mouseActive=false;
            painter.this.paintSurface.addMouseListener(this);
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        // Function to convert mouse pixel values to array coordinates
        int[] pixelsToCell(int x, int y){
            return new int[] {Math.max(0, (int)Math.ceil((float)x/painter.this.cellDims-1)),
                              Math.max(0, (int)Math.ceil((float)y/painter.this.cellDims-1))};
        }

        public void mousePressed(MouseEvent e) {
            if (!painter.this.isRunning()) {
                this.mouseActive = true;
                int x = e.getX();
                int y = e.getY();
                if (x >= 0 && x < painter.this.canvasWidth && y >= 0 && y < painter.this.canvasHeight) {
                    int[] cellClicked = pixelsToCell(x, y);
                    int index = painter.this.status.getDisplayIndex();
                    int clickedValue = painter.this.GoL.getElement(index, cellClicked[0], cellClicked[1]);
                    int toggledValue = Math.abs(clickedValue - 1);
                    painter.this.setCell(cellClicked[0], cellClicked[1], toggledValue);
                    this.paintValue = toggledValue;
                    painter.this.paintSurface.repaint();
                }
            }
        }

        public void mouseReleased(MouseEvent e) {
            if (!painter.this.isRunning()) {
                this.mouseActive = false;
                int x = e.getX();
                int y = e.getY();
                if (x >= 0 && x < painter.this.canvasWidth && y >= 0 && y < painter.this.canvasHeight) {
                    int index = painter.this.status.getDisplayIndex();
                    int[][] frame = painter.this.GoL.getCells()[index];
                    painter.this.GoL.newStackFromFrame(frame);
                    painter.this.status.setDisplayIndex(0);
                    painter.this.status.setGeneration(0);
                    painter.this.paintSurface.repaint();
                }
            }
        }

        public void mouseDragged(MouseEvent e) {
            if (!painter.this.isRunning()) {
                int x = e.getX();
                int y = e.getY();
                if (x >= 0 && x < painter.this.canvasWidth && y >= 0 && y < painter.this.canvasHeight) {
                    int[] cellClicked = pixelsToCell(x, y);
                    painter.this.setCell(cellClicked[0], cellClicked[1], this.paintValue);
                    painter.this.paintSurface.repaint();
                }
            }
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
