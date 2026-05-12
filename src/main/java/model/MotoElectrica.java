package model;

public class MotoElectrica extends VehiculoElectrico {

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

        return 0;
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
