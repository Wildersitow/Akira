package service;

import model.VehiculoElectrico;
import java.util.List;

public class ServiceCompras {

    public void validarCompra(VehiculoElectrico v) throws ServiceException {
        if (!v.estaDisponible())
            throw new ServiceException("NO_DISPONIBLE", "Este vehículo no está disponible para compra.");
    }

    public void validarCarrito(List<VehiculoElectrico> carrito) throws ServiceException {
        if (carrito == null || carrito.isEmpty())
            throw new ServiceException("CARRITO_VACIO", "El carrito está vacío.");
        for (VehiculoElectrico v : carrito)
            validarCompra(v);
    }

    public double calcularTotalCarrito(List<VehiculoElectrico> carrito) {
        return carrito.stream().mapToDouble(VehiculoElectrico::calcularPrecioFinal).sum();
    }

    public double extraerPrecioMax(String opcion) {
        return switch (opcion) {
            case "Hasta $10.000.000"  ->  10_000_000;
            case "Hasta $30.000.000"  ->  30_000_000;
            case "Hasta $60.000.000"  ->  60_000_000;
            case "Hasta $100.000.000" -> 100_000_000;
            default -> Double.MAX_VALUE;
        };
    }

    public double extraerAutonomiaMin(String opcion) {
        return switch (opcion) {
            case "Más de 50 km"  ->  50;
            case "Más de 100 km" -> 100;
            case "Más de 200 km" -> 200;
            case "Más de 400 km" -> 400;
            default -> 0;
        };
    }
}