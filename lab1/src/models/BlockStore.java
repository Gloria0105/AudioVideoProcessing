package models;

import java.util.Arrays;

public class BlockStore {
    private int size;
    private String storeType;
    private double[][] yuvStore;
    private int[][] rgbStore;
    private int linePos;
    private int columnPos;
    public BlockStore(int size, String storeType,int linePos,int columnPos) {
        this.size = size;
        this.storeType = storeType;
        this.yuvStore = new double[size][size];
        this.rgbStore = new int[size][size];
        this.linePos = linePos;
        this.columnPos = columnPos;
    }

    public int getLinePos() {
        return linePos;
    }

    public void setLinePos(int linePos) {
        this.linePos = linePos;
    }

    public int getColumnPos() {
        return columnPos;
    }

    @Override
    public String toString() {
        return "BlockStore{" +
                "storeType='" + storeType + '\'' +
                ", yuvStore=" + Arrays.toString(yuvStore) +
                ", linePos=" + linePos +
                ", columnPos=" + columnPos +
                '}';
    }

    public void setColumnPos(int columnPos) {
        this.columnPos = columnPos;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getStoreType() {
        return storeType;
    }

    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }

    public double[][] getYuvStore() {
        return yuvStore;
    }

    public void setYuvStore(double[][] yuvStore) {
        this.yuvStore = yuvStore;
    }

    public int[][] getRgbStore() {
        return rgbStore;
    }

    public void setRgbStore(int[][] rgbStore) {
        this.rgbStore = rgbStore;
    }
}
