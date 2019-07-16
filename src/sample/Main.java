package sample;

import org.opencv.core.Core;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import org.opencv.core.Mat;

import static org.opencv.imgcodecs.Imgcodecs.imwrite;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = (BorderPane) loader.load();

        Scene scene = new Scene(root, 800, 600);
//        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

        primaryStage.setTitle("Testing");
        primaryStage.setScene(scene);
        primaryStage.show();

        Controller controller = loader.getController();
        controller.init();
    }


    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Ident id = new Ident();
        launch(args);
    }
}
