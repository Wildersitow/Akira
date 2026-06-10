package view;

import dao.ClienteDAO;
import model.Cliente;
import service.ServiceException;
import service.SesionCuenta;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import service.SesionCuenta;
import view.UtilidadesFX;

public class    ControladorPerfil implements Initializable {




    @FXML private TextField field_nombre;
    @FXML private TextField field_telefono;
    @FXML private TextField field_direccion;
    @FXML private TextField field_licencia;
    @FXML private TextField field_historial;


    @FXML private Label label_email;
    @FXML private Label label_cedula;
    @FXML private Label label_puntos;
    @FXML private Label label_rol;


    @FXML private Label label_mensaje;
    @FXML private Button boton_guardar;

    private final UtilidadesFX utilidades;

    public ControladorPerfil() {
        this.utilidades = new UtilidadesFX();
    }

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private Cliente clienteActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarDatosCliente();
    }

    private void cargarDatosCliente() {
        if (!SesionCuenta.haySesionActiva()) return;
        if (!(SesionCuenta.getUsuarioActual() instanceof Cliente cliente)) return;

        this.clienteActual = cliente;

        // Campos editables — cargar valores actuales si existen
        field_nombre.setText(cliente.getNombre() != null ? cliente.getNombre() : "");
        field_telefono.setText(cliente.getTelefono() != 0 ? String.valueOf(cliente.getTelefono()) : "");
        field_direccion.setText(cliente.getDireccion() != null ? cliente.getDireccion() : "");
        field_licencia.setText(cliente.getLicenciaConducir() != null ? cliente.getLicenciaConducir() : "");
        field_historial.setText(cliente.getHistorialCredito() != 0 ? String.valueOf(cliente.getHistorialCredito()) : "");

        // Info solo lectura
        label_email.setText(cliente.getEmail());
        label_cedula.setText(cliente.getDocumentoId());
        label_puntos.setText(String.valueOf(cliente.getPuntosFidelidad()));
        label_rol.setText(cliente.getRol());
    }

    @FXML
    private void handleGuardar() {
        // Validar campos obligatorios
        if (field_nombre.getText().isBlank()) {
            mostrarMensaje("El nombre no puede estar vacío.", false);
            return;
        }
        if (field_telefono.getText().isBlank()) {
            mostrarMensaje("El teléfono no puede estar vacío.", false);
            return;
        }

        // Validar que teléfono sea numérico
        long telefono;
        try {
            telefono = Long.parseLong(field_telefono.getText().trim());
        } catch (NumberFormatException e) {
            mostrarMensaje("El teléfono debe ser un número válido.", false);
            return;
        }

        // Validar historial de crédito
        double historial = 0;
        if (!field_historial.getText().isBlank()) {
            try {
                historial = Double.parseDouble(field_historial.getText().trim());
            } catch (NumberFormatException e) {
                mostrarMensaje("El historial de crédito debe ser un número válido.", false);
                return;
            }
        }

        try {
            boton_guardar.setDisable(true);
            boton_guardar.setText("GUARDANDO...");

            clienteDAO.actualizarPerfil(
                    clienteActual.getId(),
                    field_nombre.getText().trim(),
                    telefono,
                    field_direccion.getText().trim(),
                    field_licencia.getText().trim(),
                    historial
            );

            mostrarMensaje("✓ Perfil actualizado correctamente.", true);

        } catch (ServiceException e) {
            mostrarMensaje("Error: " + e.getMessage(), false);
        } finally {
            boton_guardar.setDisable(false);
            boton_guardar.setText("GUARDAR CAMBIOS");
        }
    }

    private void mostrarMensaje(String texto, boolean exito) {
        label_mensaje.setText(texto);
        label_mensaje.setVisible(true);
        label_mensaje.setStyle(
                exito
                        ? "-fx-text-fill: #4CAF50; -fx-font-family: 'Segoe UI'; -fx-font-size: 13px; -fx-font-weight: bold;"
                        : "-fx-text-fill: #e74c3c; -fx-font-family: 'Segoe UI'; -fx-font-size: 13px; -fx-font-weight: bold;"
        );
    }


    @FXML public void irInicio(ActionEvent event) {
        utilidades.cambiarEscenaConTransicion(event, "/FXML/MenuPrincipal.fxml");
    }
    @FXML public void irContratos(ActionEvent event) {
        utilidades.cambiarEscenaConTransicion(event, "/FXML/Contratos.fxml");
    }
    @FXML public void irVehiculos(ActionEvent event) {
        utilidades.cambiarEscenaConTransicion(event, "/FXML/MenuCompras.fxml");
    }
    @FXML public void cerrarSesion(ActionEvent event) {
        SesionCuenta.cerrarSesion();
        utilidades.cambiarEscenaConTransicion(event, "/FXML/Login.fxml");
    }
}
