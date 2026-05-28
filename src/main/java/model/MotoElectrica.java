package model;

public class MotoElectrica extends VehiculoElectrico {

    private static final double RECARGO_DEPORTIVA  = 0.12;
    private static final double RECARGO_OFFROAD    = 0.10;
    private static final double RECARGO_AUTONOMIA  = 0.05;
    private static final int    UMBRAL_AUTONOMIA_KM = 150;

    private int alturaAsientoMm;
    private String tipoMoto;
    private double pesoKg;

    public MotoElectrica(int anio, Double autonomiaKm, Double capacidadBateria, String color, EstadoVehiculo estado, String id, String marca, String modelo, Double precioBase, int potenciaMotorKW, int velocidadMaxima, int alturaAsientoMm, String tipoMoto, double pesoKg) {
        super(anio, autonomiaKm, capacidadBateria, color, estado, id, marca, modelo, precioBase, potenciaMotorKW, velocidadMaxima);
        this.alturaAsientoMm = alturaAsientoMm;
        this.tipoMoto = tipoMoto;
        this.pesoKg = pesoKg;
    }

    @Override
    public double calcularPrecioFinal(){
        double precioFinal = getPrecioBase();

        if (tipoMoto.equalsIgnoreCase("Deportiva")) {
            precioFinal += precioFinal * RECARGO_DEPORTIVA;
        } else if (tipoMoto.equalsIgnoreCase("Off-road")) {
            precioFinal += precioFinal * RECARGO_OFFROAD;
        }
        if (getAutonomiaKm() > UMBRAL_AUTONOMIA_KM) {
            precioFinal += precioFinal * RECARGO_AUTONOMIA;
        }
        return precioFinal;
    }

    @Override
    public String toString() {
        return "MotoEléctrica | " + super.toString() +
                " | Tipo: " + tipoMoto +
                " | Peso: " + pesoKg + " kg" +
                " | Altura asiento: " + alturaAsientoMm + " mm";
    }

    public int getAlturaAsientoMm() {
        return alturaAsientoMm;
    }

    public void setAlturaAsientoMm(int alturaAsientoMm) {
        this.alturaAsientoMm = alturaAsientoMm;
    }

    public String getTipoMoto() {
        return tipoMoto;
    }

    public void setTipoMoto(String tipoMoto) {
        this.tipoMoto = tipoMoto;
    }

    public double getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(double pesoKg) {
        this.pesoKg = pesoKg;
    }
}
