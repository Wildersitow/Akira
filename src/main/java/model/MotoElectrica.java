package model;

public class MotoElectrica extends VehiculoElectrico {

    private int numeroPasajeros;
    private String tipoMoto;
    private String tipoCarga;

    public MotoElectrica(String id, String marca, String modelo,
                         Double autonomiaKm, Double capacidadBateria,
                         Double precioBase, int velocidadMaxima,
                         int numeroPasajeros, String tipoMoto, String tipoCarga) {

        super(id, marca, modelo, autonomiaKm, capacidadBateria, precioBase, velocidadMaxima);

        this.numeroPasajeros = numeroPasajeros;
        this.tipoMoto = tipoMoto;
        this.tipoCarga = tipoCarga;
    }

}
