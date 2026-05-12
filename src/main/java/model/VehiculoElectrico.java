package model;

public abstract class VehiculoElectrico {

    protected String Id;
    protected String Marca;
    protected String Modelo;
    protected int Anio;
    protected String Color;
    protected Double AutonomiaKm;
    protected Double CapacidadBateria;
    protected Double PrecioBase;
    protected int VelocidadMaxima;
    protected int PotenciaMotorKW;
    protected EstadoVehiculo Estado;

    public VehiculoElectrico(int anio, Double autonomiaKm, Double capacidadBateria, String color, EstadoVehiculo estado, String id, String marca, String modelo, Double precioBase, int potenciaMotorKW, int velocidadMaxima) {
        Anio = anio;
        AutonomiaKm = autonomiaKm;
        CapacidadBateria = capacidadBateria;
        Color = color;
        Estado = estado;
        Id = id;
        Marca = marca;
        Modelo = modelo;
        PrecioBase = precioBase;
        PotenciaMotorKW = potenciaMotorKW;
        VelocidadMaxima = velocidadMaxima;
    }


    public boolean estaDisponible(){
        return this.Estado == EstadoVehiculo.DISPONIBLE;
    }


    public void marcarComoVendido(){
        this.Estado = EstadoVehiculo.VENDIDO;
    }


    public abstract double calcularPrecioFinal();


    public int getAnio() {
        return Anio;
    }

    public void setAnio(int anio) {
        Anio = anio;
    }

    public Double getAutonomiaKm() {
        return AutonomiaKm;
    }

    public void setAutonomiaKm(Double autonomiaKm) {
        AutonomiaKm = autonomiaKm;
    }

    public Double getCapacidadBateria() {
        return CapacidadBateria;
    }

    public void setCapacidadBateria(Double capacidadBateria) {
        CapacidadBateria = capacidadBateria;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

    public EstadoVehiculo getEstado() {
        return Estado;
    }

    public void setEstado(EstadoVehiculo estado) {
        Estado = estado;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getMarca() {
        return Marca;
    }

    public void setMarca(String marca) {
        Marca = marca;
    }

    public String getModelo() {
        return Modelo;
    }

    public void setModelo(String modelo) {
        Modelo = modelo;
    }

    public int getPotenciaMotorKW() {
        return PotenciaMotorKW;
    }

    public void setPotenciaMotorKW(int potenciaMotorKW) {
        PotenciaMotorKW = potenciaMotorKW;
    }

    public Double getPrecioBase() {
        return PrecioBase;
    }

    public void setPrecioBase(Double precioBase) {
        PrecioBase = precioBase;
    }

    public int getVelocidadMaxima() {
        return VelocidadMaxima;
    }

    public void setVelocidadMaxima(int velocidadMaxima) {
        VelocidadMaxima = velocidadMaxima;
    }


    @Override
    public String toString() {
        return Marca + " " + Modelo + " (" + Anio + ") | " +
                "Color: " + Color + " | " +
                "Precio: $" + PrecioBase + " | " +
                "Autonomía: " + AutonomiaKm + " km | " +
                "Estado: " + Estado;
    }
}