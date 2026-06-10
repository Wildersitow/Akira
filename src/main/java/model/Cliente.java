package model;

import java.io.Serializable;
import java.util.List;

public class Cliente extends Persona implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String licenciaConducir;
    private final double historialCredito;
    private final int puntosFidelidad;
    private final List<Contrato> contratos;
    private String direccion;

    public Cliente(String nombre, String nombreUsuario, String contraseña, String documentoid, String email, String rol, int telefono, String licenciaConducir, double historialCredito, int puntosFidelidad, List<Contrato> contratos) {
        super(nombre, nombreUsuario, contraseña, documentoid, email, rol, telefono);
        this.licenciaConducir = licenciaConducir;
        this.historialCredito = historialCredito;
        this.puntosFidelidad = puntosFidelidad;
        this.contratos = contratos;
    }

    public List<Contrato> getContratos() {
        return contratos;
    }

    public double getHistorialCredito() {
        return historialCredito;
    }

    public String getLicenciaConducir() {
        return licenciaConducir;
    }

    public int getPuntosFidelidad() {
        return puntosFidelidad;
    }

    public String getDireccion() { return direccion; }

    public void getDireccion(String direccion) { this.direccion = direccion; }

    public double getTotalGastado() {
        if (contratos == null || contratos.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (Contrato contrato : contratos) {
            total += contrato.getPrecioFinal();
        }
        return total;
    }

    public boolean tieneContratos() {
        return contratos != null && !contratos.isEmpty();
    }

    @Override
    public String toString() {
        return "Cliente | " + super.toString() +
                " | Licencia: " + licenciaConducir +
                " | Crédito: " + historialCredito +
                " | Puntos: " + puntosFidelidad +
                " | Contratos: " + (contratos != null ? contratos.size() : 0);
    }
}
