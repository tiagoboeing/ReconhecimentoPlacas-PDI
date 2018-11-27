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
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OpenCV {

    // lista de contornos
    public List<MatOfPoint> contours = new ArrayList<>();

    // CARREGA BIBLIOTECAS DO OPENCV
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static File tonsDeCinza(File file){
        try {
            BufferedImage image = Utils.file2BufferedImage(file);

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
            Mat src = Utils.file2Mat(file);

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

            Mat src = Utils.file2Mat(file);
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

    public static File buscaContornos(File file){
        try {

            // gera números randomicos para cor dos traços
            Random rng = new Random(345);

            Mat image = Utils.file2Mat(file);
            Mat srcGray = new Mat();
            Mat dest = new Mat();
            Mat desfoque = new Mat();
            Mat cannyOutput = new Mat();
            Mat hierarchy = new Mat();
            MatOfPoint max_contour = new MatOfPoint();

            // lista de contornos
            List<MatOfPoint> contours = new ArrayList<>();

            Imgproc.cvtColor(image, srcGray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.threshold(srcGray, dest, 90, 255, Imgproc.THRESH_BINARY);
            Imgproc.GaussianBlur(dest, desfoque, new Size(5,5), 0);

//            Imgproc.Canny(srcGray, cannyOutput, 0, 255);
            Imgproc.findContours(desfoque, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);

            // desenha contornos encontrados
            Mat drawing = Mat.zeros(desfoque.size(), CvType.CV_8UC3);
            for (int i = 0; i < contours.size(); i++) {
                Scalar color = new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
                Imgproc.drawContours(drawing, contours, i, color, 2, Imgproc.LINE_8, hierarchy, 0, new Point());
            }

            File output = new File(Controller.CACHE + "/busca-contornos.png");
            ImageIO.write(Utils.matToBufferedImage(drawing), "png", output);

            return Cache.cacheToFile("busca-contornos.png");

        } catch (Exception e){
            System.out.println("Problema aplicar buscaContornos()");
            return null;
        }
    }

    public static File buscaRetangulos(File file){
        try{

            Mat image = Utils.file2Mat(file);
            Mat srcGray = new Mat();
            Mat cannyOutput = new Mat();
            Mat hierarchy = new Mat();
            MatOfPoint max_contour = new MatOfPoint();

            // lista de contornos
            List<MatOfPoint> contours = new ArrayList<>();
            List<MatOfPoint> approx = new ArrayList<>();

            Imgproc.cvtColor(image, new Mat(), Imgproc.COLOR_BGR2GRAY);
            Imgproc.Canny(srcGray, cannyOutput, 0, 255);
            Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);

//            System.out.println("CONTORNOS: " + contours);


            // verificamos cada contorno
            for(int i=0; i < contours.size(); i++){
                // converte de MatOfPoint to MatOfPoint2f
                contours.get(i).convertTo(contours.get(i), CvType.CV_32FC2);
                MatOfPoint2f contorno2f = new MatOfPoint2f(contours.get(i));
                MatOfPoint2f temp = new MatOfPoint2f(contours.get(i));

                Double perimetro = Imgproc.arcLength(new MatOfPoint2f(contours.get(i)), true);

                if(perimetro > 120){

                    Imgproc.approxPolyDP(contorno2f, temp, perimetro * 0.03, true);

                    // volta de MatOfPoint2f para MatOfPoint
                    temp.convertTo(contours.get(i), CvType.CV_32S);
                    MatOfPoint contornoMatOfPoint = new MatOfPoint(temp.toArray());

                    if (contornoMatOfPoint.size().height == 4) {
                        System.out.println("PASSOU AQ");
                        Rect rec = Imgproc.boundingRect(contornoMatOfPoint);
                        System.out.println("X: " + rec.x + " y:" + rec.y);
                    }
                }
            }

            System.out.println();

            return null;
//            File output = new File(Controller.CACHE + "/busca-retangulos.png");
//            ImageIO.write(Utils.matToBufferedImage(contours.get(0)), "png", output);
//
//            return Cache.cacheToFile("busca-retangulos.png");

        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Problema aplicar buscaRetangulos()");
            return null;
        }
    }


    public static Point[] getFirstThreeCoordinates(IntBuffer buffer) {

        Point[] coordinates = new Point[3];

        for (int i = 0; i < 3 && buffer.limit() - buffer.position() >= 2; i++) {

            coordinates[i] = new Point(buffer.get(), buffer.get());
        }

        return coordinates;
    }

//    utilizando classificadores
//    public static File buscaContornos(File file){
//        CascadeClassifier faceDetector = new CascadeClassifier("./src/utils/car.xml");
//        Mat image = Imgcodecs.imread(file.getAbsolutePath());
//
//        MatOfRect detections = new MatOfRect();
//        faceDetector.detectMultiScale(image, detections);
//
//        System.out.println(String.format("Detected %s", detections.toArray().length));
//
//        for (Rect rect : detections.toArray()) {
//            Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0), 3);
//        }
//
//        MatOfByte mtb = new MatOfByte();
//        Imgcodecs.imencode(".png", image, mtb);
//
//        Image img = new Image(new ByteArrayInputStream(mtb.toArray()));
//        Cache.imageToCache(img, "busca-contornos.png");
//
//        return Cache.cacheToFile("busca-contornos.png");
//    }

}
