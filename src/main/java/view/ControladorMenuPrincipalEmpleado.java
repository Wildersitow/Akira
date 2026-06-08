package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import service.SesionCuenta;

public class ControladorMenuPrincipalEmpleado {

    private final UtilidadesFX utilidades;

    public ControladorMenuPrincipalEmpleado() {
        this.utilidades = new UtilidadesFX();
    }

    @FXML
    public void cambiarInicio(ActionEvent event) {
        utilidades.cambiarEscenaConTransicion(event, "/FXML/MenuPrincipalEmpleado.fxml");
    }
    @FXML public void cambiarAgregarVehiculo(ActionEvent event) {
        utilidades.cambiarEscenaConTransicion(event, "/FXML/MenuAgregarVehiculo.fxml");
    }
    @FXML public void cambiarContratos(ActionEvent event) {
        utilidades.cambiarEscenaConTransicion(event, "/FXML/MenuContratosEmpleado.fxml");
    }
    @FXML public void cambiarHistorial(ActionEvent event) {
        utilidades.cambiarEscenaConTransicion(event, "/FXML/HistorialDeCompras.fxml");
    }
    @FXML public void cambiarFlota(ActionEvent event) {
        utilidades.cambiarEscenaConTransicion(event, "/FXML/MenuFlotaAdmin.fxml");
    }
    @FXML public void cambiarAsistente(ActionEvent event) {
        utilidades.cambiarEscenaConTransicion(event, "/FXML/AsistenteAIEmpleados.fxml");
    }

    public void cambiarLogin(ActionEvent event) {
        SesionCuenta.cerrarSesion();
        utilidades.cambiarEscenaConTransicion(event, "/FXML/Login.fxml");
    }


}
