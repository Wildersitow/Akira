package view;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ControladorMenuInicio {

    private Stage stage;
    private Scene scene;
    private final UtilidadesFX utilidades;

    public ControladorMenuInicio(UtilidadesFX utilidades) {
        this.utilidades = utilidades;
    }

    public void cambiarIniciarSesion(ActionEvent event){
        try {
            System.out.println("Intentando cambiar a Iniciar Sesión...");
            utilidades.cambiarEscenaConTransicion(event, "/src/main/resources/FXML/Login.fxml");
        } catch (Exception e) {
            System.err.println("ERROR al cambiar escena:");
            e.printStackTrace();
        }
    }

}
