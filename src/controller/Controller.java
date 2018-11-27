package controller;

import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.opencv.core.Core;
import utils.Cache;
import utils.OpenCV;
import utils.Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static javax.imageio.ImageIO.write;

public class Controller {

    // LISTA DE PASSOS
    public static final String
        ORIGINAL = "Imagem original",
        TONS_DE_CINZA = "Passo 1: Tons de cinza",
        LIMIARIZACAO = "Passo 2: Limiarização",
        DESFOQUE = "Passo 3: Desfoque",
        BUSCA_CONTORNOS = "Passo 4: Busca contornos",
        SEGMENTACAO_RETANGULOS = "Passo 5: Segmentação por retângulos";

    public static final String
            CACHE = "./src/assets/temp";

    // components
    @FXML Button buttonUpload, buttonSave, buttonPrevious, buttonNext;
    @FXML Label passoAtual;
    @FXML public Label dimensoes;
    @FXML ListView<String> passos;
    @FXML ImageView imageView;

    // auxiliares
    public Image image;
    public File file;
    public ArrayList<String> listaPassos = new ArrayList();
    public String etapa = "";

    // CARREGA BIBLIOTECAS DO OPENCV
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

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

                // guarda original em cache
                Cache.copyToCache(selecionada.getAbsolutePath(), "original.png");

                // pega original do cache e redimensiona
                if(utils.Cache.resizeImage(new File(CACHE + "/original.png"))){
                    Image resultado = Cache.cacheToImage("original.png");

                    // mostramos a imagem
                    imageView.setImage(resultado);
                    imageView.setFitHeight(392);

                    // setamos label de dimensões
                    setDimensoes(resultado.getWidth(), resultado.getHeight());

                    // adiciona na lista de passos
                    limpaListaPassos();
                    adicionaPasso(ORIGINAL);
                    etapa = ORIGINAL;
                    labelPassoAtual(ORIGINAL);

                    file = Cache.cacheToFile("original.png");
                }
            }
        } catch (Exception e){
            System.out.println("Problema durante seleção da imagem");
        }
    }

    @FXML
    private void save(){
        if(imageView.getImage() != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagem", "*.png"));
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
            File file = fileChooser.showSaveDialog(null);

            if(file != null) {
                BufferedImage bImg = SwingFXUtils.fromFXImage(image, null);
                try {
                    write(bImg, "PNG", file);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void previous(){
        switch (etapa){
            case TONS_DE_CINZA:
                setComponents("remover", TONS_DE_CINZA, ORIGINAL);
                imageView.setImage(Cache.cacheToImage("original.png"));
                break;

            case LIMIARIZACAO:
                setComponents("remover", LIMIARIZACAO, TONS_DE_CINZA);
                imageView.setImage(Cache.cacheToImage("tonsdecinza.png"));
                break;

            case DESFOQUE:
                imageView.setImage(Cache.cacheToImage("limiarizacao.png"));
                setComponents("remover", DESFOQUE, LIMIARIZACAO);
                break;

            case BUSCA_CONTORNOS:
                imageView.setImage(Cache.cacheToImage("desfoque.png"));
                setComponents("remover", BUSCA_CONTORNOS, DESFOQUE);
                break;

            case SEGMENTACAO_RETANGULOS:
                imageView.setImage(Cache.cacheToImage("busca-contornos.png"));
                setComponents("remover", SEGMENTACAO_RETANGULOS, BUSCA_CONTORNOS);
                break;
        }
    }

    @FXML
    private void next(){
        switch (etapa){
            case ORIGINAL:
                if(Cache.imageToCache(imageView.getImage(), "tonsdecinza.png")) {
                    file = OpenCV.tonsDeCinza(new File(CACHE + "/tonsdecinza.png"));
                    imageView.setImage(Cache.cacheToImage("tonsdecinza.png"));

                    setComponents("adicionar", ORIGINAL, TONS_DE_CINZA);
                }
                break;

            case TONS_DE_CINZA:
                if(Cache.imageToCache(imageView.getImage(), "limiarizacao.png")) {
                    file = OpenCV.limiarizacao(new File(CACHE + "/limiarizacao.png"));
                    imageView.setImage(Cache.cacheToImage("limiarizacao.png"));

                    setComponents("adicionar", TONS_DE_CINZA, LIMIARIZACAO);
                }
                break;

            case LIMIARIZACAO:
                if(Cache.imageToCache(imageView.getImage(), "desfoque.png")) {
                    file = OpenCV.desfoque(new File(CACHE + "/desfoque.png"));
                    imageView.setImage(Cache.cacheToImage("desfoque.png"));

                    setComponents("adicionar", LIMIARIZACAO, DESFOQUE);
                }
                break;

            case DESFOQUE:
                file = OpenCV.buscaContornos(new File(CACHE + "/original.png"));
                imageView.setImage(Cache.cacheToImage("busca-contornos.png"));
                setComponents("adicionar", DESFOQUE, BUSCA_CONTORNOS);
                break;

            case BUSCA_CONTORNOS:
                file = OpenCV.buscaRetangulos(new File(CACHE + "/original.png"));
                imageView.setImage(Cache.cacheToImage("busca-retangulos.png"));
                setComponents("adicionar", BUSCA_CONTORNOS, SEGMENTACAO_RETANGULOS);
                break;
        }
    }

    @FXML
    private void github(){
        Utils.openWebpage("https://github.com/tiagoboeing/ReconhecimentoPlacas-PDI");
    }

    @FXML
    private void lightbox() throws IOException {
        if(imageView.getImage() != null){
            Desktop.getDesktop().open(file);
        }
    }

    @FXML
    private void usaClassificadores(){
        if(imageView.getImage() != null) {
            file = OpenCV.usaClassificadores(new File(CACHE + "/original.png"));
            imageView.setImage(Cache.cacheToImage("classificadores.png"));
        }
    }

    public void adicionaPasso(String nome){
        listaPassos.add(nome);
        passos.setItems(FXCollections.observableArrayList(listaPassos));
    }

    public void removePasso(String nome){
        listaPassos.remove(nome);
        passos.setItems(FXCollections.observableArrayList(listaPassos));
    }

    public void limpaListaPassos(){
        listaPassos.clear();
        passos.setItems(FXCollections.observableArrayList(listaPassos));
    }

    public void labelPassoAtual(String passoAtual){
        this.passoAtual.setText(passoAtual);
    }

    // operações = "adicionar" - "remover"
    // passo = passo atual que será descrito na tela (labels)
    // anteriorProx = novo valor a ser adicionar/excluído da lista
    public void setComponents(String operacao, String passoAtual, String anteriorProximo){
        if(operacao.equalsIgnoreCase("adicionar")){
            etapa = anteriorProximo;
            adicionaPasso(anteriorProximo);
            labelPassoAtual(anteriorProximo);
        } else if(operacao.equalsIgnoreCase("remover")){
            etapa = anteriorProximo;
            removePasso(passoAtual);
            labelPassoAtual(anteriorProximo);
        }
    }

    public void setDimensoes(Double largura, Double altura){
        this.dimensoes.setText("(" + largura + " x " + altura + ")");
    }

}
