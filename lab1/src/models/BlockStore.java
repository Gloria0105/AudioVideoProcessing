package models;

import java.util.Arrays;

public class BlockStore {
    private int size;
    private String storeType;
    private double[][] store;
    private int[][] GStore;

    public BlockStore(int size, String storeType) {
        this.size = size;
        this.storeType = storeType;
        this.store = new double[size][size];
        this.GStore = new int[size][size];

    }



    @Override
    public String toString() {
        return "BlockStore{" +
                "storeType='" + storeType + '\'' +
                ", store=" + Arrays.toString(store) +

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

    public double[][] getStore() {
        return store;
    }

    public void setStore(double[][] store) {
        this.store = store;
    }

    public int[][] getGStore() {
        return GStore;
    }

    public void setGStore(int[][] GStore) {
        this.GStore = GStore;
    }
}
