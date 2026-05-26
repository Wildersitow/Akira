package model;

public class PatinetaElectrica extends VehiculoElectrico {

    private final boolean esPlegable;
    private final Double pesoDispositivoKg;
    private final Double pesoMaximoUsuarioKg;

    public boolean isEsPlegable() { return esPlegable; }
    public Double getPesoDispositivoKg() { return pesoDispositivoKg; }
    public Double getPesoMaximoUsuarioKg() { return pesoMaximoUsuarioKg; }


    public PatinetaElectrica(String id, String marca, String modelo,
                             Double autonomiaKm, Double capacidadBateria,
                             Double precioBase, int velocidadMaxima,
                             boolean esPlegable, Double pesoDispositivoKg,
                             Double pesoMaximoUsuarioKg) {

        super(id, marca, modelo, autonomiaKm, capacidadBateria, precioBase, velocidadMaxima);

        this.esPlegable = esPlegable;
        this.pesoDispositivoKg = pesoDispositivoKg;
        this.pesoMaximoUsuarioKg = pesoMaximoUsuarioKg;
    }

}
