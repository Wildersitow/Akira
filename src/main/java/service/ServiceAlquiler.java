package service;

import model.VehiculoElectrico;
import model.AutoElectrico;
import model.MotoElectrica;
import model.BicicletaElectrica;
import model.PatinetaElectrica;

public class ServiceAlquiler {

    public double calcularTotal(VehiculoElectrico v, String periodo) {
        return switch (periodo) {
            case "1 día"    -> precioDia(v);
            case "1 semana" -> precioSemana(v);
            case "1 mes"    -> precioMes(v);
            default         -> precioDia(v);
        };
    }

    public double calcularPrecioPorDia(VehiculoElectrico v) {
        return precioDia(v);
    }

    public int diasDePeriodo(String periodo) {
        return switch (periodo) {
            case "1 día"    ->  1;
            case "1 semana" ->  7;
            case "1 mes"    -> 30;
            default         ->  1;
        };
    }

    public void validarReserva(VehiculoElectrico v, String periodo) throws ServiceException {
        if (v == null)
            throw new ServiceException("VEHICULO_NULO", "No se seleccionó ningún vehículo.");
        if (!v.estaDisponible())
            throw new ServiceException("VEHICULO_NO_DISPONIBLE",
                    "El vehículo " + v.getMarca() + " " + v.getModelo() + " ya no está disponible.");
        if (periodo == null || periodo.isBlank())
            throw new ServiceException("PERIODO_VACIO", "Debes seleccionar un período de alquiler.");
        if (!periodo.equals("1 día") && !periodo.equals("1 semana") && !periodo.equals("1 mes"))
            throw new ServiceException("PERIODO_INVALIDO", "El período '" + periodo + "' no es válido.");
    }

    private double precioDia(VehiculoElectrico v) {
        if (v instanceof AutoElectrico)      return 150_000;
        if (v instanceof MotoElectrica)      return 80_000;
        if (v instanceof BicicletaElectrica) return 30_000;
        if (v instanceof PatinetaElectrica)  return 20_000;
        return 100_000;
    }

    private double precioSemana(VehiculoElectrico v) {
        if (v instanceof AutoElectrico)      return 900_000;
        if (v instanceof MotoElectrica)      return 500_000;
        if (v instanceof BicicletaElectrica) return 180_000;
        if (v instanceof PatinetaElectrica)  return 120_000;
        return 600_000;
    }

    private double precioMes(VehiculoElectrico v) {
        if (v instanceof AutoElectrico)      return 3_000_000;
        if (v instanceof MotoElectrica)      return 1_800_000;
        if (v instanceof BicicletaElectrica) return 600_000;
        if (v instanceof PatinetaElectrica)  return 400_000;
        return 2_000_000;
    }
}