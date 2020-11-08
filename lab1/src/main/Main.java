package main;

import models.Decoder;
import models.Encoder;
import models.PPM;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        PPM image = new PPM("./data/nt-P3.ppm");
        Encoder encoder = new Encoder(image);
        encoder.printMatrix(encoder.getEncodedY());
        encoder.printMatrix(encoder.getEncodedU());
        encoder.printMatrix(encoder.getEncodedV());
        Decoder decoder = new Decoder(encoder);
        decoder.getImage().convertToRGB().writePPM("final.ppm");


    }
}
