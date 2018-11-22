package utils;

import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Cache {

    // cria pasta de cache
    public static void createCacheFolder(){
        // se pasta não existir cria se não, limpa apenas
        if(!checkFolder("./src/assets/temp")){
            new File("./src/assets/temp").mkdirs();
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
        File folder = new File("./src/assets/temp");
        if (folder.isDirectory()) {
            File[] sun = folder.listFiles();
            for (File toDelete : sun) {
                toDelete.delete();
            }
        }
    }

    // copia imagem de um caminho para pasta cache
    // por padrão substitui arquivos com mesmo nome
    public static Boolean copyToCache(String caminho, String nome){
        try {
            Path source = Paths.get(caminho);
            Path target = Paths.get("./src/assets/temp/");
            Files.copy(source, target.resolve(nome), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            System.out.println("Problema ao copiar imagem para pasta de cache");
            System.out.println("Método: copyToCache() em utils.Cache");
            return false;
        }
    }

    // devolve imagem original da pasta cache
    public static Image cacheToImage(String nomeImagem){
        return new Image(new File("./src/assets/temp/" + nomeImagem).toURI().toString());
    }

}
