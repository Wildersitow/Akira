package view;

import javafx.event.ActionEvent;

public class ControladorMenuCompras {

    private final UtilidadesFX utilidades;

    public ControladorMenuCompras() {
        this.utilidades = new UtilidadesFX();
    }

    public void cambiarInicio(ActionEvent event){
        try {
            System.out.println("Intentando cambiar a MenuPrincipal...");
            utilidades.cambiarEscenaConTransicion(event, "/FXML/MenuPrincipal.fxml");
        } catch (Exception e) {
            System.err.println("ERROR al cambiar escena:");
            e.printStackTrace();
        }
    }

    public void cambiarAlquiler(ActionEvent event){
        try {
            System.out.println("Intentando cambiar a Alquiler...");
            utilidades.cambiarEscenaConTransicion(event, "/FXML/Alquiler.fxml");
        } catch (Exception e) {
            System.err.println("ERROR al cambiar escena:");
            e.printStackTrace();
        }
    }

    public void cambiarCompras(ActionEvent event){
        try {
            System.out.println("Intentando cambiar a MenuCompras...");
            utilidades.cambiarEscenaConTransicion(event, "/FXML/MenuCompras.fxml");
        } catch (Exception e) {
            System.err.println("ERROR al cambiar escena:");
            e.printStackTrace();
        }
    }

    public void cambiarFlota(ActionEvent event){
        try {
            System.out.println("Intentando cambiar a MenuFlota...");
            utilidades.cambiarEscenaConTransicion(event, "/FXML/MenuFlota.fxml");
        } catch (Exception e) {
            System.err.println("ERROR al cambiar escena:");
            e.printStackTrace();
        }
    }

    public void cambiarAsistente(ActionEvent event){
        try {
            System.out.println("Intentando cambiar a Asistente...");
            utilidades.cambiarEscenaConTransicion(event, "/FXML/Asistente.fxml");
        } catch (Exception e) {
            System.err.println("ERROR al cambiar escena:");
            e.printStackTrace();
        }
    }

}
