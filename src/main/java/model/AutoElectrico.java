package model;

public class AutoElectrico extends VehiculoElectrico {

    private static final double RECARGO_SUV_PICKUP   = 0.15;
    private static final double RECARGO_COUPE        = 0.10;
    private static final double RECARGO_AWD          = 0.10;
    private static final double RECARGO_ALTA_POTENCIA = 0.08;
    private static final int    UMBRAL_POTENCIA_KW   = 150;

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

        double precioFinal = getPrecioBase();

        if (tipoCarro.equalsIgnoreCase("SUV") || tipoCarro.equalsIgnoreCase("Pickup")) {
            precioFinal += precioFinal * RECARGO_SUV_PICKUP;
        } else if (tipoCarro.equalsIgnoreCase("Coupe")) {
            precioFinal += precioFinal * RECARGO_COUPE;
        }

        if (traccion.equalsIgnoreCase("AWD")) {
            precioFinal += precioFinal * RECARGO_AWD;
        }

        if (getPotenciaMotorKW() > UMBRAL_POTENCIA_KW) {
            precioFinal += precioFinal * RECARGO_ALTA_POTENCIA;
        }

        return precioFinal;
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
