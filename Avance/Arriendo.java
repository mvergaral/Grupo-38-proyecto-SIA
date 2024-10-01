import java.time.*;

public class Arriendo {
    private static int contadorId = 0;
    private int id, idEquipo;
    private LocalDate fechaInicio, fechaFin;
    private String rutCliente;
    private boolean active = true;

    public Arriendo(String rutCliente, int idEquipo, LocalDate fechaInicio, LocalDate fechaFin) {
        this.id = ++contadorId;
        this.rutCliente = rutCliente;
        this.idEquipo = idEquipo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRutCliente() {
        return rutCliente;
    }

    public void setRutCliente(String rutCliente) {
        this.rutCliente = rutCliente;
    }

    public int getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
