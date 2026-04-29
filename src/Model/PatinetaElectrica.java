package Model;

public class PatinetaElectrica extends VehiculoElectrico {

    private boolean esPlegable;
    private Double pesoDispositivoKg;
    private Double pesoMaximoUsuarioKg;

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
