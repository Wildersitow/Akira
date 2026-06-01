package view;

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
import service.ServiceCompras;
import service.ServiceException;
import service.ServiceVehiculo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ControladorMenuCompras {

    @FXML private FlowPane         flowVehiculos;
    @FXML private VBox             panelVacio;
    @FXML private TextField        txtBuscar;
    @FXML private ComboBox<String> comboTipo;
    @FXML private ComboBox<String> comboPrecio;
    @FXML private ComboBox<String> comboMarca;
    @FXML private ComboBox<String> comboAutonomia;
    @FXML private ComboBox<String> comboOrdenar;
    @FXML private Label            lblResultados;
    @FXML private Button           btnCarrito;

    private final UtilidadesFX    utilidades      = new UtilidadesFX();
    private final ServiceVehiculo serviceVehiculo = new ServiceVehiculo();
    private final ServiceCompras  serviceCompras  = new ServiceCompras();

    private List<VehiculoElectrico>       todosLosVehiculos = new ArrayList<>();
    private final List<VehiculoElectrico> carrito           = new ArrayList<>();

    @FXML
    public void initialize() {
        poblarCombosFijos();
        cargarVehiculos();
    }

    private void poblarCombosFijos() {
        comboTipo.getItems().addAll(
                "Todos", "Auto Eléctrico", "Moto Eléctrica",
                "Bicicleta Eléctrica", "Patineta Eléctrica");
        comboPrecio.getItems().addAll(
                "Sin límite", "Hasta $10.000.000", "Hasta $30.000.000",
                "Hasta $60.000.000", "Hasta $100.000.000");
        comboAutonomia.getItems().addAll(
                "Sin filtro", "Más de 50 km", "Más de 100 km",
                "Más de 200 km", "Más de 400 km");
        comboOrdenar.getItems().addAll(
                "Relevancia", "Precio: menor a mayor",
                "Precio: mayor a menor", "Más reciente");
    }

    @FXML
    public void cargarVehiculos() {
        try {
            todosLosVehiculos = serviceVehiculo.obtenerDisponibles();
        } catch (ServiceException e) {
            todosLosVehiculos = new ArrayList<>();
            System.err.println("Error cargando vehículos: " + e.getMessage());
        }

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
        if (!buscar.isEmpty())
            filtrados = filtrados.stream()
                    .filter(v -> v.getMarca().toLowerCase().contains(buscar)
                            || v.getModelo().toLowerCase().contains(buscar))
                    .collect(Collectors.toList());

        String tipo = comboTipo.getValue();
        if (tipo != null && !tipo.equals("Todos"))
            filtrados = filtrados.stream()
                    .filter(v -> obtenerTipo(v).equals(tipo))
                    .collect(Collectors.toList());

        String precio = comboPrecio.getValue();
        if (precio != null && !precio.equals("Sin límite")) {
            double max = serviceCompras.extraerPrecioMax(precio);
            filtrados = filtrados.stream()
                    .filter(v -> v.getPrecioBase() <= max)
                    .collect(Collectors.toList());
        }

        String marca = comboMarca.getValue();
        if (marca != null && !marca.equals("Todas"))
            filtrados = filtrados.stream()
                    .filter(v -> v.getMarca().equalsIgnoreCase(marca))
                    .collect(Collectors.toList());

        String autonomia = comboAutonomia.getValue();
        if (autonomia != null && !autonomia.equals("Sin filtro")) {
            double min = serviceCompras.extraerAutonomiaMin(autonomia);
            filtrados = filtrados.stream()
                    .filter(v -> v.getAutonomiaKm() != null && v.getAutonomiaKm() >= min)
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
        comboPrecio.setValue(null);
        comboMarca.setValue(null);
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
        for (VehiculoElectrico v : lista)
            flowVehiculos.getChildren().add(crearCard(v));
    }

    private VBox crearCard(VehiculoElectrico v) {
        VBox card = new VBox(0);
        card.setPrefWidth(260);
        card.setStyle(estiloCardNormal());
        card.setOnMouseEntered(e -> card.setStyle(estiloCardHover()));
        card.setOnMouseExited(e  -> card.setStyle(estiloCardNormal()));

        StackPane zonaImagen = new StackPane();
        zonaImagen.setPrefHeight(155);
        zonaImagen.setStyle("-fx-background-color:#141414; -fx-background-radius:14 14 0 0;");

        Label badgeTipo = new Label(obtenerEmoji(v) + " " + obtenerTipo(v));
        badgeTipo.setStyle("-fx-background-color:#9B0F1F; -fx-text-fill:white; -fx-font-size:10px;" +
                "-fx-font-weight:bold; -fx-background-radius:6; -fx-padding:3 8;");
        StackPane.setAlignment(badgeTipo, Pos.TOP_LEFT);
        StackPane.setMargin(badgeTipo, new Insets(10, 0, 0, 10));

        ImageView imgView = new ImageView();
        imgView.setFitWidth(200);
        imgView.setFitHeight(120);
        imgView.setPreserveRatio(true);
        imgView.setSmooth(true);
        cargarImagenVehiculo(imgView, v);
        zonaImagen.getChildren().addAll(imgView, badgeTipo);

        VBox zonaInfo = new VBox(6);
        zonaInfo.setPadding(new Insets(14, 16, 14, 16));

        Label lblNombre = new Label(v.getMarca() + " " + v.getModelo());
        lblNombre.setStyle("-fx-text-fill:#eeeeee; -fx-font-size:15px; -fx-font-weight:bold; -fx-font-family:'Rajdhani';");
        lblNombre.setWrapText(true);

        Label lblAnio = new Label(v.getAnio() + "  •  " + v.getColor());
        lblAnio.setStyle("-fx-text-fill:#666; -fx-font-size:11px;");

        Label lblPrecio = new Label(formatearPrecio(v.getPrecioBase()));
        lblPrecio.setStyle("-fx-text-fill:#9B0F1F; -fx-font-size:18px; -fx-font-weight:bold; -fx-font-family:'Rajdhani';");

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

        HBox botones = new HBox(8);
        botones.setAlignment(Pos.CENTER);
        botones.setPadding(new Insets(10, 0, 0, 0));

        Button btnInfo = new Button("Ver info");
        btnInfo.setPrefWidth(108);
        btnInfo.setPrefHeight(34);
        btnInfo.setStyle(estiloBotonSecundario());
        btnInfo.setOnMouseEntered(e -> btnInfo.setStyle(estiloBotonSecundarioHover()));
        btnInfo.setOnMouseExited(e  -> btnInfo.setStyle(estiloBotonSecundario()));
        btnInfo.setOnAction(e -> verInformacion(v));

        Button btnAgregar = new Button("🛒 Añadir");
        btnAgregar.setPrefWidth(108);
        btnAgregar.setPrefHeight(34);
        btnAgregar.setStyle(estiloBotonPrimario());
        btnAgregar.setOnMouseEntered(e -> btnAgregar.setStyle(estiloBotonPrimarioHover()));
        btnAgregar.setOnMouseExited(e  -> btnAgregar.setStyle(estiloBotonPrimario()));
        btnAgregar.setOnAction(e -> agregarAlCarrito(v, btnAgregar));

        botones.getChildren().addAll(btnInfo, btnAgregar);
        zonaInfo.getChildren().addAll(lblNombre, lblAnio, lblPrecio, infoRapida, sep, botones);
        card.getChildren().addAll(zonaImagen, zonaInfo);
        return card;
    }

    private void agregarAlCarrito(VehiculoElectrico v, Button btnAgregar) {
        try {
            serviceCompras.validarCompra(v);
        } catch (ServiceException e) {
            mostrarToast("⚠ " + e.getMessage());
            return;
        }

        boolean yaEsta = carrito.stream().anyMatch(c -> c.getId().equals(v.getId()));
        if (yaEsta) {
            mostrarToast("⚠ Ya está en el carrito");
            return;
        }

        carrito.add(v);
        actualizarBtnCarrito();

        btnAgregar.setText("✓ Añadido");
        btnAgregar.setStyle("-fx-background-color:#1a5c30; -fx-text-fill:white; -fx-border-radius:7;" +
                "-fx-background-radius:7; -fx-font-size:12px; -fx-font-weight:bold; -fx-cursor:hand;");
        btnAgregar.setDisable(true);

        mostrarToast("✓ " + v.getMarca() + " " + v.getModelo() + " añadido al carrito");
    }

    private void actualizarBtnCarrito() {
        if (btnCarrito != null)
            btnCarrito.setText("🛒  Carrito (" + carrito.size() + ")");
    }

    @FXML
    public void abrirCarrito() {
        if (carrito.isEmpty()) {
            mostrarToast("El carrito está vacío");
            return;
        }
        mostrarVentanaCarrito();
    }

    private void mostrarVentanaCarrito() {
        Stage ventana = new Stage();
        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.setTitle("Carrito de compras");
        ventana.setResizable(false);

        VBox root = new VBox(0);
        root.setStyle("-fx-background-color:#0f0f0f;");
        root.setPrefWidth(520);

        HBox header = new HBox();
        header.setStyle("-fx-background-color:#9B0F1F; -fx-padding:16 20;");
        header.setAlignment(Pos.CENTER_LEFT);
        Label titulo = new Label("🛒  Tu carrito  (" + carrito.size() + " ítem" + (carrito.size() != 1 ? "s" : "") + ")");
        titulo.setStyle("-fx-text-fill:white; -fx-font-size:16px; -fx-font-weight:bold; -fx-font-family:'Rajdhani';");
        header.getChildren().add(titulo);
        root.getChildren().add(header);

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:transparent; -fx-border-color:transparent;");
        scroll.setPrefHeight(320);

        VBox listaItems = new VBox(1);
        listaItems.setStyle("-fx-background-color:#0f0f0f;");

        double[] totalRef = {serviceCompras.calcularTotalCarrito(carrito)};

        for (VehiculoElectrico v : carrito) {
            HBox fila = new HBox(12);
            fila.setAlignment(Pos.CENTER_LEFT);
            fila.setPadding(new Insets(12, 20, 12, 20));
            fila.setStyle("-fx-border-color:transparent transparent #1e1e1e transparent; -fx-border-width:0 0 1 0;");

            Label emoji = new Label(obtenerEmoji(v));
            emoji.setStyle("-fx-font-size:22px;");

            VBox info = new VBox(2);
            HBox.setHgrow(info, Priority.ALWAYS);
            Label nombre = new Label(v.getMarca() + " " + v.getModelo());
            nombre.setStyle("-fx-text-fill:#ddd; -fx-font-size:13px; -fx-font-weight:bold;");
            Label tipo = new Label(obtenerTipo(v) + "  •  " + v.getAnio());
            tipo.setStyle("-fx-text-fill:#555; -fx-font-size:11px;");
            info.getChildren().addAll(nombre, tipo);

            Label precioV = new Label(formatearPrecio(v.calcularPrecioFinal()));
            precioV.setStyle("-fx-text-fill:#9B0F1F; -fx-font-size:14px; -fx-font-weight:bold;");

            Button btnQuitar = new Button("✕");
            btnQuitar.setStyle("-fx-background-color:transparent; -fx-text-fill:#555;" +
                    "-fx-border-color:#333; -fx-border-radius:4; -fx-background-radius:4;" +
                    "-fx-cursor:hand; -fx-padding:2 6; -fx-font-size:11px;");
            btnQuitar.setOnAction(e -> {
                carrito.remove(v);
                actualizarBtnCarrito();
                ventana.close();
                if (!carrito.isEmpty()) mostrarVentanaCarrito();
            });

            fila.getChildren().addAll(emoji, info, precioV, btnQuitar);
            listaItems.getChildren().add(fila);
        }

        scroll.setContent(listaItems);
        root.getChildren().add(scroll);

        VBox footer = new VBox(12);
        footer.setStyle("-fx-background-color:#111111; -fx-border-color:#1e1e1e; -fx-border-width:1 0 0 0; -fx-padding:16 20;");

        HBox filaTotal = new HBox();
        filaTotal.setAlignment(Pos.CENTER_LEFT);
        Label lblTotal = new Label("Total:");
        lblTotal.setStyle("-fx-text-fill:#888; -fx-font-size:14px;");
        Region esp = new Region();
        HBox.setHgrow(esp, Priority.ALWAYS);
        Label lblTotalVal = new Label(formatearPrecio(totalRef[0]));
        lblTotalVal.setStyle("-fx-text-fill:white; -fx-font-size:20px; -fx-font-weight:bold; -fx-font-family:'Rajdhani';");
        filaTotal.getChildren().addAll(lblTotal, esp, lblTotalVal);

        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER_RIGHT);

        Button btnVaciar = new Button("Vaciar carrito");
        btnVaciar.setStyle("-fx-background-color:transparent; -fx-text-fill:#9B0F1F;" +
                "-fx-border-color:#9B0F1F; -fx-border-radius:7; -fx-background-radius:7;" +
                "-fx-border-width:1; -fx-font-size:13px; -fx-cursor:hand; -fx-padding:8 16;");
        btnVaciar.setOnAction(e -> {
            carrito.clear();
            actualizarBtnCarrito();
            ventana.close();
            aplicarFiltros();
        });

        Button btnComprar = new Button("Confirmar compra  →");
        btnComprar.setStyle("-fx-background-color:#9B0F1F; -fx-text-fill:white;" +
                "-fx-border-radius:7; -fx-background-radius:7;" +
                "-fx-font-size:13px; -fx-font-weight:bold; -fx-cursor:hand; -fx-padding:8 20;");
        btnComprar.setOnAction(e -> {
            ventana.close();
            confirmarCompra();
        });

        botones.getChildren().addAll(btnVaciar, btnComprar);
        footer.getChildren().addAll(filaTotal, botones);
        root.getChildren().add(footer);

        ventana.setScene(new Scene(root));
        ventana.show();
    }

    private void confirmarCompra() {
        try {
            serviceCompras.validarCarrito(carrito);
        } catch (ServiceException e) {
            utilidades.mostrarAlerta(Alert.AlertType.ERROR, "Error en el carrito", e.getMessage());
            return;
        }

        StringBuilder resumen = new StringBuilder();
        double total = serviceCompras.calcularTotalCarrito(carrito);
        for (VehiculoElectrico v : carrito) {
            resumen.append("• ").append(v.getMarca()).append(" ").append(v.getModelo())
                    .append("  →  ").append(formatearPrecio(v.calcularPrecioFinal())).append("\n");
        }
        resumen.append("\nTotal: ").append(formatearPrecio(total));

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Compra confirmada");
        alert.setHeaderText("¡Gracias por tu compra!");
        alert.setContentText(resumen.toString());
        alert.showAndWait();

        carrito.clear();
        actualizarBtnCarrito();
        cargarVehiculos();
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
        hTitulo.setStyle("-fx-text-fill:white; -fx-font-size:18px; -fx-font-weight:bold; -fx-font-family:'Rajdhani';");
        Label hTipo = new Label(obtenerTipo(v) + "  •  " + v.getAnio());
        hTipo.setStyle("-fx-text-fill:#ffcccc; -fx-font-size:12px;");
        infoHeader.getChildren().addAll(hTitulo, hTipo);
        header.getChildren().addAll(emoji, infoHeader);
        root.getChildren().add(header);

        VBox body = new VBox(0);
        agregarFila(body, "Color",        v.getColor(),                        false);
        agregarFila(body, "Año",          String.valueOf(v.getAnio()),          true);
        agregarFila(body, "Precio base",  formatearPrecio(v.getPrecioBase()),   false);
        agregarFila(body, "Precio final", formatearPrecio(v.calcularPrecioFinal()), true);
        agregarFila(body, "Estado",       v.getEstado().name(),                 false);
        if (v.getAutonomiaKm() != null && v.getAutonomiaKm() > 0)
            agregarFila(body, "Autonomía", v.getAutonomiaKm().intValue() + " km", true);
        if (v.getCapacidadBateria() != null && v.getCapacidadBateria() > 0)
            agregarFila(body, "Batería", v.getCapacidadBateria() + " kWh", false);

        if (v instanceof AutoElectrico a) {
            agregarFila(body, "Tipo",      a.getTipoCarro(),                      false);
            agregarFila(body, "Puertas",   String.valueOf(a.getNumeroPuertas()),   true);
            agregarFila(body, "Pasajeros", String.valueOf(a.getNumeroPasajeros()), false);
            agregarFila(body, "Tracción",  a.getTraccion(),                       true);
        } else if (v instanceof MotoElectrica m) {
            agregarFila(body, "Tipo moto", m.getTipoMoto(),        false);
            agregarFila(body, "Peso",      m.getPesoKg() + " kg",  true);
        } else if (v instanceof BicicletaElectrica b) {
            agregarFila(body, "Asistencia", b.getTipoAsistencia(),               false);
            agregarFila(body, "Marchas",    String.valueOf(b.getNumeroMarchas()), true);
        } else if (v instanceof PatinetaElectrica p) {
            agregarFila(body, "Vel. máx.",  p.getVelocidadMaximaKmH() + " km/h", false);
            agregarFila(body, "Plegable",   p.isEsPlegable() ? "Sí" : "No",      true);
            agregarFila(body, "Carga máx.", p.getCargaMaximaKg() + " kg",         false);
        }
        root.getChildren().add(body);

        HBox pie = new HBox();
        pie.setStyle("-fx-background-color:#111; -fx-padding:14 20; -fx-border-color:#1e1e1e; -fx-border-width:1 0 0 0;");
        pie.setAlignment(Pos.CENTER_RIGHT);
        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setStyle("-fx-background-color:#9B0F1F; -fx-text-fill:white;" +
                "-fx-border-radius:7; -fx-background-radius:7; -fx-font-size:13px; -fx-cursor:hand; -fx-padding:8 20;");
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

    private void cargarImagenVehiculo(ImageView imgView, VehiculoElectrico v) {
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

    private void mostrarToast(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Akira");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        Platform.runLater(alert::show);
        new Thread(() -> {
            try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
            Platform.runLater(alert::close);
        }).start();
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

    private String estiloCardNormal() {
        return "-fx-background-color:#1a1a1a; -fx-background-radius:14; -fx-border-color:#2e2e2e;" +
                "-fx-border-radius:14; -fx-border-width:1; -fx-effect:dropshadow(gaussian,rgba(0,0,0,0.4),10,0,0,2);";
    }

    private String estiloCardHover() {
        return "-fx-background-color:#1e1e1e; -fx-background-radius:14; -fx-border-color:#9B0F1F;" +
                "-fx-border-radius:14; -fx-border-width:1; -fx-effect:dropshadow(gaussian,rgba(155,15,31,0.25),16,0,0,0);";
    }

    private String estiloBotonSecundario() {
        return "-fx-background-color:transparent; -fx-text-fill:#9B0F1F; -fx-border-color:#9B0F1F;" +
                "-fx-border-radius:7; -fx-background-radius:7; -fx-border-width:1;" +
                "-fx-font-size:12px; -fx-font-weight:bold; -fx-cursor:hand;";
    }

    private String estiloBotonSecundarioHover() {
        return "-fx-background-color:#9B0F1F; -fx-text-fill:white; -fx-border-color:#9B0F1F;" +
                "-fx-border-radius:7; -fx-background-radius:7; -fx-border-width:1;" +
                "-fx-font-size:12px; -fx-font-weight:bold; -fx-cursor:hand;";
    }

    private String estiloBotonPrimario() {
        return "-fx-background-color:#9B0F1F; -fx-text-fill:white; -fx-border-radius:7;" +
                "-fx-background-radius:7; -fx-font-size:12px; -fx-font-weight:bold; -fx-cursor:hand;";
    }

    private String estiloBotonPrimarioHover() {
        return "-fx-background-color:#c0392b; -fx-text-fill:white; -fx-border-radius:7;" +
                "-fx-background-radius:7; -fx-font-size:12px; -fx-font-weight:bold; -fx-cursor:hand;";
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