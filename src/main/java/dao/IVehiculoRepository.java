package dao;

import model.VehiculoElectrico;
import java.util.List;

public interface IVehiculoRepository {
    void guardar(VehiculoElectrico vehiculo);

    void actualizar(VehiculoElectrico vehiculo);

    void eliminar(String id);

    VehiculoElectrico buscarPorId(String id);

    List<VehiculoElectrico> listarTodos();

    List<VehiculoElectrico> listarDisponibles();
}
