package model;

public class AutoElectrico extends VehiculoElectrico {

    private final int numeroPuertas;
    private final int numeroPasajeros;
    private final String tipoCarro;
    private final String traccion;

    public AutoElectrico(int anio, Double autonomiaKm, Double capacidadBateria, String color, EstadoVehiculo estado, String id, String marca, String modelo, Double precioBase, int potenciaMotorKW, int velocidadMaxima, int numeroPasajeros, int numeroPuertas, String tipoCarro, String traccion) {
        super(anio, autonomiaKm, capacidadBateria, color, estado, id, marca, modelo, precioBase, potenciaMotorKW, velocidadMaxima);
        this.numeroPasajeros = numeroPasajeros;
        this.numeroPuertas = numeroPuertas;
        this.tipoCarro = tipoCarro;
        this.traccion = traccion;
    }

    @Override
    public double calcularPrecioFinal(){

        return 0;
    }

    @Override
    public String toString() {
        return "AutoEléctrico | " + super.toString() +
                " | Tipo: " + tipoCarro +
                " | Puertas: " + numeroPuertas +
                " | Pasajeros: " + numeroPasajeros +
                " | Tracción: " + traccion;
    }


    public int getNumeroPasajeros() {
        return numeroPasajeros;
    }

    public int getNumeroPuertas() {
        return numeroPuertas;
    }

    public String getTipoCarro() {
        return tipoCarro;
    }

    public String getTraccion() {
        return traccion;
    }
}
