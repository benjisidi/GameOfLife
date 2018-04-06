import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Surface extends JPanel implements ActionListener {
    Timer timer;
    private State status;
    private Color bgColor; // to use RGB: Color myWhite = new Color(255, 255, 255);
    private Color gridColor;
    private Color cellColor;
    private Color gridPaintColor;

    Surface(State status)  {
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
        g.setColor(bgColor);
        g.fillRect(0,0, this.status.canvasWidth, this.status.canvasHeight);
        for (int i=0;i<_GoL.getCells()[0].length;i++) {
            for (int j=0;j<_GoL.getCells()[0][0].length;j++) {
                if (_GoL.getCells()[this.status.displayIndex][i][j] == 1) {
                    drawCell(g2d, j * this.status.cellDims, i * this.status.cellDims,
                            this.status.cellDims, this.status.cellDims);
                }
                drawGrid(g2d, j * this.status.cellDims, i * this.status.cellDims,
                        this.status.cellDims, this.status.cellDims);
            }
        }
    }

    private void initTimer() {
        this.timer = new Timer(this.status.refreshDelay, this);
        //timer.start();
    }


    private void drawCell(Graphics g, int x, int y, int width, int height) {
        g.setColor(cellColor); // Main Cell
        g.fillRect(x, y, width, height);

    }

    private void drawGrid(Graphics g, int x, int y, int width, int height) {
        g.setColor(gridPaintColor); // Nice Border if gc not visible, otherwise gc.
        g.drawRect(x, y, width, height);
    }

    void update() {
        this.status.GoL.updateCells();
        this.status.incrementGeneration(1);
        this.status.updateStatusBar();
        repaint();
    }

    void setIndex(int index) {
        this.status.displayIndex = index;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
    }

    void toggleGrid() {
      if (gridPaintColor == gridColor) gridPaintColor = bgColor;
      else gridPaintColor = gridColor;
      repaint();
    }

    void setCellColor(Color c) {
        this.cellColor = c;
    }

    void setBgColor(Color c) {
        this.bgColor = c;
    }

    void setGridColor(Color c) {
        this.gridColor = c;
        this.gridPaintColor = c;
    }
}