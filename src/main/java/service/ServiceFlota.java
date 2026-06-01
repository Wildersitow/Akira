package service;

import dao.*;
import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class  ServiceFlota {

    private final AutoElectricoDAO      autoDAO = new AutoElectricoDAO();
    private final MotoElectricaDAO      motoDAO = new MotoElectricaDAO();
    private final BicicletaElectricaDAO biciDAO = new BicicletaElectricaDAO();
    private final PatinetaElectricaDAO  patiDAO = new PatinetaElectricaDAO();

    public List<VehiculoElectrico> obtenerTodos() throws ServiceException {
        List<VehiculoElectrico> lista = new ArrayList<>();
        lista.addAll(autoDAO.obtenerTodos());
        lista.addAll(motoDAO.obtenerTodos());
        lista.addAll(biciDAO.obtenerTodos());
        lista.addAll(patiDAO.obtenerTodos());
        return lista;
    }

    public void eliminar(VehiculoElectrico v) throws ServiceException {
        if (v.getEstado() == EstadoVehiculo.VENDIDO)
            throw new ServiceException("NO_ELIMINABLE", "No se puede eliminar un vehículo vendido.");
        long id = Long.parseLong(v.getId());
        if (v instanceof AutoElectrico)      autoDAO.eliminar(id);
        else if (v instanceof MotoElectrica) motoDAO.eliminar(id);
        else if (v instanceof BicicletaElectrica) biciDAO.eliminar(id);
        else if (v instanceof PatinetaElectrica)  patiDAO.eliminar(id);
    }

    public void actualizarCampos(VehiculoElectrico v, Map<String, Object> campos) throws ServiceException {
        String tabla = obtenerTabla(v);
        StringBuilder sql = new StringBuilder("UPDATE " + tabla + " SET ");
        List<Object> valores = new ArrayList<>();
        campos.forEach((col, val) -> {
            if (!valores.isEmpty()) sql.append(", ");
            sql.append(col).append(" = ?");
            valores.add(val);
        });
        sql.append(" WHERE id = ?");
        valores.add(Long.parseLong(v.getId()));

        try (Connection con = dao.ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            con.setAutoCommit(false);
            for (int i = 0; i < valores.size(); i++) {
                Object val = valores.get(i);
                if (val instanceof String)  ps.setString(i + 1, (String) val);
                else if (val instanceof Double)  ps.setDouble(i + 1, (Double) val);
                else if (val instanceof Integer) ps.setInt(i + 1, (Integer) val);
                else if (val instanceof Long)    ps.setLong(i + 1, (Long) val);
                else ps.setObject(i + 1, val);
            }
            int filas = ps.executeUpdate();
            con.commit();
            if (filas == 0)
                throw new ServiceException("NO_ENCONTRADO", "No se encontró el vehículo con id: " + v.getId());
        } catch (SQLException e) {
            throw new ServiceException("ERROR_ACTUALIZACION", "Error al actualizar: " + e.getMessage(), e);
        }
    }

    public int estadoToId(EstadoVehiculo estado) {
        return switch (estado) {
            case DISPONIBLE    -> 1;
            case VENDIDO       -> 2;
            case ALQUILADO     -> 3;
            case MANTENIMIENTO -> 4;
        };
    }

    private String obtenerTabla(VehiculoElectrico v) throws ServiceException {
        if (v instanceof AutoElectrico)      return "auto_electrico";
        if (v instanceof MotoElectrica)      return "moto_electrica";
        if (v instanceof BicicletaElectrica) return "bicicleta_electrica";
        if (v instanceof PatinetaElectrica)  return "patineta_electrica";
        throw new ServiceException("TIPO_INVALIDO", "Tipo de vehículo no reconocido.");
    }
}