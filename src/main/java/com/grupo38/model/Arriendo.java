package com.grupo38.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "arriendo")
public class Arriendo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_equipo", nullable = false)
    private Equipo equipo;

    @Column(name = "rut_cliente", nullable = false, length = 10)
    private String rutCliente;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin; // Se seteará al devolver el equipo

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "costo_total")
    private double costoTotal;

    // Constructor vacío requerido por Hibernate
    public Arriendo() {}

    // Constructor con parámetros
    public Arriendo(String rutCliente, Equipo equipo, LocalDate fechaInicio) {
        this.rutCliente = rutCliente;
        this.equipo = equipo;
        this.fechaInicio = fechaInicio;
    }

    // Método para calcular el costo del arriendo
    public double calcularCostoTotal() {
        if (fechaFin == null) {
            fechaFin = LocalDate.now(); // Si aún no se ha devuelto el equipo, tomar la fecha actual
        }

        // Calcular la cantidad de días entre la fecha de inicio y la fecha de fin
        long dias = ChronoUnit.DAYS.between(fechaInicio, fechaFin);

        // Garantizar un mínimo de un día de arriendo
        dias = (dias == 0) ? 1 : dias;

        // Calcular el costo total en base a la tarifa del equipo
        this.costoTotal = dias * equipo.calcularTarifa();
        return this.costoTotal;
    }

    // Método para finalizar el arriendo (marcar fecha de fin)
    public void finalizarArriendo() {
        if (this.active) {
            this.fechaFin = LocalDate.now();  // Registrar la fecha de devolución
            this.calcularCostoTotal();  // Calcular el costo total del arriendo
            this.active = false;  // Marcar el arriendo como finalizado
        } else {
            System.out.println("El arriendo ya está finalizado.");
        }
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public String getRutCliente() {
        return rutCliente;
    }

    public void setRutCliente(String rutCliente) {
        this.rutCliente = rutCliente;
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

    public double getCostoTotal() {
        return costoTotal;
    }

    public void setCostoTotal(double costoTotal) {
        this.costoTotal = costoTotal;
    }
}