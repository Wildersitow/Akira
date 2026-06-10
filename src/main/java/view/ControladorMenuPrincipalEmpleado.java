package view;

import dao.*;
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

    @FXML private TableView<Contrato>               tablaCompras;
    @FXML private TableColumn<Contrato, String>     colCompraId;
    @FXML private TableColumn<Contrato, String>     colCompraModelo;
    @FXML private TableColumn<Contrato, String>     colCompraPrecio;
    @FXML private TableColumn<Contrato, String>     colCompraCliente;
    @FXML private TableColumn<Contrato, String>     colCompraFecha;

    @FXML private TableView<ContratoAlquiler>               tablaAlquileres;
    @FXML private TableColumn<ContratoAlquiler, String>     colAlqCliente;
    @FXML private TableColumn<ContratoAlquiler, String>     colAlqVehiculo;
    @FXML private TableColumn<ContratoAlquiler, String>     colAlqFecha;
    @FXML private TableColumn<ContratoAlquiler, String>     colAlqEstado;

    private final AutoElectricoDAO      autoDAO    = new AutoElectricoDAO();
    private final MotoElectricaDAO      motoDAO    = new MotoElectricaDAO();
    private final BicicletaElectricaDAO biciDAO    = new BicicletaElectricaDAO();
    private final PatinetaElectricaDAO  patiDAO    = new PatinetaElectricaDAO();
    private final ClienteDAO            clienteDAO = new ClienteDAO();
    private final ContratoDAO           contratoDAO        = new ContratoDAO();
    private final ContratoAlquilerDAO   contratoAlquilerDAO = new ContratoAlquilerDAO();

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
        int    totalVehiculos = 0;
        double ingresos       = 0.0;
        int    alquilados     = 0;

        try { totalVehiculos += autoDAO.obtenerTodos().size(); } catch (Exception ignored) {}
        try { totalVehiculos += motoDAO.obtenerTodos().size(); } catch (Exception ignored) {}
        try { totalVehiculos += biciDAO.obtenerTodos().size(); } catch (Exception ignored) {}
        try { totalVehiculos += patiDAO.obtenerTodos().size(); } catch (Exception ignored) {}

        // Ingresos = suma de precios de contratos de compra
        try {
            for (Contrato c : contratoDAO.obtenerTodos())
                ingresos += c.getPrecioFinal();
        } catch (Exception ignored) {}

        try {
            for (ContratoAlquiler ca : contratoAlquilerDAO.obtenerTodos())
                if ("ACTIVO".equalsIgnoreCase(ca.getEstadoContrato()))
                    alquilados++;
        } catch (Exception ignored) {}

        int totalClientes = 0;
        try { totalClientes = clienteDAO.obtenerTodos().size(); } catch (Exception ignored) {}

        labelTotalVehiculos.setText(String.valueOf(totalVehiculos));
        labelIngresos.setText(String.format("$%,.0f", ingresos));
        labelTotalClientes.setText(String.valueOf(totalClientes));
        labelContratosActivos.setText(String.valueOf(alquilados));
    }

    private void configurarTablaCompras() {
        colCompraId.setCellValueFactory(
                c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));

        colCompraModelo.setCellValueFactory(c -> {
            VehiculoElectrico v = c.getValue().getVehiculoElectrico();
            return new SimpleStringProperty(v != null
                    ? v.getMarca() + " " + v.getModelo()
                    : "—");
        });

        colCompraPrecio.setCellValueFactory(
                c -> new SimpleStringProperty(
                        String.format("$%,.0f", c.getValue().getPrecioFinal())));

        colCompraCliente.setCellValueFactory(c -> {
            Cliente cl = c.getValue().getCliente();
            if (cl == null) return new SimpleStringProperty("—");
            String nombre = cl.getNombre();
            return new SimpleStringProperty(
                    nombre == null || nombre.isBlank() ? cl.getNombreUsuario() : nombre);
        });

        colCompraFecha.setCellValueFactory(c -> {
            var fecha = c.getValue().getFechaVenta();
            return new SimpleStringProperty(fecha != null ? fecha.toString() : "—");
        });
    }

    private void cargarTablaCompras() {
        ObservableList<Contrato> compras = FXCollections.observableArrayList();
        try {
            ArrayList<Contrato> todos = contratoDAO.obtenerTodos();
            compras.addAll(todos);
        } catch (Exception e) {
            System.err.println("Error cargando contratos de compra: " + e.getMessage());
        }
        tablaCompras.setItems(compras);
    }

    private void configurarTablaAlquileres() {
        colAlqCliente.setCellValueFactory(c -> {
            Cliente cl = c.getValue().getCliente();
            if (cl == null) return new SimpleStringProperty("—");
            String nombre = cl.getNombre();
            return new SimpleStringProperty(
                    nombre == null || nombre.isBlank() ? cl.getNombreUsuario() : nombre);
        });

        colAlqVehiculo.setCellValueFactory(c -> {
            VehiculoElectrico v = c.getValue().getVehiculoElectrico();
            return new SimpleStringProperty(v != null
                    ? v.getMarca() + " " + v.getModelo()
                    : "—");
        });

        colAlqFecha.setCellValueFactory(c -> {
            var fecha = c.getValue().getFechaInicio();
            return new SimpleStringProperty(fecha != null ? fecha.toString() : "—");
        });

        colAlqEstado.setCellValueFactory(
                c -> new SimpleStringProperty(
                        c.getValue().getEstadoContrato() != null
                                ? c.getValue().getEstadoContrato()
                                : "—"));
    }

    private void cargarTablaAlquileres() {
        ObservableList<ContratoAlquiler> alquileres = FXCollections.observableArrayList();
        try {
            ArrayList<ContratoAlquiler> todos = contratoAlquilerDAO.obtenerTodos();
            alquileres.addAll(todos);
        } catch (Exception e) {
            System.err.println("Error cargando contratos de alquiler: " + e.getMessage());
        }
        tablaAlquileres.setItems(alquileres);
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
    @FXML public void cambiarLogin(ActionEvent event) {
        SesionCuenta.cerrarSesion();
        utilidades.cambiarEscenaConTransicion(event, "/FXML/Login.fxml");
    }
}