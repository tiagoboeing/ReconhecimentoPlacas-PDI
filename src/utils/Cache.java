package utils;

import controller.Controller;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import static org.opencv.imgcodecs.Imgcodecs.imread;

public class Cache {

    // cria pasta de cache
    public static void createCacheFolder(){
        // se pasta não existir cria se não, limpa apenas
        if(!checkFolder(Controller.CACHE + "/")){
            new File(Controller.CACHE + "/").mkdirs();
        } else {
            clearCache();
        }
    }

    // checa se determinada pasta existe
    public static Boolean checkFolder(String pasta) {
        File folder = new File(pasta);
        if(folder.exists()){
            return true;
        } else {
            return false;
        }
    }

    // limpa conteúdo da pasta de cache
    public static void clearCache(){
        File folder = new File(Controller.CACHE + "/");
        if (folder.isDirectory()) {
            File[] sun = folder.listFiles();
            for (File toDelete : sun) {
                toDelete.delete();
            }
        }
    }

    public static Boolean imageToCache(Image img, String nomeArquivo){
        try {
            BufferedImage bImg = SwingFXUtils.fromFXImage(img, null);
            File outputfile = new File(Controller.CACHE + "/" + nomeArquivo);
            ImageIO.write(bImg, "png", outputfile);
            return true;
        } catch (IOException e) {
            System.out.println("Problema ao criar cache da imagem - method: imageToCache()");
            return false;
        }
    }

    // copia imagem de um caminho para pasta cache
    // por padrão substitui arquivos com mesmo nome
    public static Boolean copyToCache(String caminho, String nome){
        try {
            Path source = Paths.get(caminho);
            Path target = Paths.get(Controller.CACHE + "/");
            Files.copy(source, target.resolve(nome), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            System.out.println("Problema ao copiar imagem para pasta de cache");
            System.out.println("Método: copyToCache() em utils.Cache");
            return false;
        }
    }

    // redimensiona imagem para 4x de suas medidas originais
    public static Boolean resizeImage(File file){
        try {
            BufferedImage image = ImageIO.read(file);

            Mat src = imread(Controller.CACHE + "/original.png");
            Mat resizeimage = new Mat();
            Size scaleSize = new Size(image.getWidth()*4,image.getHeight()*4);

            Imgproc.resize(src, resizeimage, scaleSize, Imgproc.INTER_AREA);

            HighGui.toBufferedImage(resizeimage);

            File output = new File(Controller.CACHE + "/original.png");
            ImageIO.write(Utils.matToBufferedImage(resizeimage), "png", output);

            return true;
        } catch (Exception e){
            System.out.println("Erro ao redimensionar e salvar imagem");
            return false;
        }
    }

    // recupera qualquer imagem da pasta cache
    public static Image cacheToImage(String nomeImagem){
        return new Image(new File(Controller.CACHE + "/" + nomeImagem).toURI().toString());
    }

    public static File cacheToFile(String nomeArquivo){
        return new File(Controller.CACHE + "/" + nomeArquivo);
    }

}
