package model;

public class AutoElectrico extends VehiculoElectrico {

    private final int numeroPuertas;
    private final int numeroPasajeros;
    private final String tipoCarga;
    private final String traccion;

    public AutoElectrico(String id, String marca, String modelo,
                         Double autonomiaKm, Double capacidadBateria,
                         Double precioBase, int velocidadMaxima,
                         int numeroPuertas, int numeroPasajeros,
                         String tipoCarga, String traccion) {

        super(id, marca, modelo, autonomiaKm, capacidadBateria, precioBase, velocidadMaxima);

        this.numeroPuertas = numeroPuertas;
        this.numeroPasajeros = numeroPasajeros;
        this.tipoCarga = tipoCarga;
        this.traccion = traccion;
    }
}
