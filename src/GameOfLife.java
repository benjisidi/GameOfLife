
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
    private int[][][] cells;
    private int memory = 100;

    public GameOfLife(int width, int height, int res, int frac) {

        this.canvasWidth = width;
        this.canvasHeight = height;
        this.resolution = res;
        this.cellDims = canvasWidth / resolution;
        this.nRows = canvasHeight / cellDims;
        this.nCols = resolution;
        this.onFrac = frac;
        this.cells = new int[memory][nRows][nCols];

        // Initialize cells with random selection turned 'on'
        Random cellChecker = new Random();
        for (int col=0; (col < nCols); col++) {
            for (int row = 0; (row < nRows); row++) {
                int checkN = cellChecker.nextInt(100);
                if (checkN < onFrac) {
                    this.cells[0][row][col] = 1;
                }
                else {
                    this.cells[0][row][col] = 0;
                }
            }
        }
    }


    int getMemory() {
        return this.memory;
    }

    int[][][] getCells(){
        return this.cells;
    }

    void setCells(int [][][] newCells) {this.cells = newCells; }

    public void clearBoard() {
        this.cells = new int[memory][nRows][nCols];
    }

    private int countNeighbours(int col, int row) {
        int count = 0;
        for (int x = col-1; x<col+2; x++) {
            for (int y = row-1; y<row+2; y++) {
                if (x>=0 && x<nCols && y>=0 && y<nRows && !(y == row && x == col)) {
                    if (this.cells[0][y][x] == 1 ) {
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
                boolean live = (this.cells[0][row][col] != 0);
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

        // Shift everything down one
        for (int i=this.memory-1; i>0; i--){
            this.cells[i] = this.cells[i-1];
        }
        // Replace top frame with dummy
        this.cells[0] = dummy;
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
