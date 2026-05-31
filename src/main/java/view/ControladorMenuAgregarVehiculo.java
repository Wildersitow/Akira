package view;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import model.*;
import service.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

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

    @FXML private ImageView imgAuto, imgMoto, imgPatineta, imgBici;

    @FXML private TableView<VehiculoFila> tablaVehiculos;
    @FXML private TableColumn<VehiculoFila, String> colTipo, colId, colMarca, colModelo;
    @FXML private TableColumn<VehiculoFila, String> colFecha, colAutonomia, colBateria;
    @FXML private TableColumn<VehiculoFila, String> colPrecio, colVelocidad;

    @FXML private TextField campoBuscar;

    private final ObservableList<VehiculoFila> listaVehiculos = FXCollections.observableArrayList();
    private final ObservableList<VehiculoFila> listaFiltrada = FXCollections.observableArrayList();

    private String rutaImagenAuto = null;
    private String rutaImagenMoto = null;
    private String rutaImagenPatineta = null;
    private String rutaImagenBici = null;

    private final AutoElectricoService autoService = new AutoElectricoService();
    private final MotoElectricaService motoService = new MotoElectricaService();
    private final PatinetaElectricaService patinetaService = new PatinetaElectricaService();
    private final BicicletaElectricaService biciService = new BicicletaElectricaService();

    private final UtilidadesFX utilidadesFX = new UtilidadesFX();

    private static final String CARPETA_IMAGENES = "src/main/resources/Imagenes/";

    @FXML
    public void initialize() {
        comboTipoVehiculo.getItems().addAll(
                "Auto Eléctrico", "Moto Eléctrica",
                "Patineta Eléctrica", "Bicicleta Eléctrica"
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

        configurarColumnas();
        configurarBuscador();
        Platform.runLater(() -> cargarTabla());
    }

    private void configurarColumnas() {
        colTipo.setCellValueFactory(d -> d.getValue().tipoProperty());
        colId.setCellValueFactory(d -> d.getValue().idProperty());
        colMarca.setCellValueFactory(d -> d.getValue().marcaProperty());
        colModelo.setCellValueFactory(d -> d.getValue().modeloProperty());
        colFecha.setCellValueFactory(d -> d.getValue().fechaProperty());
        colAutonomia.setCellValueFactory(d -> d.getValue().autonomiaProperty());
        colBateria.setCellValueFactory(d -> d.getValue().bateriaProperty());
        colPrecio.setCellValueFactory(d -> d.getValue().precioProperty());
        colVelocidad.setCellValueFactory(d -> d.getValue().velocidadProperty());
        tablaVehiculos.setItems(listaFiltrada);
    }

    private void configurarBuscador() {
        campoBuscar.textProperty().addListener((obs, anterior, nuevo) -> {
            listaFiltrada.clear();
            if (nuevo == null || nuevo.isBlank()) {
                listaFiltrada.addAll(listaVehiculos);
            } else {
                String textoBajo = nuevo.toLowerCase();
                for (VehiculoFila fila : listaVehiculos) {
                    if (fila.getTipo().toLowerCase().contains(textoBajo)
                            || fila.getId().toLowerCase().contains(textoBajo)
                            || fila.getMarca().toLowerCase().contains(textoBajo)
                            || fila.getModelo().toLowerCase().contains(textoBajo)
                            || fila.getPrecio().toLowerCase().contains(textoBajo)
                            || fila.getAutonomia().toLowerCase().contains(textoBajo)) {
                        listaFiltrada.add(fila);
                    }
                }
            }
        });
    }

    private void cargarTabla() {
        listaVehiculos.clear();
        String fecha = LocalDate.now().toString();
        try {
            for (AutoElectrico a : autoService.obtenerTodos())
                listaVehiculos.add(new VehiculoFila("Auto", a.getId(), a.getMarca(),
                        a.getModelo(), fecha, String.valueOf(a.getAutonomiaKm()),
                        String.valueOf(a.getCapacidadBateria()),
                        String.valueOf(a.getPrecioBase()), String.valueOf(a.getVelocidadMaxima())));

            for (MotoElectrica m : motoService.obtenerTodos())
                listaVehiculos.add(new VehiculoFila("Moto", m.getId(), m.getMarca(),
                        m.getModelo(), fecha, String.valueOf(m.getAutonomiaKm()),
                        String.valueOf(m.getCapacidadBateria()),
                        String.valueOf(m.getPrecioBase()), String.valueOf(m.getVelocidadMaxima())));

            for (PatinetaElectrica p : patinetaService.obtenerTodos())
                listaVehiculos.add(new VehiculoFila("Patineta", p.getId(), p.getMarca(),
                        p.getModelo(), fecha, String.valueOf(p.getAutonomiaKm()),
                        String.valueOf(p.getCapacidadBateria()),
                        String.valueOf(p.getPrecioBase()), String.valueOf(p.getVelocidadMaxima())));

            for (BicicletaElectrica b : biciService.obtenerTodos())
                listaVehiculos.add(new VehiculoFila("Bicicleta", b.getId(), b.getMarca(),
                        b.getModelo(), fecha, String.valueOf(b.getAutonomiaKm()),
                        String.valueOf(b.getCapacidadBateria()),
                        String.valueOf(b.getPrecioBase()), String.valueOf(b.getVelocidadMaxima())));

            listaFiltrada.clear();
            listaFiltrada.addAll(listaVehiculos);

        } catch (ServiceException e) {
            utilidadesFX.mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    private void ocultarTodosLosPanes() {
        paneAuto.setVisible(false);
        paneMoto.setVisible(false);
        panePatineta.setVisible(false);
        paneBicicleta.setVisible(false);
    }

    @FXML
    private void seleccionarImagenAuto() {
        rutaImagenAuto = seleccionarImagen(imgAuto);
    }

    @FXML
    private void seleccionarImagenMoto() {
        rutaImagenMoto = seleccionarImagen(imgMoto);
    }

    @FXML
    private void seleccionarImagenPatineta() {
        rutaImagenPatineta = seleccionarImagen(imgPatineta);
    }

    @FXML
    private void seleccionarImagenBici() {
        rutaImagenBici = seleccionarImagen(imgBici);
    }

    private String seleccionarImagen(ImageView imageView) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        File archivo = fileChooser.showOpenDialog(null);
        if (archivo != null) {
            try {
                Path destino = Paths.get(CARPETA_IMAGENES + archivo.getName());
                Files.copy(archivo.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
                Image imagen = new Image(archivo.toURI().toString());
                imageView.setImage(imagen);
                return archivo.getName();
            } catch (IOException e) {
                utilidadesFX.mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo copiar la imagen.");
            }
        }
        return null;
    }


    @FXML
    private void eliminarVehiculo() {
        VehiculoFila seleccionado = tablaVehiculos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            utilidadesFX.mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecciona un vehículo de la tabla.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro de que deseas eliminar el vehículo " +
                seleccionado.getMarca() + " " + seleccionado.getModelo() + "?");

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                try {
                    long id = Long.parseLong(seleccionado.getId());
                    switch (seleccionado.getTipo()) {
                        case "Auto"      -> autoService.eliminar(id);
                        case "Moto"      -> motoService.eliminar(id);
                        case "Patineta"  -> patinetaService.eliminar(id);
                        case "Bicicleta" -> biciService.eliminar(id);
                    }
                    utilidadesFX.mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Vehículo eliminado correctamente.");
                    cargarTabla();
                } catch (ServiceException e) {
                    utilidadesFX.mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
                }
            }
        });
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
            auto.setImagen(rutaImagenAuto);
            autoService.guardar(auto);
            utilidadesFX.mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Auto registrado correctamente.");
            limpiarAuto();
            cargarTabla();
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
            moto.setImagen(rutaImagenMoto);
            motoService.guardar(moto);
            utilidadesFX.mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Moto registrada correctamente.");
            limpiarMoto();
            cargarTabla();
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
            patineta.setImagen(rutaImagenPatineta);
            patinetaService.guardar(patineta);
            utilidadesFX.mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Patineta registrada correctamente.");
            limpiarPatineta();
            cargarTabla();
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
            bici.setImagen(rutaImagenBici);
            biciService.guardar(bici);
            utilidadesFX.mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Bicicleta registrada correctamente.");
            limpiarBici();
            cargarTabla();
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
        carga_auto.clear(); traccion_auto.clear(); color_auto.clear();
        año_auto.clear(); imgAuto.setImage(null); rutaImagenAuto = null;
    }

    @FXML
    private void limpiarMoto() {
        id_moto.clear(); marca_moto.clear(); modelo_moto.clear();
        km_moto.clear(); baeria_moto.clear(); precio_moto.clear();
        vmax_moto.clear(); color_moto.clear(); año_moto.clear();
        carga_moto.clear(); pasajeros_moto.clear(); tipo_moto.clear();
        imgMoto.setImage(null); rutaImagenMoto = null;
    }

    @FXML
    private void limpiarPatineta() {
        id_patineta.clear(); marca_patineta.clear(); modelo_patineta.clear();
        km_patineta.clear(); bateria_patineta.clear(); precio_patineta.clear();
        velocidad_patineta.clear(); año_patineta.clear(); pesomax_patineta.clear();
        peso_patineta.clear(); color_patineta.clear(); box_patineta.setSelected(false);
        imgPatineta.setImage(null); rutaImagenPatineta = null;
    }

    @FXML
    private void limpiarBici() {
        id_bici.clear(); marca_bici.clear(); model_bici.clear();
        km_bici.clear(); bateria_bici.clear(); precio_bici.clear();
        vmax_bici.clear(); marchas_bici.clear(); tipo_bici.clear();
        color_bici.clear(); año_bici.clear(); box_bici.setSelected(false);
        imgBici.setImage(null); rutaImagenBici = null;
    }
}