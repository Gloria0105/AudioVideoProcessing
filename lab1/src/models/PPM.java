package models;

import java.io.*;
import java.util.Objects;

public class PPM {
    private String fileName;
    private int width;
    private int height;
    private int maxValue;
    private String format;
    private String line;
    private int[][] r;
    private int[][] g;
    private int[][] b;
    private double[][] y;
    private double[][] u;
    private double[][] v;

    public PPM(PPM ppm) {
        this.fileName = ppm.fileName;
        this.width = ppm.width;
        this.height = ppm.height;
        this.maxValue = ppm.maxValue;
        this.format = ppm.format;
        this.line = ppm.line;
        this.r = ppm.r;
        this.g = ppm.g;
        this.b = ppm.b;
        this.y = ppm.y;
        this.u = ppm.u;
        this.v = ppm.v;
    }
    public PPM(String fileName,int width,int height,int maxValue,String format) {
        this.fileName = fileName;
        this.width = width;
        this.height = height;
        this.maxValue = maxValue;
        this.format = format;
    }
    public PPM(String fileName) {
        this.fileName = fileName;
        readPPM(fileName);
        convertToYUV();
    }

    private void convertToYUV() {
        for (int line = 0; line < height; line++)
            for (int column = 0; column < width; column++) {
                y[line][column] = 0.299 * r[line][column] + 0.587 * g[line][column] + 0.114 * b[line][column];
                u[line][column] = 128 - 0.168736 * r[line][column] - 0.331264 * g[line][column] + 0.5 * b[line][column];
                v[line][column] = 128 + 0.5 * r[line][column] - 0.418688 * g[line][column] - 0.081312 * b[line][column];
            }
    }

    public PPM convertToRGB() {
        PPM newPPM = new PPM(this);
        for (int line = 0; line < height; line++)
            for (int column = 0; column < width; column++) {
                Double R = y[line][column] + 1.4019 * (v[line][column] - 128);
                Double G = y[line][column] - 0.344136 * (u[line][column] - 128) - 0.714136 * (v[line][column] - 128);
                Double B = y[line][column] + 1.7790 * (u[line][column] - 128);

                if (R > 255) R = 255.0;
                if (G > 255) G = 255.0;
                if (B > 255) B = 255.0;

                if (R < 0) R = 0.0;
                if (G < 0) G = 0.0;
                if (B < 0) B = 0.0;
                newPPM.r[line][column] = R.intValue();
                newPPM.g[line][column] = G.intValue();
                newPPM.b[line][column] = B.intValue();

            }
        return newPPM;
    }

    public void writePPM(String filename) throws IOException {
        FileWriter fileWriter = new FileWriter("./data/" + filename);
        PrintWriter printWriter = new PrintWriter(fileWriter);

        printWriter.println(format);
        printWriter.println(line);
        printWriter.println(height + " " + width);
        printWriter.println(maxValue);
        for (int line = 0; line < height; line++) {
            for (int column = 0; column < width; column++) {
                printWriter.println(this.r[line][column]);
                printWriter.println(this.g[line][column]);
                printWriter.println(this.b[line][column]);
            }
        }

        printWriter.close();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int[][] getR() {
        return r;
    }

    public void setR(int[][] r) {
        this.r = r;
    }

    public int[][] getG() {
        return g;
    }

    public void setG(int[][] g) {
        this.g = g;
    }

    public int[][] getB() {
        return b;
    }

    public void setB(int[][] b) {
        this.b = b;
    }

    public double[][] getY() {
        return y;
    }

    public void setY(double[][] y) {
        this.y = y;
    }

    public double[][] getU() {
        return u;
    }

    public void setU(double[][] u) {
        this.u = u;
    }

    public double[][] getV() {
        return v;
    }

    public void setV(double[][] v) {
        this.v = v;
    }

    private void readPPM(String filename) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }

        String st;
        try {
            format = Objects.requireNonNull(br).readLine();
            line=br.readLine();
            String thirdLine = br.readLine();
            height = Integer.parseInt(thirdLine.split(" ")[1]);
            width = Integer.parseInt(thirdLine.split(" ")[0]);
            maxValue = Integer.parseInt(br.readLine());
            r = new int[height][width];
            g = new int[height][width];
            b = new int[height][width];

            y = new double[height][width];
            u = new double[height][width];
            v = new double[height][width];


            for (int i = 0; i < height; i++)
                for (int j = 0; j < width; j++) {
                    r[i][j] = Integer.parseInt(br.readLine());
                    g[i][j] = Integer.parseInt(br.readLine());
                    b[i][j] = Integer.parseInt(br.readLine());
                }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
