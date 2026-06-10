package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;

public class Akira extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            Font.loadFont(new File("src/main/resources/fuentes/Rajdhani-Regular.ttf")
                    .toURI().toURL().toExternalForm(), 14);
            Font.loadFont(new File("src/main/resources/fuentes/Rajdhani-SemiBold.ttf")
                    .toURI().toURL().toExternalForm(), 14);
            Font.loadFont(new File("src/main/resources/fuentes/Rajdhani-Bold.ttf")
                    .toURI().toURL().toExternalForm(), 14);

            File fxmlFile = new File("src/main/resources/FXML/Login.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
            Parent root = loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Akira");

            File iconFile = new File("src/main/resources/imagenes/LogoFondo.png");
            if (iconFile.exists()) {
                stage.getIcons().add(new Image(iconFile.toURI().toString()));
            }

            stage.show();

        } catch (Exception e) {
            System.out.println("Error al cargar FXML:");
            e.printStackTrace();
        }
    }
}