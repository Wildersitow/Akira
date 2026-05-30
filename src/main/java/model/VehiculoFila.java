package model;

import javafx.beans.property.SimpleStringProperty;

public class VehiculoFila {

    private final SimpleStringProperty tipo;
    private final SimpleStringProperty id;
    private final SimpleStringProperty marca;
    private final SimpleStringProperty modelo;
    private final SimpleStringProperty fecha;
    private final SimpleStringProperty autonomia;
    private final SimpleStringProperty bateria;
    private final SimpleStringProperty precio;
    private final SimpleStringProperty velocidad;

    public VehiculoFila(String tipo, String id, String marca, String modelo,
                        String fecha, String autonomia, String bateria,
                        String precio, String velocidad) {
        this.tipo = new SimpleStringProperty(tipo);
        this.id = new SimpleStringProperty(id);
        this.marca = new SimpleStringProperty(marca);
        this.modelo = new SimpleStringProperty(modelo);
        this.fecha = new SimpleStringProperty(fecha);
        this.autonomia = new SimpleStringProperty(autonomia);
        this.bateria = new SimpleStringProperty(bateria);
        this.precio = new SimpleStringProperty(precio);
        this.velocidad = new SimpleStringProperty(velocidad);
    }

    public String getTipo() { return tipo.get(); }
    public String getId() { return id.get(); }
    public String getMarca() { return marca.get(); }
    public String getModelo() { return modelo.get(); }
    public String getFecha() { return fecha.get(); }
    public String getAutonomia() { return autonomia.get(); }
    public String getBateria() { return bateria.get(); }
    public String getPrecio() { return precio.get(); }
    public String getVelocidad() { return velocidad.get(); }

    public SimpleStringProperty tipoProperty() { return tipo; }
    public SimpleStringProperty idProperty() { return id; }
    public SimpleStringProperty marcaProperty() { return marca; }
    public SimpleStringProperty modeloProperty() { return modelo; }
    public SimpleStringProperty fechaProperty() { return fecha; }
    public SimpleStringProperty autonomiaProperty() { return autonomia; }
    public SimpleStringProperty bateriaProperty() { return bateria; }
    public SimpleStringProperty precioProperty() { return precio; }
    public SimpleStringProperty velocidadProperty() { return velocidad; }
}