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

    public void actualizarVehiculo(VehiculoElectrico vehiculo) throws ServiceException {
        if (vehiculo == null || vehiculo.getMarca() == null || vehiculo.getId() == null){
            throw new ServiceException("VEH_004", "El vehiculo o su id no pueden ser nulos");
        }

        buscarPorId(vehiculo.getId());
        vehiculoRepository.actualizar(vehiculo);
    }

    public void eliminarVehiculo(String id) throws ServiceException {
        VehiculoElectrico vehiculo = vehiculoRepository.buscarPorId(id);

        if (!vehiculo.estaDisponible()) {
            throw new ServiceException("VEH-005", "No se puede eliminar un vehiculo ya vendido");
        }

        vehiculoRepository.eliminar(id);
    }

    public VehiculoElectrico buscarPorId(String id) throws ServiceException {
        if (id == null || id.isBlank()) {
            throw new ServiceException("VEH-006", "El ID del vehículo no puede ser nulo o vacío");
        }
        VehiculoElectrico vehiculo = vehiculoRepository.buscarPorId(id);
        if (vehiculo == null) {
            throw new ServiceException("VEH-007", "No se encontró un vehículo con el ID: " + id);
        }
        return vehiculo;
    }

}
