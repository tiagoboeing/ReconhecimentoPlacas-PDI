package utils;

import controller.Controller;
import javafx.scene.image.Image;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OpenCV {

    // CARREGA BIBLIOTECAS DO OPENCV
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static File tonsDeCinza(File file){
        try {
            File input = file;
            BufferedImage image = ImageIO.read(input);

            byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
            mat.put(0, 0, data);

            Mat mat1 = new Mat(image.getHeight(),image.getWidth(), CvType.CV_8UC1);
            Imgproc.cvtColor(mat, mat1, Imgproc.COLOR_BGR2GRAY);

            byte[] data1 = new byte[mat1.rows() * mat1.cols() * (int)(mat1.elemSize())];
            mat1.get(0, 0, data1);
            BufferedImage image1 = new BufferedImage(mat1.cols(), mat1.rows(), BufferedImage.TYPE_BYTE_GRAY);
            image1.getRaster().setDataElements(0, 0, mat1.cols(), mat1.rows(), data1);

            File output = new File(Controller.CACHE + "/tonsdecinza.png");
            ImageIO.write(image1, "png", output);

            // pega imagem na pasta do cache e devolve
            return Cache.cacheToFile("tonsdecinza.png");

        } catch (IOException e) {
            System.out.println("Problema ao converter para Tons de Cinza");
            return null;
        }
    }

    public static File limiarizacao(File file){
        try {
            Mat dest = new Mat();

            Mat src = Imgcodecs.imread(file.getAbsolutePath());
            Imgproc.threshold(src, dest, 90, 255, Imgproc.THRESH_BINARY);

            HighGui.toBufferedImage(dest);

            File output = new File(Controller.CACHE + "/limiarizacao.png");
            ImageIO.write(Utils.matToBufferedImage(dest), "png", output);

            // pega imagem na pasta do cache e devolve
            return Cache.cacheToFile("limiarizacao.png");

        } catch (Exception e){
            System.out.println("Problema ao limiarizar a imagem");
            return null;
        }
    }

    public static File desfoque(File file){
        try {
            Mat dest = new Mat();

            Mat src = Imgcodecs.imread(file.getAbsolutePath());
            Imgproc.GaussianBlur(src, dest, new Size(5,5), 0);

            HighGui.toBufferedImage(dest);

            File output = new File(Controller.CACHE + "/desfoque.png");
            ImageIO.write(Utils.matToBufferedImage(dest), "png", output);

            // pega imagem na pasta do cache e devolve
            return Cache.cacheToFile("desfoque.png");

        } catch (Exception e){
            System.out.println("Problema aplicar filtro desfoque na imagem");
            return null;
        }
    }

//    public static File buscaContornos(File file){
//        try {
//            List<MatOfPoint> contours = new ArrayList<>();
//            Mat hierarchy = new Mat();
//
//            Mat src = Imgcodecs.imread(file.getAbsolutePath());
//
//            Imgproc.findContours(src, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);
//
//            HighGui.toBufferedImage(src);
//
//            File output = new File("./src/assets/temp/busca-contornos.png");
//            ImageIO.write(Utils.matToBufferedImage(src), "png", output);
//
//            // pega imagem na pasta do cache e devolve
//            return Cache.cacheToFile("busca-contornos.png");
//
//        } catch (Exception e){
//            System.out.println("Problema aplicar filtro desfoque na imagem");
//            return null;
//        }
//    }

    public static File buscaContornos(File file){
        CascadeClassifier faceDetector = new CascadeClassifier("./src/utils/car.xml");
        Mat image = Imgcodecs.imread(file.getAbsolutePath());

        MatOfRect detections = new MatOfRect();
        faceDetector.detectMultiScale(image, detections);

        System.out.println(String.format("Detected %s", detections.toArray().length));

        for (Rect rect : detections.toArray()) {
            Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0), 3);
        }

        MatOfByte mtb = new MatOfByte();
        Imgcodecs.imencode(".png", image, mtb);

        Image img = new Image(new ByteArrayInputStream(mtb.toArray()));
        Cache.imageToCache(img, "busca-contornos.png");

        return Cache.cacheToFile("busca-contornos.png");
    }

}
