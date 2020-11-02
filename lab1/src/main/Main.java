package main;

import models.Encoder;
import models.PPM;

public class Main {
    public static void main(String[] args) {
        PPM image = new PPM("./data/nt-P3.ppm");
        Encoder encoder = new Encoder(image);
        encoder.printMatrix(encoder.getEncodedY());
        encoder.printMatrix(encoder.getEncodedU());
        encoder.printMatrix(encoder.getEncodedV());
    }
}
