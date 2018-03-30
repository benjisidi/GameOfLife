
import java.util.Arrays;
import java.util.Random;

public class GameOfLife {
    // Declare set-up variables
    private int canvasWidth;
    private int canvasHeight;
    private int resolution;
    private int cellDims;
    private int nRows;
    private int nCols;
    private float onFrac;
    private int[][] cells;

    public GameOfLife(int width, int height, int res, int frac) {

        this.canvasWidth = width;
        this.canvasHeight = height;
        this.resolution = res;
        this.cellDims = canvasWidth / resolution;
        this.nRows = canvasHeight / cellDims;
        this.nCols = resolution;
        this.onFrac = frac;
        this.cells = new int[nRows][nCols];

        // Initialize cells with random selection turned 'on'
        Random cellChecker = new Random();
        for (int col=0; (col < nCols); col++) {
            for (int row = 0; (row < nRows); row++) {
                int checkN = cellChecker.nextInt(100);
                if (checkN < onFrac) {
                    this.cells[row][col] = 1;
                }
                else {
                    this.cells[row][col] = 0;
                }
            }
        }
    }


    public int[][] getCells(){
        return this.cells;
    }

    private int countNeighbours(int col, int row) {
        int count = 0;
        for (int x = col-1; x<col+2; x++) {
            for (int y = row-1; y<row+2; y++) {
                if (x>=0 && x<nCols && y>=0 && y<nRows && !(y == row && x == col)) {
                    if (this.cells[y][x] == 1 ) {
                        count ++;
                    }
                }
            }
        }
        return count;
    }

    public void updateCells() {
        int[][] dummy = new int[nRows][nCols];
        for (int col=0; (col < nCols); col++) {
            for (int row = 0; (row < nRows); row++) {
                int neighbours = countNeighbours(col, row);
                boolean live = (this.cells[row][col] != 0);
                if (live) {
                    if (neighbours < 2 || neighbours > 3) {
                        dummy[row][col] = 0; // Dies by starvation or overpopulation
                    }
                    else {
                        dummy[row][col] = 1;
                    }
                }
                else{
                    if (neighbours == 3) {
                        dummy[row][col] = 1; // Cell comes to life
                    }
                }
            }
        }
        this.cells = dummy;
    }


    private void printRaw(){
        String outstr = Arrays.deepToString(this.cells).replace("], ", "]\n");
        outstr = outstr.replace("[[", "[");
        outstr = outstr.replace("]]", "]");
        System.out.println(outstr);
    }

    public static void main(String argv[]) {
        GameOfLife g = new GameOfLife(100, 100, 10, 30);
        for (int i=0;i<10;i++){
            System.out.println("--------------------------Generation " + i + "--------------------------");
            g.printRaw();
            g.updateCells();
        }
    }
}
