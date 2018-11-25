package utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Utils {

    public static void openWebpage(String urlString) {
        try {
            Desktop.getDesktop().browse(new URL(urlString).toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage matToBufferedImage(Mat original) {
        // init
        BufferedImage image = null;
        int width = original.width(), height = original.height(), channels = original.channels();
        byte[] sourcePixels = new byte[width * height * channels];
        original.get(0, 0, sourcePixels);

        if (original.channels() > 1){
            image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        }
        else{
            image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        }
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

        return image;
    }

    public static Image mat2Image(Mat frame) {
        try{
            return SwingFXUtils.toFXImage(matToBufferedImage(frame), null);
        }catch (Exception e){
            System.err.println("Cannot convert the Mat obejct: " + e);
            return null;
        }
    }

    public static Mat file2Mat(File file){
        return Imgcodecs.imread(file.getAbsolutePath());
    }

    public static BufferedImage file2BufferedImage(File file){
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            System.out.println("Problema ao converter File2BufferedImage - file: Utils.java");
            return null;
        }
    }

}
