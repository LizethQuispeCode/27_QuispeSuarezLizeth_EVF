package model;

import java.time.LocalDate;
import java.util.Objects;

public class Incidencia {
    private int id;
    private String tipo;
    private String aula;
    private LocalDate fecha;
    private String estado;
    private String descripcion;

    // Constructor para nuevo (sin id)
    public Incidencia(String tipo, String aula, LocalDate fecha, String estado, String descripcion) {
        this.tipo = tipo;
        this.aula = aula;
        this.fecha = fecha;
        this.estado = estado;
        this.descripcion = descripcion;
    }

    // Constructor completo
    public Incidencia(int id, String tipo, String aula, LocalDate fecha, String estado, String descripcion) {
        this(tipo, aula, fecha, estado, descripcion);
        this.id = id;
    }

    // getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getAula() { return aula; }
    public void setAula(String aula) { this.aula = aula; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    // validación simple
    public boolean isValid() {
        return tipo != null && !tipo.trim().isEmpty()
                && aula != null && !aula.trim().isEmpty()
                && fecha != null
                && estado != null && !estado.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "Incidencia{" + "id=" + id + ", tipo='" + tipo + '\'' + ", aula='" + aula + '\'' +
                ", fecha=" + fecha + ", estado='" + estado + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Incidencia)) return false;
        Incidencia that = (Incidencia) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
