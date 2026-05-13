package model;

import java.io.Serializable;

public class Empleado extends Persona implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String codigoEmpleado;
    private final String cargo;
    private final double salario;

    public Empleado(String nombre, String nombreUsuario, String contraseña, String documentoid, String email, String rol, int telefono, String codigoEmpleado, String cargo, double salario) {
        super(nombre, nombreUsuario, contraseña, documentoid, email, rol, telefono);
        this.codigoEmpleado = codigoEmpleado;
        this.cargo = cargo;
        this.salario = salario;
    }
}
