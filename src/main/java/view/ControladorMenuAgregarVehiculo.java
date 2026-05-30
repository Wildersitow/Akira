package view;

import dao.AutoElectricoDAO;
import dao.BicicletaElectricaDAO;
import dao.MotoElectricaDAO;
import dao.PatinetaElectricaDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import model.*;
import service.*;

public class ControladorMenuAgregarVehiculo {

    @FXML private Pane paneAuto;
    @FXML private Pane paneMoto;
    @FXML private Pane panePatineta;
    @FXML private Pane paneBicicleta;

    @FXML private ComboBox<String> comboTipoVehiculo;

    @FXML private TextField id_auto, marca_auto, modelo_auto, km_auto, bateria_auto;
    @FXML private TextField precio_base, vmax_auto, puertas_auto, pasajeros_auto;
    @FXML private TextField carga_auto, traccion_auto, color_auto, año_auto;

    @FXML private TextField id_moto, marca_moto, modelo_moto, km_moto, baeria_moto;
    @FXML private TextField precio_moto, vmax_moto, color_moto, año_moto;
    @FXML private TextField carga_moto, pasajeros_moto, tipo_moto;

    @FXML private TextField id_patineta, marca_patineta, modelo_patineta, km_patineta;
    @FXML private TextField bateria_patineta, precio_patineta, velocidad_patineta;
    @FXML private TextField año_patineta, pesomax_patineta, peso_patineta, color_patineta;
    @FXML private CheckBox box_patineta;

    @FXML private TextField id_bici, marca_bici, model_bici, km_bici, bateria_bici;
    @FXML private TextField precio_bici, vmax_bici, marchas_bici, tipo_bici;
    @FXML private TextField color_bici, año_bici;
    @FXML private CheckBox box_bici;

    private final AutoElectricoService autoService = new AutoElectricoService();
    private final MotoElectricaService motoService = new MotoElectricaService();
    private final PatinetaElectricaService patinetaService = new PatinetaElectricaService();
    private final BicicletaElectricaService biciService = new BicicletaElectricaService();

    private final UtilidadesFX utilidadesFX = new UtilidadesFX();

    @FXML
    public void initialize() {
        comboTipoVehiculo.getItems().addAll(
                "Auto Eléctrico",
                "Moto Eléctrica",
                "Patineta Eléctrica",
                "Bicicleta Eléctrica"
        );
        ocultarTodosLosPanes();

        comboTipoVehiculo.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, anterior, nuevo) -> {
                    ocultarTodosLosPanes();
                    switch (nuevo) {
                        case "Auto Eléctrico"      -> paneAuto.setVisible(true);
                        case "Moto Eléctrica"      -> paneMoto.setVisible(true);
                        case "Patineta Eléctrica"  -> panePatineta.setVisible(true);
                        case "Bicicleta Eléctrica" -> paneBicicleta.setVisible(true);
                    }
                });
    }

    private void ocultarTodosLosPanes() {
        paneAuto.setVisible(false);
        paneMoto.setVisible(false);
        panePatineta.setVisible(false);
        paneBicicleta.setVisible(false);
    }

    @FXML
    private void agregarAuto() {
        try {
            AutoElectrico auto = new AutoElectrico(
                    Integer.parseInt(año_auto.getText().trim()),
                    Double.parseDouble(km_auto.getText().trim()),
                    Double.parseDouble(bateria_auto.getText().trim()),
                    color_auto.getText().trim(),
                    EstadoVehiculo.DISPONIBLE,
                    id_auto.getText().trim(),
                    marca_auto.getText().trim(),
                    modelo_auto.getText().trim(),
                    Double.parseDouble(precio_base.getText().trim()),
                    0,
                    Integer.parseInt(vmax_auto.getText().trim()),
                    Integer.parseInt(pasajeros_auto.getText().trim()),
                    Integer.parseInt(puertas_auto.getText().trim()),
                    carga_auto.getText().trim(),
                    traccion_auto.getText().trim()
            );
            autoService.guardar(auto);
            utilidadesFX.mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Auto registrado correctamente.");
            limpiarAuto();
        } catch (ServiceException e) {
            utilidadesFX.mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        } catch (NumberFormatException e) {
            utilidadesFX.mostrarAlerta(Alert.AlertType.ERROR, "Error", "Verifica que los campos numéricos sean válidos.");
        }
    }

    @FXML
    private void agregarMoto() {
        try {
            MotoElectrica moto = new MotoElectrica(
                    Integer.parseInt(año_moto.getText().trim()),
                    Double.parseDouble(km_moto.getText().trim()),
                    Double.parseDouble(baeria_moto.getText().trim()),
                    color_moto.getText().trim(),
                    EstadoVehiculo.DISPONIBLE,
                    id_moto.getText().trim(),
                    marca_moto.getText().trim(),
                    modelo_moto.getText().trim(),
                    Double.parseDouble(precio_moto.getText().trim()),
                    0,
                    Integer.parseInt(vmax_moto.getText().trim()),
                    0,
                    tipo_moto.getText().trim(),
                    0.0
            );
            motoService.guardar(moto);
            utilidadesFX.mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Moto registrada correctamente.");
            limpiarMoto();
        } catch (ServiceException e) {
            utilidadesFX.mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        } catch (NumberFormatException e) {
            utilidadesFX.mostrarAlerta(Alert.AlertType.ERROR, "Error", "Verifica que los campos numéricos sean válidos.");
        }
    }

    @FXML
    private void agregarPatineta() {
        try {
            PatinetaElectrica patineta = new PatinetaElectrica(
                    Integer.parseInt(año_patineta.getText().trim()),
                    Double.parseDouble(km_patineta.getText().trim()),
                    Double.parseDouble(bateria_patineta.getText().trim()),
                    color_patineta.getText().trim(),
                    EstadoVehiculo.DISPONIBLE,
                    id_patineta.getText().trim(),
                    marca_patineta.getText().trim(),
                    modelo_patineta.getText().trim(),
                    Double.parseDouble(precio_patineta.getText().trim()),
                    0,
                    Integer.parseInt(velocidad_patineta.getText().trim()),
                    Integer.parseInt(pesomax_patineta.getText().trim()),
                    box_patineta.isSelected(),
                    Integer.parseInt(velocidad_patineta.getText().trim())
            );
            patinetaService.guardar(patineta);
            utilidadesFX.mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Patineta registrada correctamente.");
            limpiarPatineta();
        } catch (ServiceException e) {
            utilidadesFX.mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        } catch (NumberFormatException e) {
            utilidadesFX.mostrarAlerta(Alert.AlertType.ERROR, "Error", "Verifica que los campos numéricos sean válidos.");
        }
    }

    @FXML
    private void agregarBici() {
        try {
            BicicletaElectrica bici = new BicicletaElectrica(
                    Integer.parseInt(año_bici.getText().trim()),
                    Double.parseDouble(km_bici.getText().trim()),
                    Double.parseDouble(bateria_bici.getText().trim()),
                    color_bici.getText().trim(),
                    EstadoVehiculo.DISPONIBLE,
                    id_bici.getText().trim(),
                    marca_bici.getText().trim(),
                    model_bici.getText().trim(),
                    Double.parseDouble(precio_bici.getText().trim()),
                    0,
                    Integer.parseInt(vmax_bici.getText().trim()),
                    "N/A",
                    Integer.parseInt(vmax_bici.getText().trim()),
                    box_bici.isSelected() ? "Pedal" : "Throttle",
                    Integer.parseInt(marchas_bici.getText().trim())
            );
            biciService.guardar(bici);
            utilidadesFX.mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Bicicleta registrada correctamente.");
            limpiarBici();
        } catch (ServiceException e) {
            utilidadesFX.mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        } catch (NumberFormatException e) {
            utilidadesFX.mostrarAlerta(Alert.AlertType.ERROR, "Error", "Verifica que los campos numéricos sean válidos.");
        }
    }

    @FXML
    private void limpiarAuto() {
        id_auto.clear(); marca_auto.clear(); modelo_auto.clear();
        km_auto.clear(); bateria_auto.clear(); precio_base.clear();
        vmax_auto.clear(); puertas_auto.clear(); pasajeros_auto.clear();
        carga_auto.clear(); traccion_auto.clear(); color_auto.clear(); año_auto.clear();
    }

    @FXML
    private void limpiarMoto() {
        id_moto.clear(); marca_moto.clear(); modelo_moto.clear();
        km_moto.clear(); baeria_moto.clear(); precio_moto.clear();
        vmax_moto.clear(); color_moto.clear(); año_moto.clear();
        carga_moto.clear(); pasajeros_moto.clear(); tipo_moto.clear();
    }

    @FXML
    private void limpiarPatineta() {
        id_patineta.clear(); marca_patineta.clear(); modelo_patineta.clear();
        km_patineta.clear(); bateria_patineta.clear(); precio_patineta.clear();
        velocidad_patineta.clear(); año_patineta.clear(); pesomax_patineta.clear();
        peso_patineta.clear(); color_patineta.clear(); box_patineta.setSelected(false);
    }

    @FXML
    private void limpiarBici() {
        id_bici.clear(); marca_bici.clear(); model_bici.clear();
        km_bici.clear(); bateria_bici.clear(); precio_bici.clear();
        vmax_bici.clear(); marchas_bici.clear(); tipo_bici.clear();
        color_bici.clear(); año_bici.clear(); box_bici.setSelected(false);
    }
}