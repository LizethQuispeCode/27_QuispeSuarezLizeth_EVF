package controller;

import dao.IncidenciaDAO;
import model.Incidencia;

import java.sql.SQLException;
import java.util.List;

public class IncidenciaController {
    private IncidenciaDAO dao = new IncidenciaDAO();

    public void crearIncidencia(Incidencia i) throws SQLException {
        if (!i.isValid()) throw new IllegalArgumentException("Campos obligatorios faltantes");
        dao.crear(i);
    }

    public void actualizarIncidencia(Incidencia i) throws SQLException {
        if (i.getId() <= 0) throw new IllegalArgumentException("ID inválido");
        if (!i.isValid()) throw new IllegalArgumentException("Campos obligatorios faltantes");
        dao.actualizar(i);
    }

    public void eliminarIncidencia(int id) throws SQLException {
        dao.eliminar(id);
    }

    public List<Incidencia> listarTodas() throws SQLException {
        return dao.listarTodos();
    }

    public List<Incidencia> buscar(String tipo, String aula, String estado) throws SQLException {
        return dao.buscar(tipo, aula, estado);
    }

    public List<Incidencia> listarPorEstado(String estado) throws SQLException {
        return dao.listarPorEstado(estado);
    }
}
