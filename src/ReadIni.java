import java.awt.*;
import java.util.Properties;
import java.io.*;

/*
Properties:
    -Bg Colour (R, G, B)
    -Fg Colour (R, G, B)
    -Grid Colour (R, G, B)
    -onFrac
    -Window Width
    -Window Height
    -Fps
*/

public class ReadIni {
    private String fileName;
    Color cc, bg, gc;
    int width, height, onFrac, fps, resolution;

    ReadIni(String x) {
        this.fileName = x;
        assertIni();
        readOpts();
    }

    private Color strToColour(String s) {
        String[] stringArray = s.split(",");
        int[] intArray = new int[stringArray.length];
        int i = 0;
        for (String str:stringArray) {
            intArray[i] = Integer.parseInt(str.trim());
            i++;
        }
        Color out = new Color(intArray[0], intArray[1], intArray[2]);
        return out;
    }

    void readOpts() {
        Properties p = new Properties();
        try {
            p.load(new FileInputStream(this.fileName));
            this.cc = strToColour(p.getProperty("CellColour","255,255,255"));
            this.bg = strToColour(p.getProperty("BackgroundColour","102,102,102"));
            this.gc = strToColour(p.getProperty("GridColour","204,204,204"));
            this.onFrac = Integer.valueOf(p.getProperty("InitialOnPercentage","15"));
            this.width = Integer.valueOf(p.getProperty("WindowWidth","1280"));
            this.height = Integer.valueOf(p.getProperty("WindowHeight","720"));
            this.fps = Integer.valueOf(p.getProperty("fps", "30"));
            this.resolution = Integer.valueOf(p.getProperty("Resolution", "20"));
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    private void printOpts() {  // Used for debugging
        System.out.println(this.cc);
    }

    public static void main(String[] argv) {
        ReadIni test = new ReadIni("options.ini");
        test.printOpts();
    }

    private void assertIni() {
        File file = new File(this.fileName);
        if (!file.exists() || file.isDirectory()) {
            String[] iniContents = new String[] {
        "! This is the options file for the Game of Life. Here you can set various parameters that affect how it looks",
        "! and runs. The options take the following format:",
        "!",
        "! CellColour/BackgroundColour/GridColour - These should all be integer values separated by commas denoting an RGB colour.",
        "! InitialOnPercentage - an integer between 0 and 100 inclusive denoting the proportion of cells to be activated when",
        "!                       the grid is randomly generated.",
        "! WindowWidth/WindowHeight - integers denoting the window size in pixels.",
        "! Resolution - Integer denoting the number of cells in each row.",
        "! fps - Integer denoting how many times per second the simualation should be updated.",
        "!",
        "CellColour=255,255,255",
        "BackgroundColour=102,102,102",
        "GridColour=204,204,204",
        "InitialOnPercentage=15",
        "WindowWidth=1280",
        "WindowHeight=720",
        "Resolution=60",
        "fps=20"};
            try {
                FileWriter writer = new FileWriter(this.fileName);
                for (String line: iniContents){
                    writer.write(line);
                    writer.write(System.lineSeparator());
                }
                writer.close();
            }
            catch (Exception e) {
                System.out.println(e);
            }
        }

    }

}
