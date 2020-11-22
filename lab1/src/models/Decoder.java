package models;

import java.util.List;

public class Decoder {

    private Encoder encoder;

    private List<BlockStore> encodedY;
    private List<BlockStore> encodedU;
    private List<BlockStore> encodedV;

    private double[][] Q = {
            {6, 4, 4, 6, 10, 16, 20, 24},
            {5, 5, 6, 8, 10, 23, 24, 22},
            {6, 5, 6, 10, 16, 23, 28, 22},
            {6, 7, 9, 12, 20, 35, 32, 25},
            {7, 9, 15, 22, 27, 44, 41, 31},
            {10, 14, 22, 26, 32, 42, 45, 37},
            {20, 26, 31, 35, 41, 48, 48, 40},
            {29, 37, 38, 39, 45, 40, 41, 40}
    };

    public Decoder(Encoder encoder) {
        this.encoder = encoder;
        encodedY = encoder.getEncodedY();
        encodedU = encoder.getEncodedU();
        encodedV = encoder.getEncodedV();


        deQuantizationPhase(encodedY);
        deQuantizationPhase(encodedU);
        deQuantizationPhase(encodedV);

        inverseDCT(encodedY);
        inverseDCT(encodedU);
        inverseDCT(encodedV);

        addValue(encodedY);
        addValue(encodedU);
        addValue(encodedV);

        encoder.getImage().setY(decodeBlock(encodedY));
        encoder.getImage().setU(decodeBlock(encodedU));
        encoder.getImage().setV(decodeBlock(encodedV));
    }


    public List<BlockStore> getEncodedY() {
        return encodedY;
    }

    public void setEncodedY(List<BlockStore> encodedY) {
        this.encodedY = encodedY;
    }

    public List<BlockStore> getEncodedU() {
        return encodedU;
    }

    public void setEncodedU(List<BlockStore> encodedU) {
        this.encodedU = encodedU;
    }

    public List<BlockStore> getEncodedV() {
        return encodedV;
    }

    public void setEncodedV(List<BlockStore> encodedV) {
        this.encodedV = encodedV;
    }


    private double[][] decodeBlock(List<BlockStore> encoded) {
        double[][] matrix = new double[encoder.getImage().getHeight()][encoder.getImage().getWidth()];

        int line = 0;
        int column = 0;

        for (BlockStore blockStore : encoded) {
            for (int i = 0; i < 8; i++)
                for (int j = 0; j < 8; j++)
                    matrix[line + i][column + j] = blockStore.getGStore()[i][j];
            column += 8;
            if (column == encoder.getImage().getWidth()) {
                line += 8;
                column = 0;
            }
        }

        return matrix;
    }

    public PPM getImage() {
        return encoder.getImage();
    }


    // Lab 2 functions

    private int[][] multiplyMatrixes(int[][] G, double[][] Q) {
        int[][] result = new int[8][8];

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                result[i][j] = (int) (G[i][j] * Q[i][j]);

        return result;
    }

    private void deQuantizationPhase(List<BlockStore> encoded) {
        for (BlockStore block : encoded)
            block.setGStore(this.multiplyMatrixes(block.getGStore(), this.Q));
    }

    private void inverseDCT(List<BlockStore> encoded) {
        for (BlockStore block : encoded)
            block.setGStore(iDCT(block.getGStore()));
    }

    int[][] iDCT(int[][] matrix) {
        int[][] f = new int[8][8];
        double constant = 0.25;
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++) {
                f[x][y] = (int) (constant * sum(matrix, x, y));
            }

        return f;
    }

    private double sum(int[][] matrix, int x, int y) {
        double sum = 0.0;
        for (int u = 0; u < 8; u++) {
            for (int v = 0; v < 8; v++) {
                sum += alpha(u) * alpha(v) * matrix[u][v] * Math.cos(((2 * x + 1) * u * Math.PI) / 16)
                        * Math.cos(((2 * y + 1) * v * Math.PI) / 16);
            }
        }
        return sum;
    }

    private double alpha(int value) {
        return value > 0 ? 1 : (1 / Math.sqrt(2.0));
    }

    private void addValue(List<BlockStore> encoded) {
        for (BlockStore blockStore : encoded) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    blockStore.getGStore()[i][j] += 128.0;
                }
            }
        }
    }


}
