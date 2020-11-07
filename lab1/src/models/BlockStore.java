package models;

import java.util.Arrays;

public class BlockStore {
    private int size;
    private String storeType;
    private double[][] yuvStore;
    private int[][] rgbStore;

    public BlockStore(int size, String storeType) {
        this.size = size;
        this.storeType = storeType;
        this.yuvStore = new double[size][size];
        this.rgbStore = new int[size][size];

    }



    @Override
    public String toString() {
        return "BlockStore{" +
                "storeType='" + storeType + '\'' +
                ", yuvStore=" + Arrays.toString(yuvStore) +

                '}';
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
