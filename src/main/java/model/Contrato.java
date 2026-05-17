package model;

import java.time.LocalDate;
import java.util.Date;

public class Contrato {

    private static final double DESCUENTO_CONTADO          = 0.05;
    private static final double DESCUENTO_CLIENTE_RECURRENTE = 0.03;
    private static final int    MINIMO_CONTRATOS_RECURRENTE  = 2;

    private Long id;
    private LocalDate fehcaVenta;
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
        this.fehcaVenta = fehcaVenta;
        this.estadoContrato = estadoContrato;
        this.empleado = empleado;
    }

    public double calcularDescuento(){
        return 0;
    }

    public String generarResumen(){
        return "";
    }


}
