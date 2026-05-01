package model;

public abstract  class Persona {

    private int nombre;
    private int email;
    private int telefono;

    public Persona(int nombre, int email, int telefono) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
    }
}
