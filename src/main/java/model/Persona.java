package model;

import java.io.Serializable;

public abstract  class Persona implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String nombre;
    private final String nombreUsuario;
    private final String contraseña;
    private final String documentoid;
    private final String email;
    private final String rol;
    private final long telefono;
    private Long id;

    public Persona(String nombre, String nombreUsuario, String contraseña, String documentoid, String email, String rol, long telefono) {
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
        return this.contraseña != null && this.contraseña.equals(contraseña);
    }

    public String getEmail() { return email; }

    public String getContraseña() { return contraseña; }

    public long getTelefono() { return telefono; }

    public String getRol() {
        return rol;
    }

        public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

}
