package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.File;

public class Controller {

    @FXML Button buttonUpload, buttonSave, buttonPrevious, buttonNext;
    @FXML Label passoAtual;
    @FXML ImageView imageView;

    // auxiliar
    private Image image;
    private File file;

    @FXML
    private void upload(){
        FileChooser arquivo = new FileChooser();
        arquivo.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "Images",
                "*.jpg", "*.jpeg", "*.JPG", "*.JPEG",
                "*.png", "*.PNG"
        ));
        arquivo.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
        File selecionada = arquivo.showOpenDialog(null);

        try {
            if(selecionada.exists()){
                utils.Cache.clearCache();
                labelPassoAtual("Imagem original");

                // se cópia da imagem para cache for bem sucedida, mostramos
                if(utils.Cache.copyToCache(selecionada.getAbsolutePath(), "original.jpg")){
                    imageView.setImage(utils.Cache.cacheToImage("original.jpg"));
                    imageView.setFitHeight(392);
                }
            }
        } catch (Exception e){
            System.out.println("Problema durante seleção da imagem");
        }
    }

    public void labelPassoAtual(String passoAtual){
        this.passoAtual.setText(passoAtual);
    }


}
