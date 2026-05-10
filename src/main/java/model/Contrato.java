package model;

import java.util.Date;

public class Contrato {
    private String producto;
    private Date fechaContrato;
    private String persona;
    private String supervisor;
    private String precio;

    public Contrato(String producto, Date fechaContrato, String persona, String supervisor, String precio) {
        this.producto = producto;
        this.fechaContrato = fechaContrato;
        this.persona = persona;
        this.supervisor = supervisor;
        this.precio = precio;
    }
}
