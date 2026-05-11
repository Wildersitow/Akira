package view;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import service.ServiceException;
import service.ServiceLogin;

import java.awt.*;
import java.awt.event.ActionEvent;

public class ControladorLogin {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TextField field_usuario;

    @FXML
    private PasswordField field_contra;

    private ServiceLogin serviceLogin;

    @FXML
    public void initialize() {
        serviceLogin = new ServiceLogin();
        System.out.println("✓ IniciarSesiónController inicializado correctamente");
    }

    @FXML
    private void handleIniciarSesion(ActionEvent event) {
        try {
            System.out.println("\n=== CONTROLADOR: Iniciando proceso de inicio de sesión ===");

            String nombreUsuario = field_usuario.getText();
            String contrasena = field_contra.getText();

            System.out.println("Datos capturados:");
            System.out.println("  • Usuario: " + nombreUsuario);
            System.out.println("  • Contraseña: " + (contrasena != null ? "****" : "null"));


            if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
                UtilidadesFX.mostrarAlerta(Alert.AlertType.WARNING, "Campo vacío",
                        "Por favor, ingresa tu nombre de usuario");
                field_usuario.requestFocus();
                return;
            }

            if (contrasena == null || contrasena.trim().isEmpty()) {
                UtilidadesFX.mostrarAlerta(Alert.AlertType.WARNING, "Campo vacío",
                        "Por favor, ingresa tu contraseña");
                field_contra.requestFocus();
                return;
            }

            if (contrasena.length() < 6) {
                UtilidadesFX.mostrarAlerta(Alert.AlertType.WARNING, "Contraseña inválida",
                        "La contraseña debe tener al menos 6 caracteres");
                field_contra.clear();
                field_contra.requestFocus();
                return;
            }

            System.out.println("Delegando a ServicioCuenta.iniciarSesion()...");
            serviceLogin.iniciarSesion(event, nombreUsuario, contrasena);

            System.out.println("Inicio de sesión procesado exitosamente\n");

        } catch (ServiceException e) {
            System.err.println("Error de lógica: " + e.getMessage());

            String titulo = "Error de inicio de sesión";

            switch (e.getCodigo()) {
                case "ERROR_USUARIO_NO_EXISTE":
                    titulo = "Usuario no encontrado";
                    field_usuario.clear();
                    field_contra.clear();
                    field_usuario.requestFocus();
                    break;

                case "ERROR_CONTRA_INCORRECTA":
                    titulo = "Contraseña incorrecta";
                    field_contra.clear();
                    field_contra.requestFocus();
                    break;

                case "CUENTA_INACTIVA":
                    titulo = "Cuenta desactivada";
                    break;

                default:
                    titulo = "Error";
                    break;
            }

            UtilidadesFX.mostrarAlerta(Alert.AlertType.ERROR, titulo, e.getMessage());

        } catch (Exception e) {
            System.err.println("✗ Error inesperado: " + e.getMessage());
            e.printStackTrace();
            UtilidadesFX.mostrarAlerta(Alert.AlertType.ERROR, "Error del sistema",
                    "Ocurrió un error inesperado: " + e.getMessage());
        }
    }

    @FXML
    public void cambiarRegistrar(ActionEvent event) {
        try {
            System.out.println("Navegando a Registro...");
            @Override
                    UtilidadesFX.cambiarEscenaConTransicion(event, "/com/mycompany/bankedsistema/presentacion/Registro.fxml");
        } catch (Exception e) {
            System.err.println("ERROR al cambiar escena:");
            e.printStackTrace();
        }
    }
}
