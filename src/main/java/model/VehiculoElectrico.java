package model;

public abstract class VehiculoElectrico {

    protected String Id;
    protected String Marca;
    protected String Modelo;
    protected Double AutonomiaKm;
    protected Double CapacidadBateria;
    protected Double PrecioBase;
    protected int VelocidadMaxima;


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