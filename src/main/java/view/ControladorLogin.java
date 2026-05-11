package view;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import service.ServiceCuenta;
import service.ServiceException;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;

public class ControladorLogin {

    private Stage stage;
    private Scene scene;
    private Parent root;

    private final UtilidadesFX utilidades;
    private ServiceCuenta serviceCuenta;



    @FXML
    private TextField field_usuario;

    @FXML
    private PasswordField field_contra;

    public ControladorLogin(UtilidadesFX utilidades) {
        this.utilidades = utilidades;
    }

    @FXML
    public void initialize() {
        serviceCuenta = new ServiceCuenta();
        System.out.println("✓ IniciarSesiónController inicializado correctamente");
    }

    @FXML
    private void handleIniciarSesion(ActionEvent event) {
        try {
            System.out.println("\n=== CONTROLADOR: Iniciando proceso de inicio de sesión ===");

            String nombreUsuario = field_usuario.getText();
            String contraseña = field_contra.getText();

            System.out.println("Datos capturados:");
            System.out.println("  • Usuario: " + nombreUsuario);
            System.out.println("  • Contraseña: " + (contraseña != null ? "****" : "null"));


            if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
                utilidades.mostrarAlerta(Alert.AlertType.WARNING, "Campo vacío",
                        "Por favor, ingresa tu nombre de usuario");
                field_usuario.requestFocus();
                return;
            }

            if (contraseña == null || contraseña.trim().isEmpty()) {
                utilidades.mostrarAlerta(Alert.AlertType.WARNING, "Campo vacío",
                        "Por favor, ingresa tu contraseña");
                field_contra.requestFocus();
                return;
            }

            if (contraseña.length() < 6) {
                utilidades.mostrarAlerta(Alert.AlertType.WARNING, "Contraseña inválida",
                        "La contraseña debe tener al menos 6 caracteres");
                field_contra.clear();
                field_contra.requestFocus();
                return;
            }

            System.out.println("Delegando a ServicioCuenta.iniciarSesion()...");
            serviceCuenta.iniciarSesion(event, nombreUsuario, contraseña);

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

            utilidades.mostrarAlerta(Alert.AlertType.ERROR, titulo, e.getMessage());

        } catch (Exception e) {
            System.err.println("✗ Error inesperado: " + e.getMessage());
            e.printStackTrace();
            utilidades.mostrarAlerta(Alert.AlertType.ERROR, "Error del sistema",
                    "Ocurrió un error inesperado: " + e.getMessage());
        }
    }

    @FXML
    public void cambiarRegistrar(ActionEvent event) {
        try {
            System.out.println("Navegando a Registro...");
                    utilidades.cambiarEscenaConTransicion(event, "/src/main/resources/FXML/Register.fxml");
        } catch (Exception e) {
            System.err.println("ERROR al cambiar escena:");
            e.printStackTrace();
        }
    }
}
