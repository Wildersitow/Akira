package model;

public abstract  class Persona {

    private String nombre;
    private String email;
    private int telefono;

    public Persona(String nombre, String email, int telefono) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
    }
}
