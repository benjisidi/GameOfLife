import javax.swing.*;

public class State {
    final int canvasWidth;
    final int canvasHeight;
    int refreshDelay;
    int cellDims;
    int displayIndex;
    int fps;
    GameOfLife GoL;
    JLabel statusBar;
    int generation;

    State(int width, int height, int cellDims, int fps, int displayIndex, GameOfLife _GoL, JLabel statusbar) {
        this.canvasWidth = width;
        this.canvasHeight = height;
        this.cellDims = cellDims;
        this.refreshDelay = (int) ((1.0/ fps) * 1000);
        this.fps = fps;
        this.displayIndex = displayIndex;
        this.GoL = _GoL;
        this.statusBar = statusbar;
        this.generation = 0;
    }

    void setDisplayIndex(int i){
        this.displayIndex = i;
    }

    int getDisplayIndex() {
        return this.displayIndex;
    }

    void addToIndex(int i) {
        this.displayIndex += i;
    }

    int getFPS() {
        return this.fps;
    }

    void setFPS(int fps) {
        if (fps <= 0) fps = 1;
        this.fps = fps;
        this.refreshDelay = (int) ((1.0/ fps) * 1000);
    }

    void incrementFPS(int i) {
        this.fps += i;
        if (fps <= 0) fps = 1;
        this.refreshDelay = (int) ((1.0/ this.fps) * 1000);
    }

    void incrementGeneration(int i){
        this.generation += i;
        if (this.generation < 0) this.generation = 0;
    }

    void setGeneration(int i){
        this.generation = i;
    }

    void updateStatusBar() {
        String statusString = String.format("    Generation: %d    Framerate: %d    Starting live percentage: %.0f%%",
                this.generation, this.getFPS(), this.GoL.getOnFrac());
        this.statusBar.setText(statusString);
    }
}