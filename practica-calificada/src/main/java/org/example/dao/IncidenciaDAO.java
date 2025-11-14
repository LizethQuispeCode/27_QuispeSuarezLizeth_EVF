package dao;

import db.DBConnection;
import model.Incidencia;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IncidenciaDAO {

    public void crear(Incidencia i) throws SQLException {
        String sql = "INSERT INTO incidencia (tipo, aula, fecha, estado, descripcion) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, i.getTipo());
            ps.setString(2, i.getAula());
            ps.setDate(3, Date.valueOf(i.getFecha()));
            ps.setString(4, i.getEstado());
            ps.setString(5, i.getDescripcion());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) i.setId(rs.getInt(1));
            }
        }
    }

    public void actualizar(Incidencia i) throws SQLException {
        String sql = "UPDATE incidencia SET tipo=?, aula=?, fecha=?, estado=?, descripcion=? WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, i.getTipo());
            ps.setString(2, i.getAula());
            ps.setDate(3, Date.valueOf(i.getFecha()));
            ps.setString(4, i.getEstado());
            ps.setString(5, i.getDescripcion());
            ps.setInt(6, i.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM incidencia WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Incidencia> listarTodos() throws SQLException {
        List<Incidencia> lista = new ArrayList<>();
        String sql = "SELECT * FROM incidencia ORDER BY fecha DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(map(rs));
            }
        }
        return lista;
    }

    public List<Incidencia> buscar(String tipo, String aula, String estado) throws SQLException {
        List<Incidencia> lista = new ArrayList<>();
        String sql = "SELECT * FROM incidencia WHERE 1=1";
        if (tipo != null && !tipo.trim().isEmpty()) sql += " AND tipo LIKE ?";
        if (aula != null && !aula.trim().isEmpty()) sql += " AND aula LIKE ?";
        if (estado != null && !estado.trim().isEmpty()) sql += " AND estado LIKE ?";
        sql += " ORDER BY fecha DESC";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            int idx = 1;
            if (tipo != null && !tipo.trim().isEmpty()) ps.setString(idx++, "%" + tipo + "%");
            if (aula != null && !aula.trim().isEmpty()) ps.setString(idx++, "%" + aula + "%");
            if (estado != null && !estado.trim().isEmpty()) ps.setString(idx++, "%" + estado + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(map(rs));
            }
        }
        return lista;
    }

    public List<Incidencia> listarPorEstado(String estadoFilter) throws SQLException {
        List<Incidencia> lista = new ArrayList<>();
        String sql = "SELECT * FROM incidencia WHERE estado = ? ORDER BY fecha DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, estadoFilter);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(map(rs));
            }
        }
        return lista;
    }

    private Incidencia map(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String tipo = rs.getString("tipo");
        String aula = rs.getString("aula");
        LocalDate fecha = rs.getDate("fecha").toLocalDate();
        String estado = rs.getString("estado");
        String descripcion = rs.getString("descripcion");
        return new Incidencia(id, tipo, aula, fecha, estado, descripcion);
    }
}
