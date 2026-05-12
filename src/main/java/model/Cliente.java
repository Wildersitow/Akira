package model;

import java.io.Serializable;
import java.util.List;

public class Cliente extends Persona implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String licenciaConducir;
    private final double historialCredito;
    private final int puntosFidelidad;
    private final List<Contrato> contratos;

    public Cliente(String nombre, String nombreUsuario, String contraseña, String documentoid, String email, String rol, int telefono, String licenciaConducir, double historialCredito, int puntosFidelidad, List<Contrato> contratos) {
        super(nombre, nombreUsuario, contraseña, documentoid, email, rol, telefono);
        this.licenciaConducir = licenciaConducir;
        this.historialCredito = historialCredito;
        this.puntosFidelidad = puntosFidelidad;
        this.contratos = contratos;
    }
}
