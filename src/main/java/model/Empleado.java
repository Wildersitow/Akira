package model;

import java.io.Serializable;

public class Empleado extends Persona implements Serializable {

    private static final double PORCENTAJE_SALUD      = 0.04;
    private static final double PORCENTAJE_PENSION     = 0.04;
    private static final double PORCENTAJE_RETENCION   = 0.07;
    private static final long serialVersionUID = 1L;

    private final String codigoEmpleado;
    private final String cargo;
    private final double salario;
    public String getCargo() { return cargo; }
    public double getSalario() { return salario; }
    public String getCodigoEmpleado() { return codigoEmpleado; }


    public Empleado(String nombre, String nombreUsuario, String contraseña, String documentoid, String email, String rol, long telefono, String codigoEmpleado, String cargo, double salario) {
        super(nombre, nombreUsuario, contraseña, documentoid, email, rol, telefono);
        this.codigoEmpleado = codigoEmpleado;
        this.cargo = cargo;
        this.salario = salario;
    }

    public double calcularSalarioNeto() {
        double descuentoSalud    = salario * PORCENTAJE_SALUD;
        double descuentoPension  = salario * PORCENTAJE_PENSION;
        double descuentoRetencion = salario * PORCENTAJE_RETENCION;
        return salario - descuentoSalud - descuentoPension - descuentoRetencion;
    }

    public boolean esAdmin() {
        return this.cargo.equalsIgnoreCase("Administrador");
    }
}
