package model;

import java.time.LocalDate;

public class ContratoAlquiler extends Contrato {

    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    private final int       diasAlquilados;
    private final String    periodo;

    public ContratoAlquiler(Cliente cliente, VehiculoElectrico vehiculoElectrico, double precioFinal, Long id, String formaDePago, LocalDate fechaInicio, String estadoContrato, Empleado empleado, String periodo, int diasAlquilados) {
        super(cliente, vehiculoElectrico, precioFinal, id, formaDePago,
                fechaInicio, estadoContrato, empleado);
        this.fechaInicio    = fechaInicio;
        this.fechaFin       = fechaInicio.plusDays(diasAlquilados);
        this.diasAlquilados = diasAlquilados;
        this.periodo        = periodo;
    }

    public LocalDate getFechaInicio()   { return fechaInicio; }
    public LocalDate getFechaFin()      { return fechaFin; }
    public int       getDiasAlquilados(){ return diasAlquilados; }
    public String    getPeriodo()       { return periodo; }
}