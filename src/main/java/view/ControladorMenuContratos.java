package view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import model.Cliente;
import model.Contrato;
import model.ContratoAlquiler;
import service.SesionCuenta;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ControladorMenuContratos {

    @FXML private Label lblTotalContratos;
    @FXML private Label lblContratosActivos;
    @FXML private Label lblTotalGastado;

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> comboEstado;
    @FXML private ComboBox<String> comboOrdenar;
    @FXML private Label lblResultados;

    @FXML private TableView<ContratoAlquiler> tablaContratos;
    @FXML private TableColumn<ContratoAlquiler, String> colId;
    @FXML private TableColumn<ContratoAlquiler, String> colFechaInicio;
    @FXML private TableColumn<ContratoAlquiler, String> colFechaFin;
    @FXML private TableColumn<ContratoAlquiler, String> colVehiculo;
    @FXML private TableColumn<ContratoAlquiler, String> colDias;
    @FXML private TableColumn<ContratoAlquiler, String> colPago;
    @FXML private TableColumn<ContratoAlquiler, String> colDescuento;
    @FXML private TableColumn<ContratoAlquiler, String> colPrecio;
    @FXML private TableColumn<ContratoAlquiler, String> colEstado;

    @FXML private HBox panelDetalle;
    @FXML private Label detId;
    @FXML private Label detFechaInicio;
    @FXML private Label detFechaFin;
    @FXML private Label detDias;
    @FXML private Label detEstado;
    @FXML private Label detPago;
    @FXML private Label detDescuento;
    @FXML private Label detPrecio;
    @FXML private Label detVehiculo;
    @FXML private Label detVehEstado;

    private final UtilidadesFX utilidades = new UtilidadesFX();
    private ObservableList<ContratoAlquiler> listaOriginal = FXCollections.observableArrayList();
    private FilteredList<ContratoAlquiler> listaFiltrada;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarCombos();
        cargarContratos();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getId())));

        colFechaInicio.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getFechaInicio().format(FORMATO_FECHA)));

        colFechaFin.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getFechaFin().format(FORMATO_FECHA)));

        colVehiculo.setCellValueFactory(c -> {
            if (c.getValue().getVehiculoElectrico() == null) return new SimpleStringProperty("—");
            var v = c.getValue().getVehiculoElectrico();
            return new SimpleStringProperty(v.getMarca() + " " + v.getModelo());
        });

        colDias.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getDiasAlquilados())));

        colPago.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getFormaDePago() != null
                        ? c.getValue().getFormaDePago() : "—"));

        colDescuento.setCellValueFactory(c ->
                new SimpleStringProperty(String.format("$%,.0f", c.getValue().calcularDescuento())));

        colPrecio.setCellValueFactory(c ->
                new SimpleStringProperty(String.format("$%,.0f", c.getValue().getPrecioFinal())));

        colEstado.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getEstadoContrato() != null
                        ? c.getValue().getEstadoContrato() : "—"));

        colEstado.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item.toUpperCase()) {
                        case "ACTIVO"     -> setStyle("-fx-text-fill: #4caf50; -fx-font-weight: bold;");
                        case "FINALIZADO" -> setStyle("-fx-text-fill: #888888;");
                        case "VENCIDO"    -> setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
                        default           -> setStyle("-fx-text-fill: #ffffff;");
                    }
                }
            }
        });
    }

    private void configurarCombos() {
        comboEstado.setItems(FXCollections.observableArrayList(
                "Todos", "ACTIVO", "FINALIZADO", "VENCIDO", "PENDIENTE"));

        comboOrdenar.setItems(FXCollections.observableArrayList(
                "Más reciente", "Más antiguo", "Mayor precio", "Menor precio"));
    }

    @FXML
    public void cargarContratos() {
        try {
            listaOriginal  = FXCollections.observableArrayList(obtenerContratosDelClienteActual());
            listaFiltrada  = new FilteredList<>(listaOriginal, c -> true);
            tablaContratos.setItems(listaFiltrada);
            actualizarKPIs();
            actualizarContadorResultados();
        } catch (Exception e) {
            utilidades.mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudieron cargar tus contratos: " + e.getMessage());
        }
    }

    private List<ContratoAlquiler> obtenerContratosDelClienteActual() {
        if (!SesionCuenta.haySesionActiva()) return new ArrayList<>();
        if (!(SesionCuenta.getUsuarioActual() instanceof Cliente cliente)) return new ArrayList<>();
        if (cliente.getContratos() == null) return new ArrayList<>();

        return cliente.getContratos().stream()
                .filter(c -> c instanceof ContratoAlquiler)
                .map(c -> (ContratoAlquiler) c)
                .collect(Collectors.toList());
    }

    @FXML
    public void aplicarFiltros() {
        if (listaFiltrada == null) return;

        String busqueda = txtBuscar.getText() != null ? txtBuscar.getText().toLowerCase().trim() : "";
        String estado   = comboEstado.getValue();
        String ordenar  = comboOrdenar.getValue();

        listaFiltrada.setPredicate(contrato -> {
            if (!busqueda.isEmpty()) {
                String vehiculo = contrato.getVehiculoElectrico() != null
                        ? (contrato.getVehiculoElectrico().getMarca() + " " +
                        contrato.getVehiculoElectrico().getModelo()).toLowerCase() : "";
                String id    = String.valueOf(contrato.getId());
                String fecha = contrato.getFechaInicio().format(FORMATO_FECHA);

                if (!vehiculo.contains(busqueda) && !id.contains(busqueda)
                        && !fecha.contains(busqueda)) {
                    return false;
                }
            }

            if (estado != null && !estado.equals("Todos")) {
                if (contrato.getEstadoContrato() == null
                        || !contrato.getEstadoContrato().equalsIgnoreCase(estado)) {
                    return false;
                }
            }

            return true;
        });

        if (ordenar != null) {
            List<ContratoAlquiler> lista = new ArrayList<>(listaFiltrada);
            switch (ordenar) {
                case "Más reciente" -> lista.sort((a, b) -> b.getFechaInicio().compareTo(a.getFechaInicio()));
                case "Más antiguo"  -> lista.sort((a, b) -> a.getFechaInicio().compareTo(b.getFechaInicio()));
                case "Mayor precio" -> lista.sort((a, b) -> Double.compare(b.getPrecioFinal(), a.getPrecioFinal()));
                case "Menor precio" -> lista.sort((a, b) -> Double.compare(a.getPrecioFinal(), b.getPrecioFinal()));
            }
            tablaContratos.setItems(FXCollections.observableArrayList(lista));
        } else {
            tablaContratos.setItems(listaFiltrada);
        }

        actualizarContadorResultados();
    }

    @FXML
    public void limpiarFiltros() {
        txtBuscar.clear();
        comboEstado.getSelectionModel().clearSelection();
        comboOrdenar.getSelectionModel().clearSelection();
        aplicarFiltros();
    }

    @FXML
    public void onFilaDobleClick(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
            ContratoAlquiler seleccionado = tablaContratos.getSelectionModel().getSelectedItem();
            if (seleccionado != null) mostrarDetalle(seleccionado);
        }
    }

    private void mostrarDetalle(ContratoAlquiler contrato) {
        detId.setText(String.valueOf(contrato.getId()));
        detFechaInicio.setText(contrato.getFechaInicio().format(FORMATO_FECHA));
        detFechaFin.setText(contrato.getFechaFin().format(FORMATO_FECHA));
        detDias.setText(contrato.getDiasAlquilados() + " días");
        detEstado.setText(contrato.getEstadoContrato() != null ? contrato.getEstadoContrato() : "—");
        detPago.setText(contrato.getFormaDePago() != null ? contrato.getFormaDePago() : "—");
        detDescuento.setText(String.format("$%,.0f", contrato.calcularDescuento()));
        detPrecio.setText(String.format("$%,.0f", contrato.getPrecioFinal()));

        if (contrato.getVehiculoElectrico() != null) {
            detVehiculo.setText(contrato.getVehiculoElectrico().getMarca()
                    + " " + contrato.getVehiculoElectrico().getModelo());
            detVehEstado.setText(contrato.getVehiculoElectrico().getEstado().name());
        } else {
            detVehiculo.setText("—");
            detVehEstado.setText("—");
        }

        panelDetalle.setVisible(true);
        panelDetalle.setManaged(true);
    }

    @FXML
    public void cerrarDetalle() {
        panelDetalle.setVisible(false);
        panelDetalle.setManaged(false);
        tablaContratos.getSelectionModel().clearSelection();
    }

    private void actualizarKPIs() {
        int total    = listaOriginal.size();
        long activos = listaOriginal.stream()
                .filter(c -> "ACTIVO".equalsIgnoreCase(c.getEstadoContrato())).count();
        double gastado = listaOriginal.stream()
                .mapToDouble(ContratoAlquiler::getPrecioFinal).sum();

        lblTotalContratos.setText(String.valueOf(total));
        lblContratosActivos.setText(String.valueOf(activos));
        lblTotalGastado.setText(String.format("$%,.0f", gastado));
    }

    private void actualizarContadorResultados() {
        int mostrados = tablaContratos.getItems().size();
        lblResultados.setText("Mostrando " + mostrados + " contrato" + (mostrados != 1 ? "s" : ""));
    }

    @FXML
    public void cambiarInicio(ActionEvent event) {
        try { utilidades.cambiarEscenaConTransicion(event, "/FXML/MenuPrincipal.fxml"); }
        catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    public void cambiarCompras(ActionEvent event) {
        try { utilidades.cambiarEscenaConTransicion(event, "/FXML/MenuCompras.fxml"); }
        catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    public void cambiarFlota(ActionEvent event) {
        try { utilidades.cambiarEscenaConTransicion(event, "/FXML/MenuFlota.fxml"); }
        catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    public void cambiarAsistente(ActionEvent event) {
        try { utilidades.cambiarEscenaConTransicion(event, "/FXML/AsistenteAI.fxml"); }
        catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    public void cerrarSesion(ActionEvent event) {
        SesionCuenta.cerrarSesion();
        try { utilidades.cambiarEscenaConTransicion(event, "/FXML/MenuInicio.fxml"); }
        catch (Exception e) { e.printStackTrace(); }
    }
}