package view;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import service.ServiceCuenta;
import service.ServiceException;
import javafx.fxml.FXML;

public class ControladorRegister {

    private Stage stage;
    private Scene scene;
    private ServiceCuenta serviceCuenta;
    private final UtilidadesFX utilidades;

    @FXML
    private TextField field_usuario;

    @FXML
    private TextField field_correo;

    @FXML
    private TextField field_id;

    @FXML
    private TextField field_contra;

    @FXML
    private RadioButton radio_empleado;

    @FXML
    private RadioButton radio_cliente;

    @FXML
    private ToggleGroup cuenta;

    public ControladorRegister() {
        this.utilidades = new UtilidadesFX();
    }

    public ControladorRegister(UtilidadesFX utilidades) {
        this.utilidades = utilidades;
    }

    @FXML
    public void initialize() {
        serviceCuenta = new ServiceCuenta();
        System.out.println("RegistroController inicializado correctamente");
    }

    @FXML
    private void handleRegistrar(ActionEvent event) {
        try {
            System.out.println("CONTROLADOR: Iniciando proceso de registro");

            //Recibir los datos del formulario
            String nombreUsuario = field_usuario.getText();
            String correo = field_correo.getText();
            String documentoid = field_id.getText();
            String contraseña = field_contra.getText();

            System.out.println("Datos capturados:");
            System.out.println("Usuario: " + nombreUsuario);
            System.out.println("Correo: " + correo);
            System.out.println("Documento de Identidad: " + documentoid);
            System.out.println("Contraseña: " + (contraseña != null ? "****" : "null"));

            // Obtener el rol seleccionado
            String rol = null;
            if (radio_cliente != null && radio_cliente.isSelected()) {
                rol = "cliente";
                System.out.println("Rol: Cliente");
            } else if (radio_empleado != null && radio_empleado.isSelected()) {
                rol = "empleado";
                System.out.println("Rol: Empleado");
            } else {
                System.out.println("Rol: No seleccionado");
            }

            // Validaciones de los campos

            if (correo == null || correo.trim().isEmpty()) {
                utilidades.mostrarAlerta(Alert.AlertType.WARNING, "Campo vacío",
                        "Por favor, ingresa tu correo electrónico");
                field_correo.requestFocus();
                return;
            }

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

            if (rol == null) {
                utilidades.mostrarAlerta(Alert.AlertType.WARNING, "Rol no seleccionado",
                        "Por favor, selecciona un rol (Cliente o Empleado)");
                return;
            }

            // Call out a la clase de logica
            System.out.println("Delegando a ServicioCuenta.registrarUsuario()...");
            serviceCuenta.registrarUsuario(event, nombreUsuario, correo, documentoid, contraseña, rol);

            System.out.println("Registro completado exitosamente\n");

        } catch (ServiceException e) {
            System.err.println("Error de lógica: " + e.getMessage());

            String titulo = "Error en el registro";
            switch (e.getCodigo()) {
                case "CORREO_INVALIDO":
                    titulo = "Correo inválido";
                    field_correo.clear();
                    field_correo.requestFocus();
                    break;
                case "USUARIO_CORTO":
                    titulo = "Usuario inválido";
                    field_usuario.clear();
                    field_usuario.requestFocus();
                    break;
                case "CONTRASEÑA_CORTA":
                    titulo = "Contraseña inválida";
                    field_contra.clear();
                    field_contra.requestFocus();
                    break;
                case "USUARIO_DUPLICADO":
                    titulo = "Usuario ya existe";
                    field_usuario.clear();
                    field_usuario.requestFocus();
                    break;
            }

            utilidades.mostrarAlerta(Alert.AlertType.ERROR, titulo, e.getMessage());

        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
            utilidades.mostrarAlerta(Alert.AlertType.ERROR, "Error del sistema",
                    "Ocurrió un error inesperado: " + e.getMessage());
        }
    }

    @FXML
    public void cambiarIniciarSesion(ActionEvent event) {
        try {
            System.out.println("Navegando a Iniciar Sesión...");
            utilidades.cambiarEscenaConTransicion(event, "/FXML/Login.fxml");
        } catch (Exception e) {
            System.err.println("ERROR al cambiar escena:");
            e.printStackTrace();
        }
    }

}
