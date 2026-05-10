package model;

public class Empleado extends Persona {

    private String codigoEmpleado;
    private String cargo;
    private double salario;

    public Empleado(String codigoEmpleado, String cargo, double salario) {
        this.codigoEmpleado = codigoEmpleado;
        this.cargo = cargo;
        this.salario = salario;
    }
}
