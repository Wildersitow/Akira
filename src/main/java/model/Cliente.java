package model;

public class Cliente extends Persona {

    private String licenciaConducir;
    private double historialCredito;
    private int puntosFidelidad;
    private List<Contrato> contratos;

    public Cliente(String licenciaConducir, double historialCredito, int puntosFidelidad, List<Contrato> contratos) {
        this.licenciaConducir = licenciaConducir;
        this.historialCredito = historialCredito;
        this.puntosFidelidad = puntosFidelidad;
        this.contratos = contratos;
    }
}
