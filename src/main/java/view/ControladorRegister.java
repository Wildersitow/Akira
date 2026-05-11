package view;

import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import service.ServiceCuenta;

public class ControladorRegister {

    private Stage stage;
    private Scene scene;
    private ServiceCuenta serviceCuenta;

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

    @FXML
    public void initialize() {
        serviceCuenta = new ServiceCuenta();
        System.out.println("RegistroController inicializado correctamente");
    }

}
