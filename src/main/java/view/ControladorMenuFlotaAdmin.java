package view;

import dao.AutoElectricoDAO;
import dao.BicicletaElectricaDAO;
import dao.MotoElectricaDAO;
import dao.PatinetaElectricaDAO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.*;
import service.ServiceException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ControladorMenuFlotaAdmin {

    @FXML private Label lblTotalVehiculos;
    @FXML private Label lblDisponibles;
    @FXML private Label lblAlquilados;
    @FXML private Label lblVendidos;
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> comboTipo;
    @FXML private ComboBox<String> comboEstado;
    @FXML private ComboBox<String> comboMarca;
    @FXML private ComboBox<String> comboOrdenar;
    @FXML private FlowPane flowVehiculos;
    @FXML private VBox panelVacio;
    @FXML private Label lblResultados;

    private final UtilidadesFX utilidades = new UtilidadesFX();
    private final AutoElectricoDAO autoDAO = new AutoElectricoDAO();
    private final MotoElectricaDAO motoDAO = new MotoElectricaDAO();
    private final BicicletaElectricaDAO biciDAO = new BicicletaElectricaDAO();
    private final PatinetaElectricaDAO patiDAO = new PatinetaElectricaDAO();

    private List<VehiculoElectrico> todosLosVehiculos = new ArrayList<>();
    private List<VehiculoElectrico> vehiculosFiltrados = new ArrayList<>();

    @FXML
    public void initialize() {
        comboTipo.getItems().addAll("Todos", "Auto Eléctrico", "Moto Eléctrica", "Bicicleta Eléctrica", "Patineta Eléctrica");
        comboEstado.getItems().addAll("Todos", "DISPONIBLE", "ALQUILADO", "VENDIDO", "MANTENIMIENTO");
        comboOrdenar.getItems().addAll("Relevancia", "Precio ascendente", "Precio descendente", "Marca A-Z");
        comboTipo.getSelectionModel().selectFirst();
        comboEstado.getSelectionModel().selectFirst();
        comboOrdenar.getSelectionModel().selectFirst();
        cargarVehiculos();
    }

    @FXML
    public void cargarVehiculos() {
        todosLosVehiculos.clear();
        try {
            for (AutoElectrico v : autoDAO.obtenerTodos()) todosLosVehiculos.add(v);
        } catch (ServiceException ignored) {}
        try {
            for (MotoElectrica v : motoDAO.obtenerTodos()) todosLosVehiculos.add(v);
        } catch (ServiceException ignored) {}
        try {
            for (BicicletaElectrica v : biciDAO.obtenerTodos()) todosLosVehiculos.add(v);
        } catch (ServiceException ignored) {}
        try {
            for (PatinetaElectrica v : patiDAO.obtenerTodos()) todosLosVehiculos.add(v);
        } catch (ServiceException ignored) {}

        poblarComboMarcas();
        actualizarEstadisticas();
        aplicarFiltros();
    }

    private void poblarComboMarcas() {
        String seleccionActual = comboMarca.getValue();
        comboMarca.getItems().clear();
        comboMarca.getItems().add("Todas");
        todosLosVehiculos.stream()
                .map(VehiculoElectrico::getMarca)
                .distinct()
                .sorted()
                .forEach(m -> comboMarca.getItems().add(m));
        if (seleccionActual != null && comboMarca.getItems().contains(seleccionActual)) {
            comboMarca.setValue(seleccionActual);
        } else {
            comboMarca.getSelectionModel().selectFirst();
        }
    }

    private void actualizarEstadisticas() {
        long total = todosLosVehiculos.size();
        long disponibles = todosLosVehiculos.stream().filter(v -> v.getEstado() == EstadoVehiculo.DISPONIBLE).count();
        long alquilados = todosLosVehiculos.stream().filter(v -> v.getEstado() == EstadoVehiculo.ALQUILADO).count();
        long vendidos = todosLosVehiculos.stream().filter(v -> v.getEstado() == EstadoVehiculo.VENDIDO).count();
        lblTotalVehiculos.setText(String.valueOf(total));
        lblDisponibles.setText(String.valueOf(disponibles));
        lblAlquilados.setText(String.valueOf(alquilados));
        lblVendidos.setText(String.valueOf(vendidos));
    }

    @FXML
    public void aplicarFiltros() {
        String busqueda = txtBuscar.getText() == null ? "" : txtBuscar.getText().toLowerCase().trim();
        String tipo = comboTipo.getValue();
        String estado = comboEstado.getValue();
        String marca = comboMarca.getValue();
        String orden = comboOrdenar.getValue();

        vehiculosFiltrados = todosLosVehiculos.stream()
                .filter(v -> {
                    if (tipo != null && !tipo.equals("Todos")) {
                        if (tipo.equals("Auto Eléctrico") && !(v instanceof AutoElectrico)) return false;
                        if (tipo.equals("Moto Eléctrica") && !(v instanceof MotoElectrica)) return false;
                        if (tipo.equals("Bicicleta Eléctrica") && !(v instanceof BicicletaElectrica)) return false;
                        if (tipo.equals("Patineta Eléctrica") && !(v instanceof PatinetaElectrica)) return false;
                    }
                    if (estado != null && !estado.equals("Todos")) {
                        if (!v.getEstado().name().equalsIgnoreCase(estado)) return false;
                    }
                    if (marca != null && !marca.equals("Todas")) {
                        if (!v.getMarca().equalsIgnoreCase(marca)) return false;
                    }
                    if (!busqueda.isEmpty()) {
                        String concat = (v.getMarca() + " " + v.getModelo() + " " + v.getId()).toLowerCase();
                        if (!concat.contains(busqueda)) return false;
                    }
                    return true;
                })
                .sorted((a, b) -> {
                    if (orden == null || orden.equals("Relevancia")) return 0;
                    if (orden.equals("Precio ascendente")) return Double.compare(a.getPrecioBase(), b.getPrecioBase());
                    if (orden.equals("Precio descendente")) return Double.compare(b.getPrecioBase(), a.getPrecioBase());
                    if (orden.equals("Marca A-Z")) return a.getMarca().compareToIgnoreCase(b.getMarca());
                    return 0;
                })
                .collect(java.util.stream.Collectors.toList());

        lblResultados.setText("Mostrando " + vehiculosFiltrados.size() + " vehículos");
        renderizarCards();
    }

    @FXML
    public void limpiarFiltros() {
        txtBuscar.clear();
        comboTipo.getSelectionModel().selectFirst();
        comboEstado.getSelectionModel().selectFirst();
        comboMarca.getSelectionModel().selectFirst();
        comboOrdenar.getSelectionModel().selectFirst();
        aplicarFiltros();
    }

    private void renderizarCards() {
        flowVehiculos.getChildren().clear();
        if (vehiculosFiltrados.isEmpty()) {
            flowVehiculos.getChildren().add(panelVacio);
            return;
        }
        for (VehiculoElectrico v : vehiculosFiltrados) {
            flowVehiculos.getChildren().add(crearCard(v));
        }
    }

    private VBox crearCard(VehiculoElectrico v) {
        VBox card = new VBox(8);
        card.setPrefWidth(240);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color:#1F1F1F; -fx-background-radius:12; -fx-border-color:#3B3B3B; -fx-border-radius:12;");

        String tipoStr = tipoVehiculo(v);
        Label lblTipo = new Label(tipoStr);
        lblTipo.setStyle("-fx-text-fill:#9B0F1F; -fx-font-size:10px; -fx-font-weight:bold;");

        Label lblNombre = new Label(v.getMarca() + " " + v.getModelo());
        lblNombre.setStyle("-fx-text-fill:white; -fx-font-size:14px; -fx-font-weight:bold;");
        lblNombre.setWrapText(true);

        Label lblId = new Label("ID: " + v.getId());
        lblId.setStyle("-fx-text-fill:#666; -fx-font-size:11px;");

        Label lblPrecio = new Label(String.format("$%,.0f", v.getPrecioBase()));
        lblPrecio.setStyle("-fx-text-fill:#9B0F1F; -fx-font-size:15px; -fx-font-weight:bold;");

        Label lblEstado = new Label(v.getEstado().name());
        String colorEstado = switch (v.getEstado()) {
            case DISPONIBLE -> "#90EE90";
            case ALQUILADO -> "#FFD700";
            case VENDIDO -> "#FF6B6B";
            case MANTENIMIENTO -> "#87CEEB";
        };
        lblEstado.setStyle("-fx-text-fill:" + colorEstado + "; -fx-font-size:11px; -fx-font-weight:bold; " +
                "-fx-background-color:" + colorEstado + "22; -fx-padding:2 8; -fx-background-radius:10;");

        Label lblAnio = new Label("Año: " + v.getAnio() + "  |  Autonomía: " + v.getAutonomiaKm() + " km");
        lblAnio.setStyle("-fx-text-fill:#888; -fx-font-size:11px;");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox botones = new HBox(8);
        botones.setAlignment(Pos.CENTER);
        botones.setPadding(new Insets(8, 0, 0, 0));

        Button btnEditar = new Button("✏ Editar");
        btnEditar.setStyle("-fx-background-color:#1F1F1F; -fx-text-fill:white; -fx-border-color:#9B0F1F; " +
                "-fx-border-radius:6; -fx-background-radius:6; -fx-font-size:11px; -fx-cursor:hand;");
        btnEditar.setPrefWidth(100);
        btnEditar.setOnAction(e -> abrirDialogoEditar(v));

        Button btnEliminar = new Button("🗑 Eliminar");
        btnEliminar.setStyle("-fx-background-color:#9B0F1F; -fx-text-fill:white; " +
                "-fx-border-radius:6; -fx-background-radius:6; -fx-font-size:11px; -fx-cursor:hand;");
        btnEliminar.setPrefWidth(100);
        btnEliminar.setOnAction(e -> confirmarEliminar(v));

        botones.getChildren().addAll(btnEditar, btnEliminar);
        card.getChildren().addAll(lblTipo, lblNombre, lblId, lblPrecio, lblEstado, lblAnio, spacer, botones);
        return card;
    }

    private String tipoVehiculo(VehiculoElectrico v) {
        if (v instanceof AutoElectrico) return "🚗 AUTO ELÉCTRICO";
        if (v instanceof MotoElectrica) return "🏍 MOTO ELÉCTRICA";
        if (v instanceof BicicletaElectrica) return "🚲 BICICLETA ELÉCTRICA";
        if (v instanceof PatinetaElectrica) return "🛴 PATINETA ELÉCTRICA";
        return "VEHÍCULO";
    }

    private void confirmarEliminar(VehiculoElectrico v) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Eliminar " + v.getMarca() + " " + v.getModelo() + "?");
        alert.setContentText("Esta acción no se puede deshacer. ¿Deseas continuar?");
        alert.getDialogPane().setStyle("-fx-background-color:#1F1F1F;");
        alert.getDialogPane().lookup(".content.label").setStyle("-fx-text-fill:white;");
        alert.getDialogPane().lookup(".header-panel").setStyle("-fx-background-color:#9B0F1F;");

        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                long id = Long.parseLong(v.getId());
                if (v instanceof AutoElectrico) autoDAO.eliminar(id);
                else if (v instanceof MotoElectrica) motoDAO.eliminar(id);
                else if (v instanceof BicicletaElectrica) biciDAO.eliminar(id);
                else if (v instanceof PatinetaElectrica) patiDAO.eliminar(id);
                utilidades.mostrarAlerta(Alert.AlertType.INFORMATION, "Eliminado",
                        "Vehículo eliminado correctamente.");
                cargarVehiculos();
            } catch (ServiceException ex) {
                utilidades.mostrarAlerta(Alert.AlertType.ERROR, "Error al eliminar", ex.getMessage());
            }
        }
    }

    private void abrirDialogoEditar(VehiculoElectrico v) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar vehículo");
        dialog.setHeaderText(v.getMarca() + " " + v.getModelo());
        dialog.getDialogPane().setStyle("-fx-background-color:#1F1F1F;");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color:#1F1F1F;");

        String fieldStyle = "-fx-background-color:#2a2a2a; -fx-text-fill:white; -fx-border-color:#444; " +
                "-fx-border-radius:4; -fx-background-radius:4; -fx-padding:6 8;";
        String labelStyle = "-fx-text-fill:#aaa; -fx-font-size:12px;";

        TextField fMarca = new TextField(v.getMarca());
        TextField fModelo = new TextField(v.getModelo());
        TextField fColor = new TextField(v.getColor());
        TextField fPrecio = new TextField(String.valueOf(v.getPrecioBase()));
        TextField fAutonomia = new TextField(String.valueOf(v.getAutonomiaKm()));
        TextField fAnio = new TextField(String.valueOf(v.getAnio()));
        ComboBox<String> cbEstado = new ComboBox<>();
        cbEstado.getItems().addAll("DISPONIBLE", "ALQUILADO", "VENDIDO", "MANTENIMIENTO");
        cbEstado.setValue(v.getEstado().name());

        for (TextField f : new TextField[]{fMarca, fModelo, fColor, fPrecio, fAutonomia, fAnio}) {
            f.setStyle(fieldStyle);
            f.setPrefWidth(200);
        }
        cbEstado.setStyle("-fx-background-color:#2a2a2a; -fx-border-color:#444; " +
                "-fx-border-radius:4; -fx-background-radius:4;");
        cbEstado.setPrefWidth(200);

        int row = 0;
        grid.add(styledLabel("Marca", labelStyle), 0, row); grid.add(fMarca, 1, row++);
        grid.add(styledLabel("Modelo", labelStyle), 0, row); grid.add(fModelo, 1, row++);
        grid.add(styledLabel("Color", labelStyle), 0, row); grid.add(fColor, 1, row++);
        grid.add(styledLabel("Precio base", labelStyle), 0, row); grid.add(fPrecio, 1, row++);
        grid.add(styledLabel("Autonomía km", labelStyle), 0, row); grid.add(fAutonomia, 1, row++);
        grid.add(styledLabel("Año", labelStyle), 0, row); grid.add(fAnio, 1, row++);
        grid.add(styledLabel("Estado", labelStyle), 0, row); grid.add(cbEstado, 1, row);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.setStyle("-fx-background-color:#9B0F1F; -fx-text-fill:white; -fx-border-radius:4; -fx-background-radius:4;");
        Button cancelBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelBtn.setStyle("-fx-background-color:#333; -fx-text-fill:white; -fx-border-radius:4; -fx-background-radius:4;");

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                v.setMarca(fMarca.getText().trim());
                v.setModelo(fModelo.getText().trim());
                v.setColor(fColor.getText().trim());
                v.setPrecioBase(Double.parseDouble(fPrecio.getText().trim()));
                v.setAutonomiaKm(Double.parseDouble(fAutonomia.getText().trim()));
                v.setAnio(Integer.parseInt(fAnio.getText().trim()));
                v.setEstado(EstadoVehiculo.valueOf(cbEstado.getValue()));
                guardarEdicion(v);
                utilidades.mostrarAlerta(Alert.AlertType.INFORMATION, "Guardado", "Vehículo actualizado correctamente.");
                cargarVehiculos();
            } catch (NumberFormatException ex) {
                utilidades.mostrarAlerta(Alert.AlertType.ERROR, "Datos inválidos", "Verifica que precio, autonomía y año sean números válidos.");
            } catch (ServiceException ex) {
                utilidades.mostrarAlerta(Alert.AlertType.ERROR, "Error al guardar", ex.getMessage());
            }
        }
    }

    private void guardarEdicion(VehiculoElectrico v) throws ServiceException {
        long id = Long.parseLong(v.getId());
        if (v instanceof AutoElectrico) {
            autoDAO.eliminar(id);
            autoDAO.guardar((AutoElectrico) v);
        } else if (v instanceof MotoElectrica) {
            motoDAO.eliminar(id);
            motoDAO.guardar((MotoElectrica) v);
        } else if (v instanceof BicicletaElectrica) {
            biciDAO.eliminar(id);
            biciDAO.guardar((BicicletaElectrica) v);
        } else if (v instanceof PatinetaElectrica) {
            patiDAO.eliminar(id);
            patiDAO.guardar((PatinetaElectrica) v);
        }
    }

    private Label styledLabel(String text, String style) {
        Label l = new Label(text);
        l.setStyle(style);
        return l;
    }

    @FXML
    public void cambiarInicio(ActionEvent event) {
        try {
            utilidades.cambiarEscenaConTransicion(event, "/FXML/MenuPrincipalEmpleado.fxml");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    public void cambiarAgregarVehiculo(ActionEvent event) {
        try {
            utilidades.cambiarEscenaConTransicion(event, "/FXML/MenuAgregarVehiculo.fxml");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    public void cambiarFlota(ActionEvent event) {
        try {
            utilidades.cambiarEscenaConTransicion(event, "/FXML/MenuFlotaAdmin.fxml");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    public void cambiarAsistente(ActionEvent event) {
        try {
            utilidades.cambiarEscenaConTransicion(event, "/FXML/AsistenteAI.fxml");
        } catch (Exception e) { e.printStackTrace(); }
    }
}