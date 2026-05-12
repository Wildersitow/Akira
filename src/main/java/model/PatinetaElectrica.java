package model;

public class PatinetaElectrica extends VehiculoElectrico {

    private int velocidadMaximaKmH;
    private int cargaMaximaKg;
    private boolean esPlegable;

    public PatinetaElectrica(int anio, Double autonomiaKm, Double capacidadBateria, String color, EstadoVehiculo estado, String id, String marca, String modelo, Double precioBase, int potenciaMotorKW, int velocidadMaxima, int cargaMaximaKg, boolean esPlegable, int velocidadMaximaKmH) {
        super(anio, autonomiaKm, capacidadBateria, color, estado, id, marca, modelo, precioBase, potenciaMotorKW, velocidadMaxima);
        this.cargaMaximaKg = cargaMaximaKg;
        this.esPlegable = esPlegable;
        this.velocidadMaximaKmH = velocidadMaximaKmH;
    }

    @Override
    public double calcularPrecioFinal() {
        return 0;
    }

    @Override
    public String toString() {
        return "PatinetaEléctrica | " + super.toString() +
                " | Vel. máx: " + velocidadMaximaKmH + " km/h" +
                " | Plegable: " + (esPlegable? "Sí" : "No") +
                " | Carga máx: " + cargaMaximaKg + " kg";
    }

    public int getCargaMaximaKg() {
        return cargaMaximaKg;
    }

    public void setCargaMaximaKg(int cargaMaximaKg) {
        this.cargaMaximaKg = cargaMaximaKg;
    }

    public boolean isEsPlegable() {
        return esPlegable;
    }

    public void setEsPlegable(boolean esPlegable) {
        this.esPlegable = esPlegable;
    }

    public int getVelocidadMaximaKmH() {
        return velocidadMaximaKmH;
    }

    public void setVelocidadMaximaKmH(int velocidadMaximaKmH) {
        this.velocidadMaximaKmH = velocidadMaximaKmH;
    }
}
