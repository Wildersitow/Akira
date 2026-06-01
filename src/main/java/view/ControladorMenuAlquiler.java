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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.*;
import service.ServiceAlquiler;
import service.ServiceException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ControladorMenuAlquiler {

    @FXML private FlowPane             flowVehiculos;
    @FXML private VBox                 panelVacio;
    @FXML private TextField            txtBuscar;
    @FXML private ComboBox<String>     comboTipo;
    @FXML private ComboBox<String>     comboPrecio;
    @FXML private ComboBox<String>     comboMarca;
    @FXML private ComboBox<String>     comboPeriodo;
    @FXML private ComboBox<String>     comboAutonomia;
    @FXML private ComboBox<String>     comboOrdenar;
    @FXML private Label                lblResultados;
    @FXML private Button               btnReservas;

    private final UtilidadesFX   utilidades      = new UtilidadesFX();
    private final ServiceAlquiler serviceAlquiler = new ServiceAlquiler();

    private List<VehiculoElectrico> todosLosVehiculos = new ArrayList<>();

    private final List<ItemReserva> reservas = new ArrayList<>();

    private final AutoElectricoDAO      autoDAO = new AutoElectricoDAO();
    private final MotoElectricaDAO      motoDAO = new MotoElectricaDAO();
    private final BicicletaElectricaDAO biciDAO = new BicicletaElectricaDAO();
    private final PatinetaElectricaDAO  patiDAO = new PatinetaElectricaDAO();

    public static class ItemReserva {
        public final VehiculoElectrico vehiculo;
        public final String            periodo;   // "1 día" | "1 semana" | "1 mes"
        public final int               dias;
        public final double            totalPagar;

        public ItemReserva(VehiculoElectrico vehiculo, String periodo,
                           int dias, double totalPagar) {
            this.vehiculo   = vehiculo;
            this.periodo    = periodo;
            this.dias       = dias;
            this.totalPagar = totalPagar;
        }
    }

    @FXML
    public void initialize() {
        poblarCombosFijos();
        cargarVehiculos();
    }

    private void poblarCombosFijos() {
        comboTipo.getItems().addAll(
                "Todos", "Auto Eléctrico", "Moto Eléctrica",
                "Bicicleta Eléctrica", "Patineta Eléctrica"
        );
        comboPrecio.getItems().addAll(
                "Sin límite",
                "Hasta $50.000/día",
                "Hasta $150.000/día",
                "Hasta $300.000/día",
                "Hasta $500.000/día"
        );
        comboPeriodo.getItems().addAll(
                "Cualquier período", "1 día", "1 semana", "1 mes"
        );
        comboAutonomia.getItems().addAll(
                "Sin filtro", "Más de 50 km", "Más de 100 km",
                "Más de 200 km", "Más de 400 km"
        );
        comboOrdenar.getItems().addAll(
                "Relevancia",
                "Precio/día: menor a mayor",
                "Precio/día: mayor a menor",
                "Más reciente"
        );
    }

    @FXML
    public void cargarVehiculos() {
        todosLosVehiculos.clear();

        try { todosLosVehiculos.addAll(autoDAO.obtenerTodos()); }
        catch (ServiceException e) { System.err.println("Error autos: " + e.getMessage()); }

        try { todosLosVehiculos.addAll(motoDAO.obtenerTodos()); }
        catch (ServiceException e) { System.err.println("Error motos: " + e.getMessage()); }

        try { todosLosVehiculos.addAll(biciDAO.obtenerTodos()); }
        catch (ServiceException e) { System.err.println("Error bicicletas: " + e.getMessage()); }

        try { todosLosVehiculos.addAll(patiDAO.obtenerTodos()); }
        catch (ServiceException e) { System.err.println("Error patinetas: " + e.getMessage()); }

        todosLosVehiculos = todosLosVehiculos.stream()
                .filter(VehiculoElectrico::estaDisponible)
                .collect(Collectors.toList());

        List<String> marcas = todosLosVehiculos.stream()
                .map(VehiculoElectrico::getMarca)
                .distinct().sorted()
                .collect(Collectors.toList());
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

        String precio = comboPrecio.getValue();
        if (precio != null && !precio.equals("Sin límite")) {
            double maxDia = extraerPrecioMaxDia(precio);
            filtrados = filtrados.stream()
                    .filter(v -> serviceAlquiler.calcularPrecioPorDia(v) <= maxDia)
                    .collect(Collectors.toList());
        }

        String marca = comboMarca.getValue();
        if (marca != null && !marca.equals("Todas")) {
            filtrados = filtrados.stream()
                    .filter(v -> v.getMarca().equalsIgnoreCase(marca))
                    .collect(Collectors.toList());
        }

        String autonomia = comboAutonomia.getValue();
        if (autonomia != null && !autonomia.equals("Sin filtro")) {
            double min = extraerAutonomiaMin(autonomia);
            filtrados = filtrados.stream()
                    .filter(v -> v.getAutonomiaKm() != null && v.getAutonomiaKm() >= min)
                    .collect(Collectors.toList());
        }

        String orden = comboOrdenar.getValue();
        if ("Precio/día: menor a mayor".equals(orden)) {
            filtrados.sort((a, b) -> Double.compare(
                    serviceAlquiler.calcularPrecioPorDia(a),
                    serviceAlquiler.calcularPrecioPorDia(b)));
        } else if ("Precio/día: mayor a menor".equals(orden)) {
            filtrados.sort((a, b) -> Double.compare(
                    serviceAlquiler.calcularPrecioPorDia(b),
                    serviceAlquiler.calcularPrecioPorDia(a)));
        } else if ("Más reciente".equals(orden)) {
            filtrados.sort((a, b) -> Integer.compare(b.getAnio(), a.getAnio()));
        }

        renderizarCards(filtrados);
    }

    @FXML
    public void limpiarFiltros() {
        txtBuscar.clear();
        comboTipo.setValue(null);
        comboPrecio.setValue(null);
        comboMarca.setValue(null);
        comboPeriodo.setValue(null);
        comboAutonomia.setValue(null);
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

        for (VehiculoElectrico v : lista) {
            flowVehiculos.getChildren().add(crearCard(v));
        }
    }

    private VBox crearCard(VehiculoElectrico v) {
        VBox card = new VBox(0);
        card.setPrefWidth(260);
        card.setStyle(estiloCardNormal());

        card.setOnMouseEntered(e -> card.setStyle(estiloCardHover()));
        card.setOnMouseExited(e  -> card.setStyle(estiloCardNormal()));

        StackPane zonaImagen = new StackPane();
        zonaImagen.setPrefHeight(155);
        zonaImagen.setStyle(
                "-fx-background-color:#141414;" +
                        "-fx-background-radius:14 14 0 0;");

        Label badgeTipo = new Label(obtenerEmoji(v) + "  " + obtenerTipo(v));
        badgeTipo.setStyle(
                "-fx-background-color:#9B0F1F; -fx-text-fill:white;" +
                        "-fx-font-size:10px; -fx-font-weight:bold;" +
                        "-fx-background-radius:6; -fx-padding:3 8;");
        StackPane.setAlignment(badgeTipo, Pos.TOP_LEFT);
        StackPane.setMargin(badgeTipo, new Insets(10, 0, 0, 10));

        double precioDia = serviceAlquiler.calcularPrecioPorDia(v);
        Label badgePrecio = new Label(formatearPrecio(precioDia) + "/día");
        badgePrecio.setStyle(
                "-fx-background-color:rgba(0,0,0,0.7); -fx-text-fill:#9B0F1F;" +
                        "-fx-font-size:10px; -fx-font-weight:bold;" +
                        "-fx-background-radius:6; -fx-padding:3 8;");
        StackPane.setAlignment(badgePrecio, Pos.TOP_RIGHT);
        StackPane.setMargin(badgePrecio, new Insets(10, 10, 0, 0));

        ImageView imgView = new ImageView();
        imgView.setFitWidth(200);
        imgView.setFitHeight(120);
        imgView.setPreserveRatio(true);
        imgView.setSmooth(true);
        cargarImagenVehiculo(imgView, v);

        zonaImagen.getChildren().addAll(imgView, badgeTipo, badgePrecio);

        VBox zonaInfo = new VBox(6);
        zonaInfo.setPadding(new Insets(14, 16, 14, 16));

        Label lblNombre = new Label(v.getMarca() + " " + v.getModelo());
        lblNombre.setStyle(
                "-fx-text-fill:#eeeeee; -fx-font-size:15px;" +
                        "-fx-font-weight:bold; -fx-font-family:'Rajdhani';");
        lblNombre.setWrapText(true);

        Label lblAnio = new Label(v.getAnio() + "  •  " + v.getColor());
        lblAnio.setStyle("-fx-text-fill:#666; -fx-font-size:11px;");

        VBox tablaPrecio = new VBox(4);
        tablaPrecio.setStyle(
                "-fx-background-color:#141414; -fx-background-radius:6;" +
                        "-fx-padding:8;");
        agregarFilaPrecio(tablaPrecio, "1 día",    serviceAlquiler.calcularTotal(v, "1 día"));
        agregarFilaPrecio(tablaPrecio, "1 semana", serviceAlquiler.calcularTotal(v, "1 semana"));
        agregarFilaPrecio(tablaPrecio, "1 mes",    serviceAlquiler.calcularTotal(v, "1 mes"));

        HBox infoRapida = new HBox(12);
        infoRapida.setAlignment(Pos.CENTER_LEFT);
        if (v.getAutonomiaKm() != null && v.getAutonomiaKm() > 0) {
            Label lblAuto = new Label("⚡ " + v.getAutonomiaKm().intValue() + " km");
            lblAuto.setStyle("-fx-text-fill:#555; -fx-font-size:11px;");
            infoRapida.getChildren().add(lblAuto);
        }
        Label lblEstado = new Label("✓ Disponible");
        lblEstado.setStyle("-fx-text-fill:#2d8a4e; -fx-font-size:11px; -fx-font-weight:bold;");
        infoRapida.getChildren().add(lblEstado);

        Region sep = new Region();
        sep.setPrefHeight(1);
        sep.setStyle("-fx-background-color:#2e2e2e;");

        ComboBox<String> comboPeriodoCard = new ComboBox<>();
        comboPeriodoCard.getItems().addAll("1 día", "1 semana", "1 mes");
        comboPeriodoCard.setPromptText("Selecciona período");
        comboPeriodoCard.setPrefWidth(228);
        comboPeriodoCard.setPrefHeight(32);
        comboPeriodoCard.setStyle(
                "-fx-background-color:#1a1a1a; -fx-border-color:#333;" +
                        "-fx-border-radius:6; -fx-background-radius:6;" +
                        "-fx-font-size:12px;");

        HBox botones = new HBox(8);
        botones.setAlignment(Pos.CENTER);
        botones.setPadding(new Insets(6, 0, 0, 0));

        Button btnInfo = crearBotonInfo(v);

        Button btnReservar = new Button("🔑 Reservar");
        btnReservar.setPrefWidth(108);
        btnReservar.setPrefHeight(34);
        aplicarEstiloBotonPrimario(btnReservar);
        btnReservar.setOnAction(e ->
                agregarReserva(v, comboPeriodoCard, btnReservar));

        botones.getChildren().addAll(btnInfo, btnReservar);

        zonaInfo.getChildren().addAll(
                lblNombre, lblAnio, tablaPrecio, infoRapida, sep,
                comboPeriodoCard, botones);
        card.getChildren().addAll(zonaImagen, zonaInfo);

        return card;
    }

    private void agregarFilaPrecio(VBox parent, String periodo, double total) {
        HBox fila = new HBox();
        fila.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label(periodo);
        lbl.setStyle("-fx-text-fill:#666; -fx-font-size:11px;");
        lbl.setPrefWidth(70);
        Region esp = new Region();
        HBox.setHgrow(esp, Priority.ALWAYS);
        Label val = new Label(formatearPrecio(total));
        val.setStyle("-fx-text-fill:#9B0F1F; -fx-font-size:11px; -fx-font-weight:bold;");
        fila.getChildren().addAll(lbl, esp, val);
        parent.getChildren().add(fila);
    }

    private void agregarReserva(VehiculoElectrico v,
                                ComboBox<String> comboPeriodoCard,
                                Button btnReservar) {
        String periodo = comboPeriodoCard.getValue();

        if (periodo == null) {
            mostrarToast("⚠ Selecciona un período primero");
            return;
        }

        try {
            serviceAlquiler.validarReserva(v, periodo);
        } catch (ServiceException ex) {
            mostrarToast("⚠ " + ex.getMessage());
            return;
        }

        boolean yaEsta = reservas.stream()
                .anyMatch(r -> r.vehiculo.getId().equals(v.getId()));
        if (yaEsta) {
            mostrarToast("⚠ Este vehículo ya está en tus reservas");
            return;
        }

        double total = serviceAlquiler.calcularTotal(v, periodo);
        int    dias  = serviceAlquiler.diasDePeriodo(periodo);

        reservas.add(new ItemReserva(v, periodo, dias, total));
        actualizarBtnReservas();

        btnReservar.setText("✓ Reservado");
        btnReservar.setStyle(
                "-fx-background-color:#1a5c30; -fx-text-fill:white;" +
                        "-fx-border-radius:7; -fx-background-radius:7;" +
                        "-fx-font-size:12px; -fx-font-weight:bold; -fx-cursor:hand;");
        btnReservar.setDisable(true);
        comboPeriodoCard.setDisable(true);

        mostrarToast("✓ " + v.getMarca() + " " + v.getModelo()
                + " reservado por " + periodo);
    }

    private void actualizarBtnReservas() {
        if (btnReservas != null) {
            btnReservas.setText("📋  Reservas (" + reservas.size() + ")");
        }
    }

    @FXML
    public void abrirReservas() {
        if (reservas.isEmpty()) {
            mostrarToast("No tienes reservas aún");
            return;
        }
        mostrarVentanaReservas();
    }

    private void mostrarVentanaReservas() {
        Stage ventana = new Stage();
        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.setTitle("Mis reservas de alquiler");
        ventana.setResizable(false);

        VBox root = new VBox(0);
        root.setStyle("-fx-background-color:#0f0f0f;");
        root.setPrefWidth(560);

        HBox header = new HBox();
        header.setStyle("-fx-background-color:#9B0F1F; -fx-padding:16 20;");
        header.setAlignment(Pos.CENTER_LEFT);
        Label titulo = new Label("📋  Reservas (" + reservas.size() + ")");
        titulo.setStyle(
                "-fx-text-fill:white; -fx-font-size:16px;" +
                        "-fx-font-weight:bold; -fx-font-family:'Rajdhani';");
        header.getChildren().add(titulo);
        root.getChildren().add(header);

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:transparent; -fx-border-color:transparent;");
        scroll.setPrefHeight(340);

        VBox listaItems = new VBox(1);
        listaItems.setStyle("-fx-background-color:#0f0f0f;");

        double[] totalRef = {0};

        for (ItemReserva item : reservas) {
            totalRef[0] += item.totalPagar;

            HBox fila = new HBox(12);
            fila.setAlignment(Pos.CENTER_LEFT);
            fila.setPadding(new Insets(14, 20, 14, 20));
            fila.setStyle(
                    "-fx-border-color:transparent transparent #1e1e1e transparent;" +
                            "-fx-border-width:0 0 1 0;");

            Label emoji = new Label(obtenerEmoji(item.vehiculo));
            emoji.setStyle("-fx-font-size:22px;");

            VBox info = new VBox(3);
            HBox.setHgrow(info, Priority.ALWAYS);

            Label nombre = new Label(
                    item.vehiculo.getMarca() + " " + item.vehiculo.getModelo());
            nombre.setStyle(
                    "-fx-text-fill:#ddd; -fx-font-size:13px; -fx-font-weight:bold;");

            HBox detalles = new HBox(8);
            Label periodoLbl = new Label("🗓 " + item.periodo);
            periodoLbl.setStyle(
                    "-fx-text-fill:#888; -fx-font-size:11px;" +
                            "-fx-background-color:#1e1e1e; -fx-background-radius:4;" +
                            "-fx-padding:2 6;");
            Label diasLbl = new Label(item.dias + " día(s)");
            diasLbl.setStyle("-fx-text-fill:#555; -fx-font-size:11px;");
            detalles.getChildren().addAll(periodoLbl, diasLbl);

            info.getChildren().addAll(nombre, detalles);

            Label precioV = new Label(formatearPrecio(item.totalPagar));
            precioV.setStyle(
                    "-fx-text-fill:#9B0F1F; -fx-font-size:14px; -fx-font-weight:bold;");

            Button btnQuitar = new Button("✕");
            btnQuitar.setStyle(
                    "-fx-background-color:transparent; -fx-text-fill:#555;" +
                            "-fx-border-color:#333; -fx-border-radius:4; -fx-background-radius:4;" +
                            "-fx-cursor:hand; -fx-padding:2 6; -fx-font-size:11px;");
            btnQuitar.setOnAction(e -> {
                reservas.remove(item);
                actualizarBtnReservas();
                ventana.close();
                if (!reservas.isEmpty()) mostrarVentanaReservas();
                aplicarFiltros();
            });

            fila.getChildren().addAll(emoji, info, precioV, btnQuitar);
            listaItems.getChildren().add(fila);
        }

        scroll.setContent(listaItems);
        root.getChildren().add(scroll);

        VBox footer = new VBox(12);
        footer.setStyle(
                "-fx-background-color:#111111;" +
                        "-fx-border-color:#1e1e1e; -fx-border-width:1 0 0 0;" +
                        "-fx-padding:16 20;");

        HBox filaTotal = new HBox();
        filaTotal.setAlignment(Pos.CENTER_LEFT);
        Label lblTotal = new Label("Total a pagar:");
        lblTotal.setStyle("-fx-text-fill:#888; -fx-font-size:14px;");
        Region esp = new Region();
        HBox.setHgrow(esp, Priority.ALWAYS);
        Label lblTotalVal = new Label(formatearPrecio(totalRef[0]));
        lblTotalVal.setStyle(
                "-fx-text-fill:white; -fx-font-size:20px;" +
                        "-fx-font-weight:bold; -fx-font-family:'Rajdhani';");
        filaTotal.getChildren().addAll(lblTotal, esp, lblTotalVal);

        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER_RIGHT);

        Button btnVaciar = new Button("Cancelar todo");
        btnVaciar.setStyle(
                "-fx-background-color:transparent; -fx-text-fill:#9B0F1F;" +
                        "-fx-border-color:#9B0F1F; -fx-border-radius:7; -fx-background-radius:7;" +
                        "-fx-border-width:1; -fx-font-size:13px; -fx-cursor:hand; -fx-padding:8 16;");
        btnVaciar.setOnAction(e -> {
            reservas.clear();
            actualizarBtnReservas();
            ventana.close();
            aplicarFiltros();
        });

        Button btnConfirmar = new Button("Confirmar alquiler  →");
        btnConfirmar.setStyle(
                "-fx-background-color:#9B0F1F; -fx-text-fill:white;" +
                        "-fx-border-radius:7; -fx-background-radius:7;" +
                        "-fx-font-size:13px; -fx-font-weight:bold;" +
                        "-fx-cursor:hand; -fx-padding:8 20;");
        btnConfirmar.setOnAction(e -> {
            ventana.close();
            confirmarAlquiler();
        });

        botones.getChildren().addAll(btnVaciar, btnConfirmar);
        footer.getChildren().addAll(filaTotal, botones);
        root.getChildren().add(footer);

        ventana.setScene(new Scene(root));
        ventana.show();
    }

    private void confirmarAlquiler() {
        StringBuilder resumen = new StringBuilder();
        double total = 0;
        for (ItemReserva item : reservas) {
            resumen.append("• ").append(item.vehiculo.getMarca())
                    .append(" ").append(item.vehiculo.getModelo())
                    .append("  (").append(item.periodo).append(")")
                    .append("  →  ").append(formatearPrecio(item.totalPagar))
                    .append("\n");
            total += item.totalPagar;
        }
        resumen.append("\nTotal: ").append(formatearPrecio(total));

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Alquiler confirmado");
        alert.setHeaderText("¡Tu reserva está lista!");
        alert.setContentText(resumen.toString());
        alert.showAndWait();

        reservas.clear();
        actualizarBtnReservas();
        cargarVehiculos();
    }

    private Button crearBotonInfo(VehiculoElectrico v) {
        Button btn = new Button("Ver info");
        btn.setPrefWidth(108);
        btn.setPrefHeight(34);
        btn.setStyle(estiloBotonSecundario());
        btn.setOnMouseEntered(e -> btn.setStyle(estiloBotonSecundarioHover()));
        btn.setOnMouseExited(e  -> btn.setStyle(estiloBotonSecundario()));
        btn.setOnAction(e -> verInformacion(v));
        return btn;
    }

    private void verInformacion(VehiculoElectrico v) {
        Stage ventana = new Stage();
        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.setTitle("Información: " + v.getMarca() + " " + v.getModelo());
        ventana.setResizable(false);

        VBox root = new VBox(0);
        root.setStyle("-fx-background-color:#0f0f0f;");
        root.setPrefWidth(460);

        HBox header = new HBox(10);
        header.setStyle("-fx-background-color:#9B0F1F; -fx-padding:16 20;");
        header.setAlignment(Pos.CENTER_LEFT);
        Label emoji = new Label(obtenerEmoji(v));
        emoji.setStyle("-fx-font-size:24px;");
        VBox infoHeader = new VBox(2);
        Label hTitulo = new Label(v.getMarca() + " " + v.getModelo());
        hTitulo.setStyle(
                "-fx-text-fill:white; -fx-font-size:18px;" +
                        "-fx-font-weight:bold; -fx-font-family:'Rajdhani';");
        Label hTipo = new Label(obtenerTipo(v) + "  •  " + v.getAnio());
        hTipo.setStyle("-fx-text-fill:#ffcccc; -fx-font-size:12px;");
        infoHeader.getChildren().addAll(hTitulo, hTipo);
        header.getChildren().addAll(emoji, infoHeader);
        root.getChildren().add(header);

        VBox seccionPrecios = new VBox(0);
        seccionPrecios.setStyle("-fx-background-color:#141414; -fx-padding:12 20;");
        Label tituloPrecios = new Label("PRECIOS DE ALQUILER");
        tituloPrecios.setStyle(
                "-fx-text-fill:#9B0F1F; -fx-font-size:11px;" +
                        "-fx-font-weight:bold; -fx-padding:0 0 8 0;");
        seccionPrecios.getChildren().add(tituloPrecios);
        for (String per : new String[]{"1 día", "1 semana", "1 mes"}) {
            HBox fila = new HBox();
            fila.setAlignment(Pos.CENTER_LEFT);
            fila.setPadding(new Insets(4, 0, 4, 0));
            Label lPer = new Label(per);
            lPer.setStyle("-fx-text-fill:#888; -fx-font-size:12px;");
            lPer.setPrefWidth(90);
            Region espPrecio = new Region();
            HBox.setHgrow(espPrecio, Priority.ALWAYS);
            Label lVal = new Label(formatearPrecio(serviceAlquiler.calcularTotal(v, per)));
            lVal.setStyle(
                    "-fx-text-fill:#eeeeee; -fx-font-size:12px; -fx-font-weight:bold;");
            fila.getChildren().addAll(lPer, espPrecio, lVal);
            seccionPrecios.getChildren().add(fila);
        }
        root.getChildren().add(seccionPrecios);

        VBox body = new VBox(0);
        agregarFilaInfo(body, "Color",       v.getColor(),               false);
        agregarFilaInfo(body, "Año",         String.valueOf(v.getAnio()), true);
        agregarFilaInfo(body, "Estado",      v.getEstado().name(),        false);
        if (v.getAutonomiaKm() != null && v.getAutonomiaKm() > 0)
            agregarFilaInfo(body, "Autonomía", v.getAutonomiaKm().intValue() + " km", true);
        if (v.getCapacidadBateria() != null && v.getCapacidadBateria() > 0)
            agregarFilaInfo(body, "Batería", v.getCapacidadBateria() + " kWh", false);
        if (v.getPotenciaMotorKW() > 0)
            agregarFilaInfo(body, "Potencia", v.getPotenciaMotorKW() + " kW", true);

        if (v instanceof AutoElectrico a) {
            agregarFilaInfo(body, "Tipo",      a.getTipoCarro(),                 false);
            agregarFilaInfo(body, "Puertas",   String.valueOf(a.getNumeroPuertas()), true);
            agregarFilaInfo(body, "Pasajeros", String.valueOf(a.getNumeroPasajeros()), false);
            agregarFilaInfo(body, "Tracción",  a.getTraccion(),                  true);
        } else if (v instanceof MotoElectrica m) {
            agregarFilaInfo(body, "Tipo moto", m.getTipoMoto(),  false);
            agregarFilaInfo(body, "Peso",      m.getPesoKg() + " kg", true);
        } else if (v instanceof BicicletaElectrica b) {
            agregarFilaInfo(body, "Asistencia", b.getTipoAsistencia(), false);
            agregarFilaInfo(body, "Marchas",    String.valueOf(b.getNumeroMarchas()), true);
            agregarFilaInfo(body, "Marco",      b.getMaterialMarco(),  false);
        } else if (v instanceof PatinetaElectrica p) {
            agregarFilaInfo(body, "Vel. máx.",  p.getVelocidadMaximaKmH() + " km/h", false);
            agregarFilaInfo(body, "Plegable",   p.isEsPlegable() ? "Sí" : "No", true);
            agregarFilaInfo(body, "Carga máx.", p.getCargaMaximaKg() + " kg", false);
        }
        root.getChildren().add(body);

        HBox pie = new HBox();
        pie.setStyle(
                "-fx-background-color:#111; -fx-padding:14 20;" +
                        "-fx-border-color:#1e1e1e; -fx-border-width:1 0 0 0;");
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

    private void agregarFilaInfo(VBox parent, String etiqueta,
                                 String valor, boolean par) {
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

    private void cargarImagenVehiculo(ImageView imgView, VehiculoElectrico v) {
        String[] rutas = {
                "src/main/resources/Imagenes/" + v.getMarca() + "_" + v.getModelo() + ".png",
                "src/main/resources/Imagenes/" + v.getMarca() + "_" + v.getModelo() + ".jpg",
                "src/main/resources/Imagenes/" + obtenerTipoArchivo(v) + ".png"
        };
        for (String ruta : rutas) {
            File f = new File(ruta);
            if (f.exists()) {
                imgView.setImage(new Image(f.toURI().toString()));
                return;
            }
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

    private String formatearPrecio(double precio) {
        return "$" + String.format("%,.0f", precio).replace(",", ".");
    }

    private double extraerPrecioMaxDia(String opcion) {
        return switch (opcion) {
            case "Hasta $50.000/día"  ->  50_000;
            case "Hasta $150.000/día" -> 150_000;
            case "Hasta $300.000/día" -> 300_000;
            case "Hasta $500.000/día" -> 500_000;
            default -> Double.MAX_VALUE;
        };
    }

    private double extraerAutonomiaMin(String opcion) {
        return switch (opcion) {
            case "Más de 50 km"  ->  50;
            case "Más de 100 km" -> 100;
            case "Más de 200 km" -> 200;
            case "Más de 400 km" -> 400;
            default -> 0;
        };
    }

    private void mostrarToast(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Akira");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        Platform.runLater(alert::show);
        new Thread(() -> {
            try { Thread.sleep(1500); }
            catch (InterruptedException ignored) {}
            Platform.runLater(alert::close);
        }).start();
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
    private String estiloBotonSecundario() {
        return "-fx-background-color:transparent; -fx-text-fill:#9B0F1F;" +
                "-fx-border-color:#9B0F1F; -fx-border-radius:7; -fx-background-radius:7;" +
                "-fx-border-width:1; -fx-font-size:12px; -fx-font-weight:bold; -fx-cursor:hand;";
    }
    private String estiloBotonSecundarioHover() {
        return "-fx-background-color:#9B0F1F; -fx-text-fill:white;" +
                "-fx-border-color:#9B0F1F; -fx-border-radius:7; -fx-background-radius:7;" +
                "-fx-border-width:1; -fx-font-size:12px; -fx-font-weight:bold; -fx-cursor:hand;";
    }
    private void aplicarEstiloBotonPrimario(Button btn) {
        String base = "-fx-background-color:#9B0F1F; -fx-text-fill:white;" +
                "-fx-border-radius:7; -fx-background-radius:7;" +
                "-fx-font-size:12px; -fx-font-weight:bold; -fx-cursor:hand;";
        String hover = "-fx-background-color:#c0392b; -fx-text-fill:white;" +
                "-fx-border-radius:7; -fx-background-radius:7;" +
                "-fx-font-size:12px; -fx-font-weight:bold; -fx-cursor:hand;";
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e  -> btn.setStyle(base));
    }

    @FXML public void cambiarInicio(ActionEvent event) {
        utilidades.cambiarEscenaConTransicion(event, "/FXML/MenuPrincipal.fxml");
    }
    @FXML public void cambiarCompras(ActionEvent event) {
        utilidades.cambiarEscenaConTransicion(event, "/FXML/MenuCompras.fxml");
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