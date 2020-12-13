package main;

import models.Decoder;
import models.Encoder;
import models.PPM;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        PPM image = new PPM("./data/nt-P3.ppm");
        Encoder encoder = new Encoder(image);
        Decoder decoder = new Decoder(encoder,encoder.getEntropy());
        decoder.getImage().convertToRGB().writePPM("final.ppm");


    }
}
