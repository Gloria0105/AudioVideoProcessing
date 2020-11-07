package models;

import java.util.ArrayList;
import java.util.List;

public class Encoder {
    private PPM image;
    private List<BlockStore> encodedY;
    private List<BlockStore> encodedU;
    private List<BlockStore> encodedV;


    public Encoder(PPM image) {
        this.image = image;
        encodedY = splitInBlocks(image, "Y", image.getY());
        encodedU = splitInBlocks(image, "U", image.getU());
        encodedV = splitInBlocks(image, "V", image.getV());

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
                    encoded.add(resizeBlock(average4Block(store)));
            }

        return encoded;
    }

    private static BlockStore subMatrix(String type, int i_pos, int j_pos, double[][] matrix) {
        BlockStore store = new BlockStore(8, type);

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                store.getYuvStore()[i][j] = matrix[i + i_pos][j + j_pos];

        return store;
    }

    private static BlockStore average4Block(BlockStore toSample) {
        BlockStore sampleStore = new BlockStore(4, toSample.getStoreType());
        int line = 0;
        int column = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {

                sampleStore.getYuvStore()[i][j] = (toSample.getYuvStore()[line][column] +
                        toSample.getYuvStore()[line][column + 1] +
                        toSample.getYuvStore()[line + 1][column] +
                        toSample.getYuvStore()[line + 1][column + 1])
                        / 4;
                column += 2;
            }
            line += 2;
            column = 0;
        }
        return sampleStore;
    }

//    public List<BlockStore> getListResized(List<BlockStore> encoded) {
//        List<BlockStore> resized = new ArrayList<>();
//        encoded.forEach(b -> resized.add(resizeBlock(b)));
//        return resized;
//    }

    private static BlockStore resizeBlock(BlockStore blockStore) {
        BlockStore sampleStore = new BlockStore(8, blockStore.getStoreType());
        int line = 0;
        int column = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                double value = blockStore.getYuvStore()[i][j];
                sampleStore.getYuvStore()[line][column] = value;
                sampleStore.getYuvStore()[line][column + 1] = value;
                sampleStore.getYuvStore()[line + 1][column] = value;
                sampleStore.getYuvStore()[line + 1][column + 1] = value;
                column += 2;
            }
            line += 2;
            column = 0;
        }

        return sampleStore;
    }

}
