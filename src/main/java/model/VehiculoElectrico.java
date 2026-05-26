package model;

public abstract class VehiculoElectrico {

    protected String Id;
    protected String Marca;
    protected String Modelo;
    protected Double AutonomiaKm;
    protected Double CapacidadBateria;
    protected Double PrecioBase;
    protected int VelocidadMaxima;


    public String getId() { return Id; }
    public String getMarca() { return Marca; }
    public String getModelo() { return Modelo; }
    public Double getAutonomiaKm() { return AutonomiaKm; }
    public Double getCapacidadBateria() { return CapacidadBateria; }
    public Double getPrecioBase() { return PrecioBase; }
    public int getVelocidadMaxima() { return VelocidadMaxima; }

    public VehiculoElectrico(String id, String marca, String modelo,
                             Double autonomiaKm, Double CapacidadBateria,
                             Double precioBase, int velocidadMaxima)
    {
        this.Id = id;
        this.Marca = marca;
        this.Modelo = modelo;
        this.AutonomiaKm = autonomiaKm;
        this.CapacidadBateria = CapacidadBateria;
        this.PrecioBase = precioBase;
        this.VelocidadMaxima = velocidadMaxima;

    }
}