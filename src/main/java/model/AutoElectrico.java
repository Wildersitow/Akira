package model;

public class AutoElectrico extends VehiculoElectrico {

    private static final double RECARGO_SUV_PICKUP   = 0.15;
    private static final double RECARGO_COUPE        = 0.10;
    private static final double RECARGO_AWD          = 0.10;

    private final int numeroPuertas;
    private final int numeroPasajeros;
    private final String tipoCarga;
    private final String traccion;

    public AutoElectrico(int anio, Double autonomiaKm, Double capacidadBateria,
                         String color, EstadoVehiculo estado, String id,
                         String marca, String modelo, Double precioBase,
                         int velocidadMaxima, int numeroPasajeros,
                         int numeroPuertas, String tipoCarga, String traccion) {
        super(anio, autonomiaKm, capacidadBateria, color, estado, id, marca, modelo, precioBase, velocidadMaxima);
        this.numeroPasajeros = numeroPasajeros;
        this.numeroPuertas = numeroPuertas;
        this.tipoCarga = tipoCarga;
        this.traccion = traccion;
    }

    @Override
        public double calcularPrecioFinal() {
            double precioFinal = getPrecioBase();
            if (tipoCarga != null) {
                if (tipoCarga.equalsIgnoreCase("SUV") || tipoCarga.equalsIgnoreCase("Pickup"))
                    precioFinal += precioFinal * RECARGO_SUV_PICKUP;
                else if (tipoCarga.equalsIgnoreCase("Coupe"))
                    precioFinal += precioFinal * RECARGO_COUPE;
            }
            if (traccion != null && traccion.equalsIgnoreCase("AWD"))
                precioFinal += precioFinal * RECARGO_AWD;
            return precioFinal;
        }

    @Override
    public String toString() {
        return "AutoEléctrico | " + super.toString() +
                " | Tipo: " + tipoCarga +
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

    public String getTipoCarga() {
        return tipoCarga;
    }

    public String getTraccion() {
        return traccion;
    }
}
