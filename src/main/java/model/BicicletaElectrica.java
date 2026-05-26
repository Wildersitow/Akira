package model;

public class BicicletaElectrica extends VehiculoElectrico {

    private final int numeroMarchas;
    private final boolean tieneAsistenciaPedal;
    private final String tipoBicicleta;

    public int getNumeroMarchas() { return numeroMarchas; }
    public boolean isTieneAsistenciaPedal() { return tieneAsistenciaPedal; }
    public String getTipoBicicleta() { return tipoBicicleta; }

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
