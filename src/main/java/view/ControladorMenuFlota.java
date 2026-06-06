package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.*;
import service.ServiceException;
import service.ServiceFlota;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ControladorMenuFlota {

    @FXML private FlowPane         flowVehiculos;
    @FXML private VBox             panelVacio;
    @FXML private TextField        txtBuscar;
    @FXML private ComboBox<String> comboTipo;
    @FXML private ComboBox<String> comboEstado;
    @FXML private ComboBox<String> comboMarca;
    @FXML private ComboBox<String> comboOrdenar;
    @FXML private Label            lblResultados;

    private final UtilidadesFX utilidades = new UtilidadesFX();

    private List<VehiculoElectrico> todosLosVehiculos = new ArrayList<>();

    private final ServiceFlota serviceFlota = new ServiceFlota();

    @FXML
    public void initialize() {
        poblarCombosFijos();
        cargarVehiculos();
    }

    private void poblarCombosFijos() {
        comboTipo.getItems().addAll(
                "Todos", "Auto Eléctrico", "Moto Eléctrica",
                "Bicicleta Eléctrica", "Patineta Eléctrica");
        comboEstado.getItems().addAll(
                "Todos", "Disponible", "Mantenimiento");
        comboOrdenar.getItems().addAll(
                "Relevancia", "Precio: menor a mayor",
                "Precio: mayor a menor", "Más reciente");
    }

    @FXML
    public void cargarVehiculos() {
        todosLosVehiculos.clear();

        try {
            todosLosVehiculos.addAll(serviceFlota.obtenerTodos()); // ✅ una sola vez
        } catch (ServiceException e) {
            System.err.println("Error cargando vehículos: " + e.getMessage());
        }

        todosLosVehiculos = todosLosVehiculos.stream()
                .filter(v -> v.getEstado() == EstadoVehiculo.DISPONIBLE
                        || v.getEstado() == EstadoVehiculo.MANTENIMIENTO)
                .collect(Collectors.toList());

        List<String> marcas = todosLosVehiculos.stream()
                .map(VehiculoElectrico::getMarca)
                .distinct().sorted().collect(Collectors.toList());
        comboMarca.getItems().clear();
        comboMarca.getItems().add("Todas");
        comboMarca.getItems().addAll(marcas);

        aplicarFiltros();
    }

    @FXML
    public void aplicarFiltros() {
        List<VehiculoElectrico> filtrados = new ArrayList<>(todosLosVehiculos);

        String buscar = txtBuscar != null && txtBuscar.getText() != null
                ? txtBuscar.getText().trim().toLowerCase() : "";
        if (!buscar.isEmpty()) {
            filtrados = filtrados.stream()
                    .filter(v -> v.getMarca().toLowerCase().contains(buscar)
                            || v.getModelo().toLowerCase().contains(buscar))
                    .collect(Collectors.toList());
        }

        String tipo = comboTipo.getValue();
        if (tipo != null && !tipo.equals("Todos")) {
            filtrados = filtrados.stream()
                    .filter(v -> obtenerTipo(v).equals(tipo))
                    .collect(Collectors.toList());
        }

        String estado = comboEstado.getValue();
        if (estado != null && !estado.equals("Todos")) {
            filtrados = filtrados.stream()
                    .filter(v -> obtenerEstadoTexto(v).equals(estado))
                    .collect(Collectors.toList());
        }

        String marca = comboMarca.getValue();
        if (marca != null && !marca.equals("Todas")) {
            filtrados = filtrados.stream()
                    .filter(v -> v.getMarca().equalsIgnoreCase(marca))
                    .collect(Collectors.toList());
        }

        String orden = comboOrdenar.getValue();
        if ("Precio: menor a mayor".equals(orden))
            filtrados.sort((a, b) -> Double.compare(a.getPrecioBase(), b.getPrecioBase()));
        else if ("Precio: mayor a menor".equals(orden))
            filtrados.sort((a, b) -> Double.compare(b.getPrecioBase(), a.getPrecioBase()));
        else if ("Más reciente".equals(orden))
            filtrados.sort((a, b) -> Integer.compare(b.getAnio(), a.getAnio()));

        renderizarCards(filtrados);
    }

    @FXML
    public void limpiarFiltros() {
        txtBuscar.clear();
        comboTipo.setValue(null);
        comboEstado.setValue(null);
        comboMarca.setValue(null);
        comboOrdenar.setValue(null);
        aplicarFiltros();
    }

    private void renderizarCards(List<VehiculoElectrico> lista) {
        flowVehiculos.getChildren().clear();
        if (lista.isEmpty()) {
            flowVehiculos.getChildren().add(panelVacio);
            lblResultados.setText("Sin resultados");
            return;
        }
        lblResultados.setText("Mostrando " + lista.size() + " vehículo(s)");
        for (VehiculoElectrico v : lista)
            flowVehiculos.getChildren().add(crearCard(v));
    }

    private VBox crearCard(VehiculoElectrico v) {
        VBox card = new VBox(0);
        card.setPrefWidth(255);
        card.setStyle(estiloCardNormal());
        card.setOnMouseEntered(e -> card.setStyle(estiloCardHover()));
        card.setOnMouseExited(e  -> card.setStyle(estiloCardNormal()));

        StackPane zonaImagen = new StackPane();
        zonaImagen.setPrefHeight(150);
        zonaImagen.setStyle("-fx-background-color:#141414; -fx-background-radius:14 14 0 0;");

        Label badgeTipo = new Label(obtenerEmoji(v) + "  " + obtenerTipo(v));
        badgeTipo.setStyle(
                "-fx-background-color:#9B0F1F; -fx-text-fill:white;" +
                        "-fx-font-size:10px; -fx-font-weight:bold;" +
                        "-fx-background-radius:6; -fx-padding:3 8;");
        StackPane.setAlignment(badgeTipo, Pos.TOP_LEFT);
        StackPane.setMargin(badgeTipo, new Insets(10, 0, 0, 10));

        Label badgeEstado = new Label(obtenerEstadoTexto(v));
        badgeEstado.setStyle(
                "-fx-background-color:" + colorEstado(v) + ";" +
                        "-fx-text-fill:white; -fx-font-size:10px; -fx-font-weight:bold;" +
                        "-fx-background-radius:6; -fx-padding:3 8;");
        StackPane.setAlignment(badgeEstado, Pos.TOP_RIGHT);
        StackPane.setMargin(badgeEstado, new Insets(10, 10, 0, 0));

        ImageView imgView = new ImageView();
        imgView.setFitWidth(190);
        imgView.setFitHeight(110);
        imgView.setPreserveRatio(true);
        imgView.setSmooth(true);
        cargarImagen(imgView, v);

        zonaImagen.getChildren().addAll(imgView, badgeTipo, badgeEstado);

        VBox zonaInfo = new VBox(6);
        zonaInfo.setPadding(new Insets(14, 16, 14, 16));

        Label lblNombre = new Label(v.getMarca() + " " + v.getModelo());
        lblNombre.setStyle(
                "-fx-text-fill:#eeeeee; -fx-font-size:15px;" +
                        "-fx-font-weight:bold; -fx-font-family:'Rajdhani';");
        lblNombre.setWrapText(true);

        Label lblAnio = new Label(v.getAnio() + "  •  " + v.getColor());
        lblAnio.setStyle("-fx-text-fill:#666; -fx-font-size:11px;");

        Label lblPrecio = new Label("$" + String.format("%,.0f", v.getPrecioBase()).replace(",", "."));
        lblPrecio.setStyle(
                "-fx-text-fill:#9B0F1F; -fx-font-size:17px;" +
                        "-fx-font-weight:bold; -fx-font-family:'Rajdhani';");

        HBox infoRapida = new HBox(12);
        infoRapida.setAlignment(Pos.CENTER_LEFT);
        if (v.getAutonomiaKm() != null && v.getAutonomiaKm() > 0) {
            Label lblAuto = new Label("⚡ " + v.getAutonomiaKm().intValue() + " km");
            lblAuto.setStyle("-fx-text-fill:#555; -fx-font-size:11px;");
            infoRapida.getChildren().add(lblAuto);
        }

        Region sep = new Region();
        sep.setPrefHeight(1);
        sep.setStyle("-fx-background-color:#2e2e2e;");

        Button btnInfo = new Button("Ver información");
        btnInfo.setPrefWidth(223);
        btnInfo.setPrefHeight(34);
        btnInfo.setStyle(
                "-fx-background-color:transparent; -fx-text-fill:#9B0F1F;" +
                        "-fx-border-color:#9B0F1F; -fx-border-radius:7; -fx-background-radius:7;" +
                        "-fx-border-width:1; -fx-font-size:12px; -fx-font-weight:bold; -fx-cursor:hand;");
        btnInfo.setOnMouseEntered(e -> btnInfo.setStyle(
                "-fx-background-color:#9B0F1F; -fx-text-fill:white;" +
                        "-fx-border-color:#9B0F1F; -fx-border-radius:7; -fx-background-radius:7;" +
                        "-fx-border-width:1; -fx-font-size:12px; -fx-font-weight:bold; -fx-cursor:hand;"));
        btnInfo.setOnMouseExited(e -> btnInfo.setStyle(
                "-fx-background-color:transparent; -fx-text-fill:#9B0F1F;" +
                        "-fx-border-color:#9B0F1F; -fx-border-radius:7; -fx-background-radius:7;" +
                        "-fx-border-width:1; -fx-font-size:12px; -fx-font-weight:bold; -fx-cursor:hand;"));
        btnInfo.setOnAction(e -> verInformacion(v));

        zonaInfo.getChildren().addAll(lblNombre, lblAnio, lblPrecio, infoRapida, sep, btnInfo);
        card.getChildren().addAll(zonaImagen, zonaInfo);
        return card;
    }

    private void verInformacion(VehiculoElectrico v) {
        Stage ventana = new Stage();
        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.setTitle(v.getMarca() + " " + v.getModelo());
        ventana.setResizable(false);

        VBox root = new VBox(0);
        root.setStyle("-fx-background-color:#0f0f0f;");
        root.setPrefWidth(460);

        HBox header = new HBox(10);
        header.setStyle("-fx-background-color:#9B0F1F; -fx-padding:16 20;");
        header.setAlignment(Pos.CENTER_LEFT);
        Label emoji = new Label(obtenerEmoji(v));
        emoji.setStyle("-fx-font-size:24px;");
        VBox infoH = new VBox(2);
        Label hTitulo = new Label(v.getMarca() + " " + v.getModelo());
        hTitulo.setStyle("-fx-text-fill:white; -fx-font-size:18px; -fx-font-weight:bold; -fx-font-family:'Rajdhani';");
        Label hSub = new Label(obtenerTipo(v) + "  •  " + v.getAnio());
        hSub.setStyle("-fx-text-fill:#ffcccc; -fx-font-size:12px;");
        infoH.getChildren().addAll(hTitulo, hSub);
        header.getChildren().addAll(emoji, infoH);
        root.getChildren().add(header);

        VBox body = new VBox(0);
        agregarFila(body, "Estado",      obtenerEstadoTexto(v),          false);
        agregarFila(body, "Color",       v.getColor(),                   true);
        agregarFila(body, "Año",         String.valueOf(v.getAnio()),     false);
        agregarFila(body, "Precio base", formatearPrecio(v.getPrecioBase()), true);
        if (v.getAutonomiaKm() != null && v.getAutonomiaKm() > 0)
            agregarFila(body, "Autonomía", v.getAutonomiaKm().intValue() + " km", false);
        if (v.getCapacidadBateria() != null && v.getCapacidadBateria() > 0)
            agregarFila(body, "Batería", v.getCapacidadBateria() + " kWh", true);

        if (v instanceof AutoElectrico a) {
            agregarFila(body, "Tipo",      a.getTipoCarga(),                      false);
            agregarFila(body, "Puertas",   String.valueOf(a.getNumeroPuertas()),   true);
            agregarFila(body, "Pasajeros", String.valueOf(a.getNumeroPasajeros()), false);
            agregarFila(body, "Tracción",  a.getTraccion(),                       true);
        } else if (v instanceof MotoElectrica m) {
            agregarFila(body, "Tipo moto", m.getTipoMoto(),        true);
            agregarFila(body, "Peso",      m.getPesoKg() + " kg",  false);
        } else if (v instanceof BicicletaElectrica b) {
            agregarFila(body, "Asistencia", b.getTipoAsistencia(),               false);
            agregarFila(body, "Marchas",    String.valueOf(b.getNumeroMarchas()), true);
        } else if (v instanceof PatinetaElectrica p) {
            agregarFila(body, "Vel. máx.",  p.getVelocidadMaximaKmH() + " km/h", false);
            agregarFila(body, "Plegable",   p.isEsPlegable() ? "Sí" : "No",       true);
            agregarFila(body, "Carga máx.", p.getCargaMaximaKg() + " kg",          false);
        }
        root.getChildren().add(body);

        HBox pie = new HBox();
        pie.setStyle("-fx-background-color:#111; -fx-padding:14 20; -fx-border-color:#1e1e1e; -fx-border-width:1 0 0 0;");
        pie.setAlignment(Pos.CENTER_RIGHT);
        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setStyle(
                "-fx-background-color:#9B0F1F; -fx-text-fill:white;" +
                        "-fx-border-radius:7; -fx-background-radius:7;" +
                        "-fx-font-size:13px; -fx-cursor:hand; -fx-padding:8 20;");
        btnCerrar.setOnAction(e -> ventana.close());
        pie.getChildren().add(btnCerrar);
        root.getChildren().add(pie);

        ventana.setScene(new Scene(root));
        ventana.show();
    }

    private void agregarFila(VBox parent, String etiqueta, String valor, boolean par) {
        HBox fila = new HBox();
        fila.setPadding(new Insets(10, 20, 10, 20));
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setStyle("-fx-background-color:" + (par ? "#141414" : "#111111") + ";");
        Label lbl = new Label(etiqueta);
        lbl.setStyle("-fx-text-fill:#666; -fx-font-size:12px;");
        lbl.setPrefWidth(130);
        Region esp = new Region();
        HBox.setHgrow(esp, Priority.ALWAYS);
        Label val = new Label(valor != null ? valor : "—");
        val.setStyle("-fx-text-fill:#cccccc; -fx-font-size:12px; -fx-font-weight:bold;");
        fila.getChildren().addAll(lbl, esp, val);
        parent.getChildren().add(fila);
    }

    private void cargarImagen(ImageView imgView, VehiculoElectrico v) {
        String[] rutas = {
                "src/main/resources/Imagenes/" + v.getMarca() + "_" + v.getModelo() + ".png",
                "src/main/resources/Imagenes/" + v.getMarca() + "_" + v.getModelo() + ".jpg",
                "src/main/resources/Imagenes/" + obtenerTipoArchivo(v) + ".png"
        };
        for (String ruta : rutas) {
            File f = new File(ruta);
            if (f.exists()) { imgView.setImage(new Image(f.toURI().toString())); return; }
        }
    }

    private String obtenerTipo(VehiculoElectrico v) {
        if (v instanceof AutoElectrico)      return "Auto Eléctrico";
        if (v instanceof MotoElectrica)      return "Moto Eléctrica";
        if (v instanceof BicicletaElectrica) return "Bicicleta Eléctrica";
        if (v instanceof PatinetaElectrica)  return "Patineta Eléctrica";
        return "Vehículo";
    }

    private String obtenerEmoji(VehiculoElectrico v) {
        if (v instanceof AutoElectrico)      return "🚗";
        if (v instanceof MotoElectrica)      return "🏍";
        if (v instanceof BicicletaElectrica) return "🚲";
        if (v instanceof PatinetaElectrica)  return "🛴";
        return "⚡";
    }

    private String obtenerTipoArchivo(VehiculoElectrico v) {
        if (v instanceof AutoElectrico)      return "auto";
        if (v instanceof MotoElectrica)      return "moto";
        if (v instanceof BicicletaElectrica) return "bicicleta";
        if (v instanceof PatinetaElectrica)  return "patineta";
        return "vehiculo";
    }

    private String obtenerEstadoTexto(VehiculoElectrico v) {
        return switch (v.getEstado()) {
            case DISPONIBLE    -> "Disponible";
            case MANTENIMIENTO -> "Mantenimiento";
            default            -> "Disponible";
        };
    }

    private String colorEstado(VehiculoElectrico v) {
        return switch (v.getEstado()) {
            case DISPONIBLE    -> "#1a5c30";
            case MANTENIMIENTO -> "#7a6000";
            default            -> "#1a5c30";
        };
    }

    private String formatearPrecio(double precio) {
        return "$" + String.format("%,.0f", precio).replace(",", ".");
    }

    private String estiloCardNormal() {
        return "-fx-background-color:#1a1a1a; -fx-background-radius:14;" +
                "-fx-border-color:#2e2e2e; -fx-border-radius:14; -fx-border-width:1;" +
                "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.4),10,0,0,2);";
    }

    private String estiloCardHover() {
        return "-fx-background-color:#1e1e1e; -fx-background-radius:14;" +
                "-fx-border-color:#9B0F1F; -fx-border-radius:14; -fx-border-width:1;" +
                "-fx-effect:dropshadow(gaussian,rgba(155,15,31,0.25),16,0,0,0);";
    }

    @FXML public void cambiarInicio(ActionEvent event) {
        utilidades.cambiarEscenaConTransicion(event, "/FXML/MenuPrincipal.fxml");
    }
    @FXML public void cambiarCompras(ActionEvent event) {
        utilidades.cambiarEscenaConTransicion(event, "/FXML/MenuCompras.fxml");
    }
    @FXML
    public void cambiarContrato(ActionEvent event) {
        try { utilidades.cambiarEscenaConTransicion(event, "/FXML/Contratos.fxml"); }
        catch (Exception e) { e.printStackTrace(); }
    }

    @FXML public void cambiarAlquiler(ActionEvent event) {
        utilidades.cambiarEscenaConTransicion(event, "/FXML/Alquiler.fxml");
    }
    @FXML public void cambiarFlota(ActionEvent event) {
        utilidades.cambiarEscenaConTransicion(event, "/FXML/MenuFlota.fxml");
    }
    @FXML public void cambiarAsistente(ActionEvent event) {
        utilidades.cambiarEscenaConTransicion(event, "/FXML/AsistenteAI.fxml");
    }
}