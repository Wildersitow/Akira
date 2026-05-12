package model;

public class BicicletaElectrica extends VehiculoElectrico {

    private int numeroMarchas;
    private int velocidadMaximaKmH;
    private String tipoAsistencia;
    private String materialMarco;

    public BicicletaElectrica(int anio, Double autonomiaKm, Double capacidadBateria, String color, EstadoVehiculo estado, String id, String marca, String modelo, Double precioBase, int potenciaMotorKW, int velocidadMaxima, String materialMarco, int velocidadMaximaKmH, String tipoAsistencia, int numeroMarchas) {
        super(anio, autonomiaKm, capacidadBateria, color, estado, id, marca, modelo, precioBase, potenciaMotorKW, velocidadMaxima);
        this.materialMarco = materialMarco;
        this.velocidadMaximaKmH = velocidadMaximaKmH;
        this.tipoAsistencia = tipoAsistencia;
        this.numeroMarchas = numeroMarchas;
    }

    @Override
    public double calcularPrecioFinal() {
        return 0;
    }

    @Override
    public String toString() {
        return "BicicletaEléctrica | " + super.toString() +
                " | Asistencia: " + tipoAsistencia +
                " | Vel. máx: " + velocidadMaximaKmH + " km/h" +
                " | Cambios: " + numeroMarchas +
                " | Marco: " + materialMarco;
    }

    public String getMaterialMarco() {
        return materialMarco;
    }

    public void setMaterialMarco(String materialMarco) {
        this.materialMarco = materialMarco;
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
