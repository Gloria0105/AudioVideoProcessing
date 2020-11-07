package models;

import java.util.List;

public class Decoder {

    private Encoder encoder;

    private List<BlockStore> encodedY;
    private List<BlockStore> encodedU;
    private List<BlockStore> encodedV;

    public Decoder(Encoder encoder) {
        this.encoder = encoder;
        encodedY = encoder.getEncodedY();
        encodedU = encoder.getEncodedU();
        encodedV = encoder.getEncodedV();
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
                    matrix[line + i][column + j] = blockStore.getYuvStore()[i][j];
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

}
