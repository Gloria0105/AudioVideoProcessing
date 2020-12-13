package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Encoder {
    private PPM image;
    private List<BlockStore> encodedY;
    private List<BlockStore> encodedU;
    private List<BlockStore> encodedV;
    private HashMap<Integer, List<Integer>> amplitudes = new HashMap<>();
    List<Integer> entropy = new ArrayList<>();

    public List<Integer> getEntropy() {
        return entropy;
    }

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

    public Encoder(PPM image) {
        this.image = image;
        encodedY = splitInBlocks(image, "Y", image.getY());
        encodedU = splitInBlocks(image, "U", image.getU());
        encodedV = splitInBlocks(image, "V", image.getV());

        encodedU = getListResized(encodedU);
        encodedV = getListResized(encodedV);

        substractValue(encodedY);
        substractValue(encodedU);
        substractValue(encodedV);

        forwardDCT(encodedY);
        forwardDCT(encodedU);
        forwardDCT(encodedV);

        quantizationPhase(encodedY);
        quantizationPhase(encodedU);
        quantizationPhase(encodedV);

        amplitudes.put(1, Arrays.asList(-1, 1));
        amplitudes.put(2, Arrays.asList(2, 3));
        amplitudes.put(3, Arrays.asList(4, 7));
        amplitudes.put(4, Arrays.asList(8, 15));
        amplitudes.put(5, Arrays.asList(16, 31));
        amplitudes.put(6, Arrays.asList(32, 63));
        amplitudes.put(7, Arrays.asList(64, 127));
        amplitudes.put(8, Arrays.asList(128, 255));
        amplitudes.put(9, Arrays.asList(256, 511));
        amplitudes.put(10, Arrays.asList(512, 1023));


        for (int i = 0; i < encodedY.size(); i++) {
            addEntropy(encodedY.get(i).getGStore());
            addEntropy(encodedU.get(i).getGStore());
            addEntropy(encodedV.get(i).getGStore());
        }

    }


    public void printMatrix(List<BlockStore> encoded) {
        for (BlockStore block : encoded) {
            System.out.println(block);
        }
    }

    public PPM getImage() {
        return image;
    }

    public void setImage(PPM image) {
        this.image = image;
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

    static List<BlockStore> splitInBlocks(PPM image, String type, double[][] matrix) {
        List<BlockStore> encoded = new ArrayList<>();

        for (int i = 0; i < image.getHeight(); i += 8)
            for (int j = 0; j < image.getWidth(); j += 8) {

                BlockStore store = subMatrix(type, i, j, matrix);
                if (type.equals("Y"))
                    encoded.add(store);
                else
                    encoded.add(average4Block(store));
            }

        return encoded;
    }

    private static BlockStore subMatrix(String type, int i_pos, int j_pos, double[][] matrix) {
        BlockStore store = new BlockStore(8, type);

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                store.getStore()[i][j] = matrix[i + i_pos][j + j_pos];

        return store;
    }

    private static BlockStore average4Block(BlockStore toSample) {
        BlockStore sampleStore = new BlockStore(4, toSample.getStoreType());
        int line = 0;
        int column = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {

                sampleStore.getStore()[i][j] = (toSample.getStore()[line][column] +
                        toSample.getStore()[line][column + 1] +
                        toSample.getStore()[line + 1][column] +
                        toSample.getStore()[line + 1][column + 1])
                        / 4;
                column += 2;
            }
            line += 2;
            column = 0;
        }
        return sampleStore;
    }

    public List<BlockStore> getListResized(List<BlockStore> encoded) {
        List<BlockStore> resized = new ArrayList<>();
        encoded.forEach(b -> resized.add(resizeBlock(b)));
        return resized;
    }

    private static BlockStore resizeBlock(BlockStore blockStore) {
        BlockStore sampleStore = new BlockStore(8, blockStore.getStoreType());
        int line = 0;
        int column = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                double value = blockStore.getStore()[i][j];
                sampleStore.getStore()[line][column] = value;
                sampleStore.getStore()[line][column + 1] = value;
                sampleStore.getStore()[line + 1][column] = value;
                sampleStore.getStore()[line + 1][column + 1] = value;
                column += 2;
            }
            line += 2;
            column = 0;
        }

        return sampleStore;
    }

    // Lab 2 functions


    private void substractValue(List<BlockStore> encoded) {
        for (BlockStore blockStore : encoded) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    blockStore.getStore()[i][j] -= 128.0;
                }
            }
        }
    }

    private void forwardDCT(List<BlockStore> encoded) {
        for (BlockStore block : encoded)
            block.setGStore(fDCT(block.getStore()));
    }

    int[][] fDCT(double[][] matrix) {
        int[][] G = new int[8][8];
        double constant = 0.25;
        for (int u = 0; u < 8; u++)
            for (int v = 0; v < 8; v++) {
                G[u][v] = (int) (constant * alpha(u) * alpha(v) * sum(matrix, u, v));
            }

        return G;
    }

    private double sum(double[][] matrix, int u, int v) {
        double sum = 0.0;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                sum += matrix[x][y] * Math.cos(((2 * x + 1) * u * Math.PI) / 16) * Math.cos(((2 * y + 1) * v * Math.PI) / 16);
            }
        }
        return sum;
    }

    private double alpha(int value) {
        return value > 0 ? 1 : (1 / Math.sqrt(2.0));
    }


    private int[][] divideMatrixes(int[][] G, double[][] Q) {
        int[][] result = new int[8][8];
        double aux;
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                aux = G[i][j] / Q[i][j];
//                if (aux < 0)
//                    result[i][j] = (int) Math.ceil(aux);
//                else
//                    result[i][j] = (int) Math.floor(aux);
                result[i][j] = (int) aux;
            }

        return result;
    }

    private void quantizationPhase(List<BlockStore> encoded) {
        for (BlockStore block : encoded)
            block.setGStore(this.divideMatrixes(block.getGStore(), this.Q));
    }

    //Lab3

    public void addEntropy(int[][] gstore) {
        //list of coefficients
        List<Integer> list = parseMatrix(gstore);


        //Adding the first dc coefficient
        entropy.addAll(Arrays.asList( getSize(list.get(0)), list.get(0) ));

        //remove the zeros from final
        int countFinalZeros = 0;
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i) == 0) {
                countFinalZeros++;
            } else break;
        }
        int initialSize = list.size();
        if (countFinalZeros > 0) {
            while (list.size() > (initialSize - countFinalZeros)) {
                list.remove(list.size() - 1);
            }
        }
        for (int i = 1; i < list.size()-1; i++) {
            int countZeros = 0;
            while (list.get(i) == 0) {
                countZeros++;
                i++;

            }
            entropy.addAll(Arrays.asList(  countZeros, getSize(list.get(i)), list.get(i) ));
        }
        entropy.addAll(Arrays.asList( 0, 0 ));

    }

    private int getSize(int amplitude) {
        // calculating in wich interval it is placed to return the size corresponding

        if (amplitude == 0) return 0;
        else if (amplitude == 1 || amplitude == -1) {
            return 1;
        } else {
            for (int i = 2; i <= 10; i++) {
                if (amplitude < 0) {
                    amplitude = (-1) * amplitude;
                }
                if (amplitude >= amplitudes.get(i).get(0) && amplitude <= amplitudes.get(i).get(1)) return i;

            }
        }
        return -1;
    }

    private List<Integer> parseMatrix(int[][] gstore) {
        List<Integer> list = new ArrayList<>();
        int col = 0, row = 0, k = 0;
        for (int i = 0; i < 7; i++) {
            k++;
            if (k % 2 == 1) {
                list.add(gstore[row][col]);
                col++;
                for (int j = 0; j < k; j++) {
                    list.add(gstore[row][col]);
                    col--;
                    row++;
                }

            } else {
                list.add(gstore[row][col]);
                row++;
                for (int j = 0; j < k; j++) {
                    list.add(gstore[row][col]);
                    row--;
                    col++;
                }
            }
        }
        for (int i = 0; i < 6; i++) {
            k--;
            if (k % 2 == 1) {
                list.add(gstore[row][col]);
                row++;
                for (int j = 0; j < k; j++) {
                    list.add(gstore[row][col]);
                    col--;
                    row++;
                }

            } else {
                list.add(gstore[row][col]);
                col++;
                for (int j = 0; j < k; j++) {
                    list.add(gstore[row][col]);
                    row--;
                    col++;
                }
            }
        }
        return list;

    }

}
