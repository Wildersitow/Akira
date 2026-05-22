package service;

import model.VehiculoElectrico;
import dao.IVehiculoRepository;
import java.util.List;

public class VehiculoService {

    private final IVehiculoRepository vehiculoRepository;

    public VehiculoService(IVehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    public void registrarVehiculo(VehiculoElectrico vehiculo) throws ServiceException {
        if (vehiculo == null){
            throw new ServiceException("VEH-001", "El vehiculo no puede ser nulo");
        }

        if (vehiculo.getMarca() == null || vehiculo.getMarca().isBlank()){
            throw new ServiceException("VEH-002", "El vehiculo debe tener una marca");
        }

        if (vehiculo.getPrecioBase() <= 0){
            throw new ServiceException("VEH-003", "El precio del vehiculo debe ser mayor o igual a cero");
        }

        vehiculoRepository.guardar(vehiculo);
    }
}
