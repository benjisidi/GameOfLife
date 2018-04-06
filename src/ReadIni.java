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

    public void readOpts() {
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

    public void printOpts() {
        System.out.println(this.cc);
    }

    public static void main(String[] argv) {
        ReadIni test = new ReadIni("options.ini");
        test.printOpts();
    }

}
