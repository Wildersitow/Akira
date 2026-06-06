package view;

import dao.ContratoDAO;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import model.Contrato;
import service.ServiceException;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ControladorHistorialDeCompras {

    @FXML private TableView<Contrato>           tablaContratos;
    @FXML private TableColumn<Contrato, String> colId;
    @FXML private TableColumn<Contrato, String> colFecha;
    @FXML private TableColumn<Contrato, String> colCliente;
    @FXML private TableColumn<Contrato, String> colCedula;
    @FXML private TableColumn<Contrato, String> colVehiculo;
    @FXML private TableColumn<Contrato, String> colPago;
    @FXML private TableColumn<Contrato, String> colDescuento;
    @FXML private TableColumn<Contrato, String> colPrecio;
    @FXML private TableColumn<Contrato, String> colEmpleado;
    @FXML private TableColumn<Contrato, String> colEstado;

    @FXML private TextField  txtBuscar;
    @FXML private ComboBox<String> comboEstado;
    @FXML private ComboBox<String> comboPago;
    @FXML private ComboBox<String> comboOrdenar;

    @FXML private Label lblTotalContratos;
    @FXML private Label lblContratosActivos;
    @FXML private Label lblIngresoTotal;
    @FXML private Label lblResultados;

    @FXML private HBox  panelDetalle;
    @FXML private Label detId;
    @FXML private Label detFecha;
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
    private final DateTimeFormatter fmt    = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private List<Contrato> todosLosContratos = new ArrayList<>();
    private ObservableList<Contrato> itemsTabla = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarColumnas();
        poblarCombos();
        cargarContratos();
        tablaContratos.setItems(itemsTabla);
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getId())));

        colFecha.setCellValueFactory(c -> {
            var fecha = c.getValue().getFechaVenta();
            return new SimpleStringProperty(fecha != null ? fecha.format(fmt) : "—");
        });

        colCliente.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getCliente().getNombre()));

        colCedula.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getCliente().getDocumentoId()));

        colVehiculo.setCellValueFactory(c -> {
            var v = c.getValue().getVehiculoElectrico();
            return new SimpleStringProperty(v.getMarca() + " " + v.getModelo());
        });

        colPago.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getFormaDePago()));

        colDescuento.setCellValueFactory(c -> {
            double desc = c.getValue().calcularDescuento();
            return new SimpleStringProperty(desc > 0 ? formatearPrecio(desc) : "—");
        });

        colPrecio.setCellValueFactory(c ->
                new SimpleStringProperty(formatearPrecio(c.getValue().getPrecioFinal())));

        colEmpleado.setCellValueFactory(c -> {
            var emp = c.getValue().getEmpleado();
            return new SimpleStringProperty(emp != null ? emp.getNombre() : "—");
        });

        colEstado.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getEstadoContrato()));

        colEstado.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    String color = switch (item.toUpperCase()) {
                        case "COMPLETADO", "PAGADO" -> "#2ecc71";
                        case "PENDIENTE"            -> "#f39c12";
                        case "CANCELADO"            -> "#e74c3c";
                        default                     -> "#cccccc";
                    };
                    setStyle("-fx-text-fill:" + color + "; -fx-font-weight:bold; -fx-font-size:11px;");
                }
            }
        });

        colPrecio.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill:#9B0F1F; -fx-font-weight:bold; -fx-font-size:12px;");
                }
            }
        });
    }

    private void poblarCombos() {
        comboEstado.getItems().addAll(
                "Todos", "COMPLETADO", "PENDIENTE", "CANCELADO", "PAGADO"
        );
        comboPago.getItems().addAll(
                "Todas", "CONTADO", "CRÉDITO", "TRANSFERENCIA", "OTRO"
        );
        comboOrdenar.getItems().addAll(
                "Más reciente", "Más antiguo",
                "Precio: mayor a menor", "Precio: menor a mayor",
                "Cliente A-Z"
        );
    }

    @FXML
    public void cargarContratos() {
        todosLosContratos.clear();
        try {
            ContratoDAO dao = new ContratoDAO();
            todosLosContratos = dao.obtenerTodos(); // ← esto faltaba
        } catch (Exception e) {
            System.err.println("Error cargando contratos: " + e.getMessage());
        }
        actualizarKPIs(todosLosContratos);
        aplicarFiltros();
    }

    @FXML
    public void aplicarFiltros() {
        List<Contrato> filtrados = new ArrayList<>(todosLosContratos);

        String buscar = txtBuscar != null && txtBuscar.getText() != null
                ? txtBuscar.getText().trim().toLowerCase() : "";
        if (!buscar.isEmpty()) {
            filtrados = filtrados.stream()
                    .filter(c ->
                            c.getCliente().getNombre().toLowerCase().contains(buscar)
                                    || c.getVehiculoElectrico().getMarca().toLowerCase().contains(buscar)
                                    || c.getVehiculoElectrico().getModelo().toLowerCase().contains(buscar)
                                    || String.valueOf(c.getId()).contains(buscar)
                    )
                    .collect(Collectors.toList());
        }

        String estado = comboEstado.getValue();
        if (estado != null && !estado.equals("Todos")) {
            filtrados = filtrados.stream()
                    .filter(c -> c.getEstadoContrato().equalsIgnoreCase(estado))
                    .collect(Collectors.toList());
        }

        String pago = comboPago.getValue();
        if (pago != null && !pago.equals("Todas")) {
            filtrados = filtrados.stream()
                    .filter(c -> c.getFormaDePago().equalsIgnoreCase(pago))
                    .collect(Collectors.toList());
        }

        String orden = comboOrdenar.getValue();
        if (orden != null) {
            filtrados = switch (orden) {
                case "Más reciente"          -> filtrados.stream()
                        .sorted((a, b) -> b.getFechaVenta().compareTo(a.getFechaVenta()))
                        .collect(Collectors.toList());
                case "Más antiguo"           -> filtrados.stream()
                        .sorted((a, b) -> a.getFechaVenta().compareTo(b.getFechaVenta()))
                        .collect(Collectors.toList());
                case "Precio: mayor a menor" -> filtrados.stream()
                        .sorted((a, b) -> Double.compare(b.getPrecioFinal(), a.getPrecioFinal()))
                        .collect(Collectors.toList());
                case "Precio: menor a mayor" -> filtrados.stream()
                        .sorted((a, b) -> Double.compare(a.getPrecioFinal(), b.getPrecioFinal()))
                        .collect(Collectors.toList());
                case "Cliente A-Z"           -> filtrados.stream()
                        .sorted((a, b) -> a.getCliente().getNombre()
                                .compareToIgnoreCase(b.getCliente().getNombre()))
                        .collect(Collectors.toList());
                default -> filtrados;
            };
        }

        itemsTabla.setAll(filtrados);
        if (lblResultados != null)
            lblResultados.setText("Mostrando " + filtrados.size() + " contrato(s)");
    }

    @FXML
    public void limpiarFiltros() {
        if (txtBuscar != null)    txtBuscar.clear();
        comboEstado.setValue(null);
        comboPago.setValue(null);
        comboOrdenar.setValue(null);
        aplicarFiltros();
    }

    private void actualizarKPIs(List<Contrato> lista) {
        if (lista == null) return;

        long activos = lista.stream()
                .filter(c -> !"CANCELADO".equalsIgnoreCase(c.getEstadoContrato()))
                .count();
        double ingresos = lista.stream()
                .filter(c -> "COMPLETADO".equalsIgnoreCase(c.getEstadoContrato())
                        || "PAGADO".equalsIgnoreCase(c.getEstadoContrato()))
                .mapToDouble(Contrato::getPrecioFinal)
                .sum();

        if (lblTotalContratos  != null) lblTotalContratos.setText(String.valueOf(lista.size()));
        if (lblContratosActivos != null) lblContratosActivos.setText(String.valueOf(activos));
        if (lblIngresoTotal     != null) lblIngresoTotal.setText(formatearPrecio(ingresos));
    }

    @FXML
    public void onFilaDobleClick(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
            Contrato sel = tablaContratos.getSelectionModel().getSelectedItem();
            if (sel != null) mostrarDetalle(sel);
        }
    }

    private void mostrarDetalle(Contrato c) {
        detId.setText(String.valueOf(c.getId()));
        detFecha.setText(c.getFechaVenta() != null ? c.getFechaVenta().format(fmt) : "—");
        detEstado.setText(c.getEstadoContrato());
        detPago.setText(c.getFormaDePago());
        detDescuento.setText(c.calcularDescuento() > 0
                ? "-" + formatearPrecio(c.calcularDescuento()) : "Sin descuento");
        detPrecio.setText(formatearPrecio(c.getPrecioFinal()));

        detClienteNombre.setText(c.getCliente().getNombre());
        detClienteCedula.setText(c.getCliente().getDocumentoId());
        detClienteEmail.setText(c.getCliente().getEmail() != null
                ? c.getCliente().getEmail() : "—");

        var v = c.getVehiculoElectrico();
        detVehiculo.setText(v.getMarca() + " " + v.getModelo() + " (" + v.getAnio() + ")");
        detVehEstado.setText(v.getEstado().name());

        detEmpleado.setText(c.getEmpleado() != null ? c.getEmpleado().getNombre() : "—");

        if (!panelDetalle.isVisible()) {
            panelDetalle.setVisible(true);
            panelDetalle.setManaged(true);
            panelDetalle.setPrefHeight(0);

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.millis(220),
                            new KeyValue(panelDetalle.prefHeightProperty(), 200.0))
            );
            timeline.play();
        }
    }

    @FXML
    public void cerrarDetalle() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(200),
                        new KeyValue(panelDetalle.prefHeightProperty(), 0.0))
        );
        timeline.setOnFinished(e -> {
            panelDetalle.setVisible(false);
            panelDetalle.setManaged(false);
        });
        timeline.play();
    }

    private String formatearPrecio(double valor) {
        return "$" + String.format("%,.0f", valor).replace(",", ".");
    }

    @FXML public void cambiarInicio(ActionEvent event) {
        utilidades.cambiarEscenaConTransicion(event, "/FXML/MenuPrincipalEmpleado.fxml");
    }
    @FXML public void cambiarAgregarVehiculo(ActionEvent event) {
        utilidades.cambiarEscenaConTransicion(event, "/FXML/MenuAgregarVehiculo.fxml");
    }
    @FXML public void cambiarFlota(ActionEvent event) {
        utilidades.cambiarEscenaConTransicion(event, "/FXML/MenuFlotaAdmin.fxml");
    }
    @FXML public void cambiarAsistente(ActionEvent event) {
        utilidades.cambiarEscenaConTransicion(event, "/FXML/AsistenteAI.fxml");
    }
}