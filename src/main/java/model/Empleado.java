package model;

public class Empleado extends Persona {

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
