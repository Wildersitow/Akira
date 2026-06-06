package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.*;
import service.ServiceException;
import service.ServiceFlota;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ControladorMenuFlotaAdmin {

    @FXML private Label lblTotalVehiculos;
    @FXML private Label lblDisponibles;
    @FXML private Label lblAlquilados;
    @FXML private Label lblVendidos;

    @FXML private TextField        txtBuscar;
    @FXML private ComboBox<String> comboTipo;
    @FXML private ComboBox<String> comboEstado;
    @FXML private ComboBox<String> comboMarca;
    @FXML private ComboBox<String> comboOrdenar;

    @FXML private FlowPane flowVehiculos;
    @FXML private VBox     panelVacio;
    @FXML private Label    lblResultados;

    private final UtilidadesFX utilidades   = new UtilidadesFX();
    private final ServiceFlota serviceFlota = new ServiceFlota();

    private List<VehiculoElectrico> todosLosVehiculos  = new ArrayList<>();
    private List<VehiculoElectrico> vehiculosFiltrados = new ArrayList<>();

    @FXML
    public void initialize() {
        comboTipo.getItems().addAll(
                "Todos", "Auto Eléctrico", "Moto Eléctrica",
                "Bicicleta Eléctrica", "Patineta Eléctrica");
        comboEstado.getItems().addAll(
                "Todos", "DISPONIBLE", "ALQUILADO", "VENDIDO", "MANTENIMIENTO");
        comboOrdenar.getItems().addAll(
                "Relevancia", "Precio ascendente", "Precio descendente", "Marca A-Z");
        comboTipo.getSelectionModel().selectFirst();
        comboEstado.getSelectionModel().selectFirst();
        comboOrdenar.getSelectionModel().selectFirst();
        cargarVehiculos();
    }

    @FXML
    public void cargarVehiculos() {
        try {
            todosLosVehiculos = serviceFlota.obtenerTodos();
        } catch (ServiceException e) {
            todosLosVehiculos = new ArrayList<>();
            utilidades.mostrarAlerta(Alert.AlertType.ERROR, "Error al cargar", e.getMessage());
        }
        poblarComboMarcas();
        actualizarEstadisticas();
        aplicarFiltros();
    }

    private void poblarComboMarcas() {
        String selActual = comboMarca.getValue();
        comboMarca.getItems().clear();
        comboMarca.getItems().add("Todas");
        todosLosVehiculos.stream()
                .map(VehiculoElectrico::getMarca)
                .distinct()
                .sorted()
                .forEach(m -> comboMarca.getItems().add(m));
        if (selActual != null && comboMarca.getItems().contains(selActual))
            comboMarca.setValue(selActual);
        else
            comboMarca.getSelectionModel().selectFirst();
    }

    private void actualizarEstadisticas() {
        lblTotalVehiculos.setText(String.valueOf(todosLosVehiculos.size()));
        lblDisponibles.setText(String.valueOf(
                todosLosVehiculos.stream().filter(v -> v.getEstado() == EstadoVehiculo.DISPONIBLE).count()));
        lblAlquilados.setText(String.valueOf(
                todosLosVehiculos.stream().filter(v -> v.getEstado() == EstadoVehiculo.ALQUILADO).count()));
        lblVendidos.setText(String.valueOf(
                todosLosVehiculos.stream().filter(v -> v.getEstado() == EstadoVehiculo.VENDIDO).count()));
    }

    @FXML
    public void aplicarFiltros() {
        String busqueda = txtBuscar.getText() == null ? "" : txtBuscar.getText().toLowerCase().trim();
        String tipo    = comboTipo.getValue();
        String estado  = comboEstado.getValue();
        String marca   = comboMarca.getValue();
        String orden   = comboOrdenar.getValue();

        vehiculosFiltrados = todosLosVehiculos.stream()
                .filter(v -> {
                    if (tipo != null && !tipo.equals("Todos")) {
                        if (tipo.equals("Auto Eléctrico")      && !(v instanceof AutoElectrico))      return false;
                        if (tipo.equals("Moto Eléctrica")      && !(v instanceof MotoElectrica))      return false;
                        if (tipo.equals("Bicicleta Eléctrica") && !(v instanceof BicicletaElectrica)) return false;
                        if (tipo.equals("Patineta Eléctrica")  && !(v instanceof PatinetaElectrica))  return false;
                    }
                    if (estado != null && !estado.equals("Todos"))
                        if (!v.getEstado().name().equalsIgnoreCase(estado)) return false;
                    if (marca != null && !marca.equals("Todas"))
                        if (!v.getMarca().equalsIgnoreCase(marca)) return false;
                    if (!busqueda.isEmpty()) {
                        String concat = (v.getMarca() + " " + v.getModelo() + " " + v.getId()).toLowerCase();
                        if (!concat.contains(busqueda)) return false;
                    }
                    return true;
                })
                .sorted((a, b) -> {
                    if (orden == null || orden.equals("Relevancia"))   return 0;
                    if (orden.equals("Precio ascendente"))  return Double.compare(a.getPrecioBase(), b.getPrecioBase());
                    if (orden.equals("Precio descendente")) return Double.compare(b.getPrecioBase(), a.getPrecioBase());
                    if (orden.equals("Marca A-Z"))          return a.getMarca().compareToIgnoreCase(b.getMarca());
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
        for (VehiculoElectrico v : vehiculosFiltrados)
            flowVehiculos.getChildren().add(crearCard(v));
    }

    private VBox crearCard(VehiculoElectrico v) {
        VBox card = new VBox(8);
        card.setPrefWidth(240);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color:#1F1F1F; -fx-background-radius:12; " +
                "-fx-border-color:#3B3B3B; -fx-border-radius:12;");

        Label lblTipo = new Label(tipoVehiculo(v));
        lblTipo.setStyle("-fx-text-fill:#9B0F1F; -fx-font-size:10px; -fx-font-weight:bold;");

        Label lblNombre = new Label(v.getMarca() + " " + v.getModelo());
        lblNombre.setStyle("-fx-text-fill:white; -fx-font-size:14px; -fx-font-weight:bold;");
        lblNombre.setWrapText(true);

        Label lblId = new Label("ID: " + v.getId());
        lblId.setStyle("-fx-text-fill:#666; -fx-font-size:11px;");

        Label lblPrecio = new Label(String.format("$%,.0f", v.getPrecioBase()));
        lblPrecio.setStyle("-fx-text-fill:#9B0F1F; -fx-font-size:15px; -fx-font-weight:bold;");

        String colorEstado = switch (v.getEstado()) {
            case DISPONIBLE    -> "#90EE90";
            case ALQUILADO     -> "#FFD700";
            case VENDIDO       -> "#FF6B6B";
            case MANTENIMIENTO -> "#87CEEB";
        };
        Label lblEstado = new Label(v.getEstado().name());
        lblEstado.setStyle("-fx-text-fill:" + colorEstado + "; -fx-font-size:11px; -fx-font-weight:bold; " +
                "-fx-background-color:" + colorEstado + "22; -fx-padding:2 8; -fx-background-radius:10;");

        Label lblInfo = new Label("Año: " + v.getAnio() + "  |  Autonomía: " + v.getAutonomiaKm() + " km");
        lblInfo.setStyle("-fx-text-fill:#888; -fx-font-size:11px;");

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
        card.getChildren().addAll(lblTipo, lblNombre, lblId, lblPrecio, lblEstado, lblInfo, spacer, botones);
        return card;
    }

    private String tipoVehiculo(VehiculoElectrico v) {
        if (v instanceof AutoElectrico)      return "🚗 AUTO ELÉCTRICO";
        if (v instanceof MotoElectrica)      return "🏍 MOTO ELÉCTRICA";
        if (v instanceof BicicletaElectrica) return "🚲 BICICLETA ELÉCTRICA";
        if (v instanceof PatinetaElectrica)  return "🛴 PATINETA ELÉCTRICA";
        return "VEHÍCULO";
    }

    private void confirmarEliminar(VehiculoElectrico v) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Eliminar " + v.getMarca() + " " + v.getModelo() + "?");
        alert.setContentText("Esta acción no se puede deshacer. ¿Deseas continuar?");
        estilizarDialogo(alert.getDialogPane());

        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                serviceFlota.eliminar(v);
                utilidades.mostrarAlerta(Alert.AlertType.INFORMATION, "Eliminado", "Vehículo eliminado correctamente.");
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
        estilizarDialogo(dialog.getDialogPane());

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color:#1F1F1F;");

        final String fieldStyle =
                "-fx-background-color:#2a2a2a; -fx-text-fill:white; -fx-border-color:#444; " +
                        "-fx-border-radius:4; -fx-background-radius:4; -fx-padding:6 8;";
        final String labelStyle = "-fx-text-fill:#aaa; -fx-font-size:12px;";

        TextField fMarca     = styledField(v.getMarca(),                      fieldStyle);
        TextField fModelo    = styledField(v.getModelo(),                     fieldStyle);
        TextField fColor     = styledField(v.getColor(),                      fieldStyle);
        TextField fPrecio    = styledField(String.valueOf(v.getPrecioBase()),  fieldStyle);
        TextField fAutonomia = styledField(String.valueOf(v.getAutonomiaKm()), fieldStyle);
        TextField fAnio      = styledField(String.valueOf(v.getAnio()),        fieldStyle);

        ComboBox<String> cbEstado = new ComboBox<>();
        cbEstado.getItems().addAll("DISPONIBLE", "ALQUILADO", "VENDIDO", "MANTENIMIENTO");
        cbEstado.setValue(v.getEstado().name());
        cbEstado.setStyle("-fx-background-color:#2a2a2a; -fx-border-color:#444; " +
                "-fx-border-radius:4; -fx-background-radius:4;");
        cbEstado.setPrefWidth(200);

        int row = 0;
        grid.add(lbl("Marca",        labelStyle), 0, row); grid.add(fMarca,     1, row++);
        grid.add(lbl("Modelo",       labelStyle), 0, row); grid.add(fModelo,    1, row++);
        grid.add(lbl("Color",        labelStyle), 0, row); grid.add(fColor,     1, row++);
        grid.add(lbl("Precio base",  labelStyle), 0, row); grid.add(fPrecio,    1, row++);
        grid.add(lbl("Autonomía km", labelStyle), 0, row); grid.add(fAutonomia, 1, row++);
        grid.add(lbl("Año",          labelStyle), 0, row); grid.add(fAnio,      1, row++);
        grid.add(lbl("Estado",       labelStyle), 0, row); grid.add(cbEstado,   1, row++);

        TextField fExtra1 = null, fExtra2 = null;
        if (v instanceof AutoElectrico auto) {
            fExtra1 = styledField(auto.getTipoCarga(), fieldStyle);
            fExtra2 = styledField(auto.getTraccion(),  fieldStyle);
            grid.add(lbl("Tipo carga", labelStyle), 0, row); grid.add(fExtra1, 1, row++);
            grid.add(lbl("Tracción",   labelStyle), 0, row); grid.add(fExtra2, 1, row++);
        } else if (v instanceof MotoElectrica moto) {
            fExtra1 = styledField(moto.getTipoMoto(),               fieldStyle);
            fExtra2 = styledField(String.valueOf(moto.getPesoKg()), fieldStyle);
            grid.add(lbl("Tipo moto", labelStyle), 0, row); grid.add(fExtra1, 1, row++);
            grid.add(lbl("Peso kg",   labelStyle), 0, row); grid.add(fExtra2, 1, row++);
        } else if (v instanceof BicicletaElectrica bici) {
            fExtra1 = styledField(bici.getTipoAsistencia(),               fieldStyle);
            fExtra2 = styledField(String.valueOf(bici.getNumeroMarchas()), fieldStyle);
            grid.add(lbl("Asistencia", labelStyle), 0, row); grid.add(fExtra1, 1, row++);
            grid.add(lbl("Nº marchas", labelStyle), 0, row); grid.add(fExtra2, 1, row++);
        } else if (v instanceof PatinetaElectrica pati) {
            fExtra1 = styledField(String.valueOf(pati.getVelocidadMaximaKmH()), fieldStyle);
            fExtra2 = styledField(String.valueOf(pati.getCargaMaximaKg()),       fieldStyle);
            grid.add(lbl("Vel. máx km/h", labelStyle), 0, row); grid.add(fExtra1, 1, row++);
            grid.add(lbl("Carga máx kg",  labelStyle), 0, row); grid.add(fExtra2, 1, row++);
        }

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okBtn     = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        Button cancelBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        okBtn.setStyle(    "-fx-background-color:#9B0F1F; -fx-text-fill:white; -fx-border-radius:4; -fx-background-radius:4;");
        cancelBtn.setStyle("-fx-background-color:#333;    -fx-text-fill:white; -fx-border-radius:4; -fx-background-radius:4;");

        final TextField fe1 = fExtra1;
        final TextField fe2 = fExtra2;

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Map<String, Object> campos = new LinkedHashMap<>();
                campos.put("marca",        fMarca.getText().trim());
                campos.put("modelo",       fModelo.getText().trim());
                campos.put("color",        fColor.getText().trim());
                campos.put("precio_base",  Double.parseDouble(fPrecio.getText().trim()));
                campos.put("autonomia_km", Double.parseDouble(fAutonomia.getText().trim()));
                campos.put("anio",         Integer.parseInt(fAnio.getText().trim()));
                campos.put("estado_id",    serviceFlota.estadoToId(EstadoVehiculo.valueOf(cbEstado.getValue())));

                if (v instanceof AutoElectrico) {
                    campos.put("tipo_carga", fe1.getText().trim());
                    campos.put("traccion",   fe2.getText().trim());
                } else if (v instanceof MotoElectrica) {
                    campos.put("tipo_moto", fe1.getText().trim());
                    campos.put("peso_kg",   Double.parseDouble(fe2.getText().trim()));
                } else if (v instanceof BicicletaElectrica) {
                    campos.put("tipo_asistencia", fe1.getText().trim());
                    campos.put("num_cambios",      Integer.parseInt(fe2.getText().trim()));
                } else if (v instanceof PatinetaElectrica) {
                    campos.put("velocidad_max_kmh", Integer.parseInt(fe1.getText().trim()));
                    campos.put("carga_maxima_kg",   Integer.parseInt(fe2.getText().trim()));
                }

                serviceFlota.actualizarCampos(v, campos);
                utilidades.mostrarAlerta(Alert.AlertType.INFORMATION, "Guardado", "Vehículo actualizado correctamente.");
                cargarVehiculos();

            } catch (NumberFormatException ex) {
                utilidades.mostrarAlerta(Alert.AlertType.ERROR, "Datos inválidos",
                        "Verifica que precio, autonomía, año y valores numéricos sean correctos.");
            } catch (ServiceException ex) {
                utilidades.mostrarAlerta(Alert.AlertType.ERROR, "Error al guardar", ex.getMessage());
            }
        }
    }

    @FXML
    public void cambiarInicio(ActionEvent event) {
        try { utilidades.cambiarEscenaConTransicion(event, "/FXML/MenuPrincipalEmpleado.fxml"); }
        catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    public void cambiarAgregarVehiculo(ActionEvent event) {
        try { utilidades.cambiarEscenaConTransicion(event, "/FXML/MenuAgregarVehiculo.fxml"); }
        catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    public void cambiarFlota(ActionEvent event) {
        try { utilidades.cambiarEscenaConTransicion(event, "/FXML/MenuFlotaAdmin.fxml"); }
        catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    public void cambiarAsistente(ActionEvent event) {
        try { utilidades.cambiarEscenaConTransicion(event, "/FXML/AsistenteAI.fxml"); }
        catch (Exception e) { e.printStackTrace(); }
    }

    private void estilizarDialogo(DialogPane dp) {
        dp.setStyle("-fx-background-color:#1F1F1F;");
        var header = dp.lookup(".header-panel");
        if (header != null) header.setStyle("-fx-background-color:#9B0F1F;");
        var content = dp.lookup(".content.label");
        if (content != null) content.setStyle("-fx-text-fill:white;");
    }

    private TextField styledField(String valor, String style) {
        TextField tf = new TextField(valor);
        tf.setStyle(style);
        tf.setPrefWidth(200);
        return tf;
    }

    private Label lbl(String texto, String style) {
        Label l = new Label(texto);
        l.setStyle(style);
        return l;
    }
}