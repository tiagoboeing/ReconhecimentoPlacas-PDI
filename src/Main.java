import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.Cache;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("view/main.fxml"));
        primaryStage.setTitle("Reconhecimento de placas - PDI");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 1000, 520));
        primaryStage.show();
    }


    public static void main(String[] args) {

        // verificamos pasta de cache
        utils.Cache.createCacheFolder();

        launch(args);

        // ao fechar o programa
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                // limpa o que houver no cache
                utils.Cache.clearCache();
            }
        }, "Shutdown-thread"));
    }
}
