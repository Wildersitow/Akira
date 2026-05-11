package model;

public abstract  class Persona {

    private String nombre;
    private String nombreUsuario;
    private String contraseña;
    private String documentoid;
    private String email;
    private String rol;
    private int telefono;

    public Persona(String nombre, String nombreUsuario, String contraseña, String documentoid, String email, String rol, int telefono) {
        this.nombre = nombre;
        this.nombreUsuario = nombreUsuario;
        this.contraseña = contraseña;
        this.documentoid = documentoid;
        this.email = email;
        this.rol = rol;
        this.telefono = telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getDocumentoId() {
        return documentoid;
    }

    public boolean verificarContraseña(String contraseña) {
        return this.contraseña != null && this.contraseña.equals(this.contraseña);
    }

    public String getRol() {
        return rol;
    }
}
