package model;

public class BicicletaElectrica extends VehiculoElectrico {

    private static final double RECARGO_THROTTLE  = 0.08;
    private static final double RECARGO_AUTONOMIA = 0.05;
    private static final int    UMBRAL_AUTONOMIA_KM = 80;

    private int numeroMarchas;
    private int velocidadMaximaKmH;
    private String tipoAsistencia;

    public BicicletaElectrica(int anio, Double autonomiaKm, Double capacidadBateria, String color, EstadoVehiculo estado, String id, String marca, String modelo, Double precioBase, int velocidadMaximaKmH, String tipoAsistencia, int numeroMarchas) {
        super(anio, autonomiaKm, capacidadBateria, color, estado, id, marca, modelo, precioBase, 0);
        this.velocidadMaximaKmH = velocidadMaximaKmH;
        this.tipoAsistencia = tipoAsistencia;
        this.numeroMarchas = numeroMarchas;
    }

    @Override
    public double calcularPrecioFinal() {
        double precioFinal = getPrecioBase();

        if (tipoAsistencia.equalsIgnoreCase("Throttle")) {
            precioFinal += precioFinal * RECARGO_THROTTLE;
        }
        if (getAutonomiaKm() > UMBRAL_AUTONOMIA_KM) {
            precioFinal += precioFinal * RECARGO_AUTONOMIA;
        }
        return precioFinal;
    }

    @Override
    public String toString() {
        return "BicicletaEléctrica | " + super.toString() +
                " | Asistencia: " + tipoAsistencia +
                " | Vel. máx: " + velocidadMaximaKmH + " km/h" +
                " | Cambios: " + numeroMarchas; // ← quitar marco
    }

    public int getVelocidadMaximaKmH() {
        return velocidadMaximaKmH;
    }

    public void setVelocidadMaximaKmH(int velocidadMaximaKmH) {
        this.velocidadMaximaKmH = velocidadMaximaKmH;
    }

    public String getTipoAsistencia() {
        return tipoAsistencia;
    }

    public void setTipoAsistencia(String tipoAsistencia) {
        this.tipoAsistencia = tipoAsistencia;
    }

    public int getNumeroMarchas() {
        return numeroMarchas;
    }

    public void setNumeroMarchas(int numeroMarchas) {
        this.numeroMarchas = numeroMarchas;
    }
}
