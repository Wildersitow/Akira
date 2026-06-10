package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import service.SesionCuenta;

public class ControladorMenuPrincipal {

    private final UtilidadesFX utilidades;

    public ControladorMenuPrincipal() {
        this.utilidades = new UtilidadesFX();
    }

    @FXML
    public void cambiarInicio(ActionEvent event) {
        utilidades.cambiarEscenaConTransicion(event, "/FXML/MenuPrincipal.fxml");
    }

    @FXML
    public void cambiarCompras(ActionEvent event) {
        utilidades.cambiarEscenaConTransicion(event, "/FXML/MenuCompras.fxml");
    }

    @FXML
    public void cambiarAlquiler(ActionEvent event) {
        utilidades.cambiarEscenaConTransicion(event, "/FXML/Alquiler.fxml");
    }

    @FXML
    public void cambiarContratos(ActionEvent event) {
        utilidades.cambiarEscenaConTransicion(event, "/FXML/Contratos.fxml");
    }

    @FXML
    public void cambiarFlota(ActionEvent event) {
        utilidades.cambiarEscenaConTransicion(event, "/FXML/MenuFlota.fxml");
    }

    @FXML
    public void cambiarAsistente(ActionEvent event) {
        utilidades.cambiarEscenaConTransicion(event, "/FXML/AsistenteAI.fxml");
    }

    @FXML
    public void cambiarLogin(ActionEvent event) {
        SesionCuenta.cerrarSesion();
        utilidades.cambiarEscenaConTransicion(event, "/FXML/Login.fxml");
    }

    @FXML
    public void cambiarPerfil(ActionEvent event) {
        utilidades.cambiarEscenaConTransicion(event, "/FXML/Perfil.fxml");
    }
}