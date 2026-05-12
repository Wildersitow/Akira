package model;

import java.util.Date;

public class Contrato {
    private final String producto;
    private final Date fechaContrato;
    private final String persona;
    private final String supervisor;
    private final String precio;

    public Contrato(String producto, Date fechaContrato, String persona, String supervisor, String precio) {
        this.producto = producto;
        this.fechaContrato = fechaContrato;
        this.persona = persona;
        this.supervisor = supervisor;
        this.precio = precio;
    }
}
