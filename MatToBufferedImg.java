package net.semanticmetadata.lire.sampleapp;

//import java.awt.Image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;

public class MatToBufferedImg {

    String fileE;
    Mat matrix;
    MatOfByte matOfByte;

    public MatToBufferedImg() {
    }

    public MatToBufferedImg(Mat matrix, String fileE) {
        this.matrix = matrix;
        this.fileE = fileE;

    }

    public void settMatrix(Mat matrix, String fileE) {
        this.matrix = matrix;
        this.fileE = fileE;
        matOfByte = new MatOfByte();
    }

    public BufferedImage hentBufferedImg() {
        Highgui.imencode(fileE, matrix, matOfByte);
        BufferedImage bufferedImg = null;
        byte[] byteArray = matOfByte.toArray();

        try {
            InputStream inputStream = new ByteArrayInputStream(byteArray);
            bufferedImg = ImageIO.read(inputStream);

        } catch (Exception e) {

            e.printStackTrace();
        }


        return bufferedImg;

    }

}
