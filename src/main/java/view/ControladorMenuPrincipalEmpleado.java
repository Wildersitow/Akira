package view;

import dao.AutoElectricoDAO;
import dao.BicicletaElectricaDAO;
import dao.ClienteDAO;
import dao.MotoElectricaDAO;
import dao.PatinetaElectricaDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.*;
import service.SesionCuenta;

import java.util.ArrayList;

public class ControladorMenuPrincipalEmpleado {

    @FXML private Label label_usuario;
    @FXML private Label labelTotalVehiculos;
    @FXML private Label labelIngresos;
    @FXML private Label labelTotalClientes;
    @FXML private Label labelContratosActivos;

    @FXML private TableView<AutoElectrico>       tablaCompras;
    @FXML private TableColumn<AutoElectrico, String> colCompraId;
    @FXML private TableColumn<AutoElectrico, String> colCompraModelo;
    @FXML private TableColumn<AutoElectrico, String> colCompraPrecio;
    @FXML private TableColumn<AutoElectrico, String> colCompraCliente;
    @FXML private TableColumn<AutoElectrico, String> colCompraFecha;

    @FXML private TableView<VehiculoElectrico>       tablaAlquileres;
    @FXML private TableColumn<VehiculoElectrico, String> colAlqCliente;
    @FXML private TableColumn<VehiculoElectrico, String> colAlqVehiculo;
    @FXML private TableColumn<VehiculoElectrico, String> colAlqFecha;
    @FXML private TableColumn<VehiculoElectrico, String> colAlqEstado;

    private final AutoElectricoDAO      autoDAO  = new AutoElectricoDAO();
    private final MotoElectricaDAO      motoDAO  = new MotoElectricaDAO();
    private final BicicletaElectricaDAO biciDAO  = new BicicletaElectricaDAO();
    private final PatinetaElectricaDAO  patiDAO  = new PatinetaElectricaDAO();
    private final ClienteDAO            clienteDAO = new ClienteDAO();

    private final UtilidadesFX utilidades = new UtilidadesFX();

    @FXML
    public void initialize() {
        mostrarNombreUsuario();
        configurarTablaCompras();
        configurarTablaAlquileres();
        cargarKPIs();
        cargarTablaCompras();
        cargarTablaAlquileres();
    }

    private void mostrarNombreUsuario() {
        if (label_usuario == null) return;
        Persona usuario = SesionCuenta.getUsuarioActual();
        if (usuario != null) {
            String nombre = usuario.getNombre();
            label_usuario.setText(nombre == null || nombre.isBlank()
                    ? usuario.getNombreUsuario()
                    : nombre);
        }
    }

    private void cargarKPIs() {
        int totalVehiculos = 0;
        double ingresos    = 0.0;
        int vendidos       = 0;
        int alquilados     = 0;

        try {
            ArrayList<AutoElectrico> autos = autoDAO.obtenerTodos();
            totalVehiculos += autos.size();
            for (AutoElectrico a : autos) {
                if (a.getEstado() == EstadoVehiculo.VENDIDO)   { ingresos += a.getPrecioBase(); vendidos++; }
                if (a.getEstado() == EstadoVehiculo.ALQUILADO) alquilados++;
            }
        } catch (Exception ignored) {}

        try {
            ArrayList<MotoElectrica> motos = motoDAO.obtenerTodos();
            totalVehiculos += motos.size();
            for (MotoElectrica m : motos) {
                if (m.getEstado() == EstadoVehiculo.VENDIDO)   { ingresos += m.getPrecioBase(); vendidos++; }
                if (m.getEstado() == EstadoVehiculo.ALQUILADO) alquilados++;
            }
        } catch (Exception ignored) {}

        try {
            ArrayList<BicicletaElectrica> bicis = biciDAO.obtenerTodos();
            totalVehiculos += bicis.size();
            for (BicicletaElectrica b : bicis) {
                if (b.getEstado() == EstadoVehiculo.VENDIDO)   { ingresos += b.getPrecioBase(); vendidos++; }
                if (b.getEstado() == EstadoVehiculo.ALQUILADO) alquilados++;
            }
        } catch (Exception ignored) {}

        try {
            ArrayList<PatinetaElectrica> pats = patiDAO.obtenerTodos();
            totalVehiculos += pats.size();
            for (PatinetaElectrica p : pats) {
                if (p.getEstado() == EstadoVehiculo.VENDIDO)   { ingresos += p.getPrecioBase(); vendidos++; }
                if (p.getEstado() == EstadoVehiculo.ALQUILADO) alquilados++;
            }
        } catch (Exception ignored) {}

        int totalClientes = 0;
        try { totalClientes = clienteDAO.obtenerTodos().size(); } catch (Exception ignored) {}

        labelTotalVehiculos.setText(String.valueOf(totalVehiculos));
        labelIngresos.setText(String.format("$%,.0f", ingresos));
        labelTotalClientes.setText(String.valueOf(totalClientes));
        labelContratosActivos.setText(String.valueOf(alquilados));
    }

    private void configurarTablaCompras() {
        // Usamos AutoElectrico como tipo base; mostramos solo autos vendidos
        colCompraId.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().getId()));
        colCompraModelo.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().getMarca() + " " + c.getValue().getModelo()));
        colCompraPrecio.setCellValueFactory(
                c -> new SimpleStringProperty(String.format("$%,.0f", c.getValue().getPrecioBase())));
        colCompraCliente.setCellValueFactory(
                c -> new SimpleStringProperty("—"));   // sin DAO de contratos por ahora
        colCompraFecha.setCellValueFactory(
                c -> new SimpleStringProperty("—"));
    }

    private void cargarTablaCompras() {
        ObservableList<AutoElectrico> vendidos = FXCollections.observableArrayList();
        try {
            for (AutoElectrico a : autoDAO.obtenerTodos()) {
                if (a.getEstado() == EstadoVehiculo.VENDIDO) vendidos.add(a);
            }
        } catch (Exception e) {
            System.err.println("Error cargando compras: " + e.getMessage());
        }
        tablaCompras.setItems(vendidos);
    }

    private void configurarTablaAlquileres() {
        colAlqCliente.setCellValueFactory(
                c -> new SimpleStringProperty("—"));   // sin DAO de contratos por ahora
        colAlqVehiculo.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().getMarca() + " " + c.getValue().getModelo()));
        colAlqFecha.setCellValueFactory(
                c -> new SimpleStringProperty("—"));
        colAlqEstado.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().getEstado().name()));
    }

    private void cargarTablaAlquileres() {
        ObservableList<VehiculoElectrico> alquilados = FXCollections.observableArrayList();
        try {
            for (AutoElectrico a : autoDAO.obtenerTodos())
                if (a.getEstado() == EstadoVehiculo.ALQUILADO) alquilados.add(a);
        } catch (Exception ignored) {}
        try {
            for (MotoElectrica m : motoDAO.obtenerTodos())
                if (m.getEstado() == EstadoVehiculo.ALQUILADO) alquilados.add(m);
        } catch (Exception ignored) {}
        try {
            for (BicicletaElectrica b : biciDAO.obtenerTodos())
                if (b.getEstado() == EstadoVehiculo.ALQUILADO) alquilados.add(b);
        } catch (Exception ignored) {}
        try {
            for (PatinetaElectrica p : patiDAO.obtenerTodos())
                if (p.getEstado() == EstadoVehiculo.ALQUILADO) alquilados.add(p);
        } catch (Exception ignored) {}

        tablaAlquileres.setItems(alquilados);
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