package model;

public class BicicletaElectrica extends VehiculoElectrico {

    private int numeroMarchas;
    private boolean tieneAsistenciaPedal;
    private String tipoBicicleta;

    public BicicletaElectrica(String id, String marca, String modelo,
                              Double autonomiaKm, Double capacidadBateria,
                              Double precioBase, int velocidadMaxima,
                              int numeroMarchas, boolean tieneAsistenciaPedal,
                              String tipoBicicleta) {

        super(id, marca, modelo, autonomiaKm, capacidadBateria, precioBase, velocidadMaxima);

        this.numeroMarchas = numeroMarchas;
        this.tieneAsistenciaPedal = tieneAsistenciaPedal;
        this.tipoBicicleta = tipoBicicleta;
    }

}
