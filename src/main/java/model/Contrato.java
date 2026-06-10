package model;

import java.time.LocalDate;

public class Contrato {

    private static final double DESCUENTO_CONTADO          = 0.05;
    private static final double DESCUENTO_CLIENTE_RECURRENTE = 0.03;
    private static final int    MINIMO_CONTRATOS_RECURRENTE  = 2;

    private Long id;
    private LocalDate fechaVenta;
    private Cliente cliente;
    private VehiculoElectrico vehiculoElectrico;
    private double precioFinal;
    private String formaDePago;
    private Empleado empleado;
    private String estadoContrato;

    public Contrato(Cliente cliente, VehiculoElectrico vehiculoElectrico, double precioFinal, Long id, String formaDePago, LocalDate fehcaVenta, String estadoContrato, Empleado empleado) {
        this.cliente = cliente;
        this.vehiculoElectrico = vehiculoElectrico;
        this.precioFinal = precioFinal;
        this.id = id;
        this.formaDePago = formaDePago;
        this.fechaVenta = fehcaVenta;
        this.estadoContrato = estadoContrato;
        this.empleado = empleado;
    }

    public double calcularDescuento() {
        double descuento = 0.0;

        if (formaDePago.equalsIgnoreCase("CONTADO")) {
            descuento += precioFinal * DESCUENTO_CONTADO;
        }

        if (cliente.tieneContratos() &&
                cliente.getContratos().size() >= MINIMO_CONTRATOS_RECURRENTE) {
            descuento += precioFinal * DESCUENTO_CLIENTE_RECURRENTE;
        }

        return descuento;
    }

    public String generarResumen(){
        return  "===== RESUMEN DEL CONTRATO =====" + "\n" +
                "ID Contrato   : " + id                              + "\n" +
                "Fecha         : " + fechaVenta                           + "\n" +
                "Estado        : " + estadoContrato                          + "\n" +
                "--- Cliente ---"                                     + "\n" +
                "Nombre        : " + cliente.getNombre()             + "\n" +
                "Cédula        : " + cliente.getDocumentoId()             + "\n" +
                "--- Vehículo ---"                                    + "\n" +
                "Vehículo      : " + vehiculoElectrico.getMarca() + " " +
                vehiculoElectrico.getModelo()             + "\n" +
                "--- Empleado ---"                                    + "\n" +
                "Gestionado por: " + empleado.getNombre()            + "\n" +
                "--- Pago ---"                                        + "\n" +
                "Forma de pago : " + formaDePago                       + "\n" +
                "Descuento     : $" + calcularDescuento()            + "\n" +
                "Precio total  : $" + precioFinal                    + "\n" +
                "================================";
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public String getEstadoContrato() {
        return estadoContrato;
    }

    public String getFormaDePago() {
        return formaDePago;
    }

    public LocalDate getFechaVenta() {
        return fechaVenta;
    }

    public Long getId() {
        return id;
    }

    public double getPrecioFinal() {
        return precioFinal;
    }

    public VehiculoElectrico getVehiculoElectrico() {
        return vehiculoElectrico;
    }
}
