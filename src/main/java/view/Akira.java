package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

import static javafx.application.Application.launch;

public class Akira extends Application {

    static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) {
        try {
            File fxmlFile = new File("src/main/resources/FXML/Login.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());

            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Akira");
            stage.show();

        } catch (Exception e) {
            System.out.println("Error al cargar FXML:");
            e.printStackTrace();
        }
    }
}
