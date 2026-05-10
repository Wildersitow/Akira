package model;

public class Cliente extends Persona {

    private String licenciaConducir;
    private double historialCredito;
    private int puntosFidelidad;
    private List<Contrato> contratos;

    public Cliente(String nombre, String nombreUsuario, String contraseña, String documentoid, String email, int telefono, String licenciaConducir, double historialCredito, int puntosFidelidad, List<Contrato> contratos) {
        super(nombre, nombreUsuario, contraseña, documentoid, email, telefono);
        this.licenciaConducir = licenciaConducir;
        this.historialCredito = historialCredito;
        this.puntosFidelidad = puntosFidelidad;
        this.contratos = contratos;
    }
}
