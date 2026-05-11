package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static javafx.application.Application.launch;

public class Akira extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Akira.class.getResource("/src/main/resources/FXML/MenuInicio.fxml"));

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
