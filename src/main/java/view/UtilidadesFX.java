package view;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class UtilidadesFX {

    private Stage stage;
    private Scene scene;
    private Parent root;

    private void cambiarEscenaConTransicion(ActionEvent event, String rutaFxml) {
        try {
            Node source = (Node) event.getSource();
            stage = (Stage) source.getScene().getWindow();
            Parent rootActual = source.getScene().getRoot();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(400), rootActual);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            ScaleTransition scaleOut = new ScaleTransition(Duration.millis(400), rootActual);
            scaleOut.setFromX(1.0);
            scaleOut.setFromY(1.0);
            scaleOut.setToX(0.95);
            scaleOut.setToY(0.95);

            ParallelTransition exitTransition = new ParallelTransition(fadeOut, scaleOut);

            exitTransition.setOnFinished(e -> {
                try {
                    Parent rootNuevo = FXMLLoader.load(getClass().getResource(rutaFxml));

                    rootNuevo.setOpacity(0.0);
                    rootNuevo.setScaleX(1.05);
                    rootNuevo.setScaleY(1.05);

                    scene = new Scene(rootNuevo);
                    stage.setScene(scene);

                    FadeTransition fadeIn = new FadeTransition(Duration.millis(400), rootNuevo);
                    fadeIn.setFromValue(0.0);
                    fadeIn.setToValue(1.0);

                    ScaleTransition scaleIn = new ScaleTransition(Duration.millis(400), rootNuevo);
                    scaleIn.setFromX(1.05);
                    scaleIn.setFromY(1.05);
                    scaleIn.setToX(1.0);
                    scaleIn.setToY(1.0);

                    ParallelTransition enterTransition = new ParallelTransition(fadeIn, scaleIn);
                    enterTransition.play();

                } catch (Exception ex) {
                    System.err.println("ERROR al cargar nueva escena:");
                    ex.printStackTrace();
                }
            });

            exitTransition.play();

        } catch (Exception e) {
            System.err.println("ERROR en la transición:");
            e.printStackTrace();
        }
    }
}
