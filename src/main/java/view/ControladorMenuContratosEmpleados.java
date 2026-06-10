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
import model.ContratoAlquiler;
import service.SesionCuenta;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ControladorMenuContratosEmpleados {

    @FXML private Label lblTotalContratos;
    @FXML private Label lblContratosActivos;
    @FXML private Label lblContratosVencidos;
    @FXML private Label lblIngresoTotal;

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> comboEstado;
    @FXML private ComboBox<String> comboPago;
    @FXML private ComboBox<String> comboOrdenar;
    @FXML private Label lblResultados;

    @FXML private TableView<ContratoAlquiler> tablaContratos;
    @FXML private TableColumn<ContratoAlquiler, String> colId;
    @FXML private TableColumn<ContratoAlquiler, String> colFechaInicio;
    @FXML private TableColumn<ContratoAlquiler, String> colFechaFin;
    @FXML private TableColumn<ContratoAlquiler, String> colCliente;
    @FXML private TableColumn<ContratoAlquiler, String> colCedula;
    @FXML private TableColumn<ContratoAlquiler, String> colVehiculo;
    @FXML private TableColumn<ContratoAlquiler, String> colDias;
    @FXML private TableColumn<ContratoAlquiler, String> colPago;
    @FXML private TableColumn<ContratoAlquiler, String> colDescuento;
    @FXML private TableColumn<ContratoAlquiler, String> colPrecio;
    @FXML private TableColumn<ContratoAlquiler, String> colEmpleado;
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
    @FXML private Label detClienteNombre;
    @FXML private Label detClienteCedula;
    @FXML private Label detClienteEmail;
    @FXML private Label detVehiculo;
    @FXML private Label detVehEstado;
    @FXML private Label detEmpleado;

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

        colCliente.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getCliente() != null
                        ? c.getValue().getCliente().getNombreUsuario() : "—"));

        colCedula.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getCliente() != null
                        ? c.getValue().getCliente().getDocumentoId() : "—"));

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

        colEmpleado.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getEmpleado() != null
                        ? c.getValue().getEmpleado().getNombreUsuario() : "—"));

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

        comboPago.setItems(FXCollections.observableArrayList(
                "Todas", "CONTADO", "CREDITO", "TRANSFERENCIA"));

        comboOrdenar.setItems(FXCollections.observableArrayList(
                "Más reciente", "Más antiguo", "Mayor precio", "Menor precio"));
    }

    @FXML
    public void cargarContratos() {
        try {
            listaOriginal = FXCollections.observableArrayList(new ArrayList<>());
            listaFiltrada = new FilteredList<>(listaOriginal, c -> true);
            tablaContratos.setItems(listaFiltrada);
            actualizarKPIs();
            actualizarContadorResultados();
        } catch (Exception e) {
            utilidades.mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudieron cargar los contratos: " + e.getMessage());
        }
    }

    @FXML
    public void aplicarFiltros() {
        if (listaFiltrada == null) return;

        String busqueda = txtBuscar.getText() != null ? txtBuscar.getText().toLowerCase().trim() : "";
        String estado   = comboEstado.getValue();
        String pago     = comboPago.getValue();
        String ordenar  = comboOrdenar.getValue();

        listaFiltrada.setPredicate(contrato -> {
            if (!busqueda.isEmpty()) {
                String clienteNombre = contrato.getCliente() != null
                        ? contrato.getCliente().getNombreUsuario().toLowerCase() : "";
                String cedula = contrato.getCliente() != null
                        ? contrato.getCliente().getDocumentoId().toLowerCase() : "";
                String vehiculo = contrato.getVehiculoElectrico() != null
                        ? (contrato.getVehiculoElectrico().getMarca() + " " +
                        contrato.getVehiculoElectrico().getModelo()).toLowerCase() : "";
                String id = String.valueOf(contrato.getId());

                if (!clienteNombre.contains(busqueda) && !cedula.contains(busqueda)
                        && !vehiculo.contains(busqueda) && !id.contains(busqueda)) {
                    return false;
                }
            }

            if (estado != null && !estado.equals("Todos")) {
                if (contrato.getEstadoContrato() == null
                        || !contrato.getEstadoContrato().equalsIgnoreCase(estado)) {
                    return false;
                }
            }

            if (pago != null && !pago.equals("Todas")) {
                if (contrato.getFormaDePago() == null
                        || !contrato.getFormaDePago().equalsIgnoreCase(pago)) {
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
        comboPago.getSelectionModel().clearSelection();
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

        if (contrato.getCliente() != null) {
            detClienteNombre.setText(contrato.getCliente().getNombreUsuario());
            detClienteCedula.setText(contrato.getCliente().getDocumentoId());
            detClienteEmail.setText(contrato.getCliente().getEmail());
        }

        if (contrato.getVehiculoElectrico() != null) {
            detVehiculo.setText(contrato.getVehiculoElectrico().getMarca()
                    + " " + contrato.getVehiculoElectrico().getModelo());
            detVehEstado.setText(contrato.getVehiculoElectrico().getEstado().name());
        }

        if (contrato.getEmpleado() != null) {
            detEmpleado.setText(contrato.getEmpleado().getNombreUsuario());
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

    @FXML
    public void nuevoContrato() {
        utilidades.mostrarAlerta(Alert.AlertType.INFORMATION, "Nuevo contrato",
                "Aquí se abrirá el formulario para crear un nuevo contrato de alquiler.");
    }

    private void actualizarKPIs() {
        int total     = listaOriginal.size();
        long activos  = listaOriginal.stream()
                .filter(c -> "ACTIVO".equalsIgnoreCase(c.getEstadoContrato())).count();
        long vencidos = listaOriginal.stream()
                .filter(c -> "VENCIDO".equalsIgnoreCase(c.getEstadoContrato())).count();
        double ingresos = listaOriginal.stream()
                .mapToDouble(ContratoAlquiler::getPrecioFinal).sum();

        lblTotalContratos.setText(String.valueOf(total));
        lblContratosActivos.setText(String.valueOf(activos));
        lblContratosVencidos.setText(String.valueOf(vencidos));
        lblIngresoTotal.setText(String.format("$%,.0f", ingresos));
    }

    private void actualizarContadorResultados() {
        int mostrados = tablaContratos.getItems().size();
        lblResultados.setText("Mostrando " + mostrados + " contrato" + (mostrados != 1 ? "s" : ""));
    }

    @FXML public void cambiarInicio(ActionEvent event) {
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