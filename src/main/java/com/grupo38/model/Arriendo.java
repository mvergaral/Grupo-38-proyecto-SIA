package com.grupo38.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Clase que representa un arriendo de un equipo.
 * Gestiona el estado del arriendo, las fechas de inicio y fin, y el costo total.
 */
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

    /**
     * Constructor vacío requerido por Hibernate.
     */
    public Arriendo() {}

    /**
     * Constructor con parámetros.
     *
     * @param rutCliente  El RUT del cliente que arrienda el equipo.
     * @param equipo      El equipo que está siendo arrendado.
     * @param fechaInicio La fecha de inicio del arriendo.
     */
    public Arriendo(String rutCliente, Equipo equipo, LocalDate fechaInicio) {
        this.rutCliente = rutCliente;
        this.equipo = equipo;
        this.fechaInicio = fechaInicio;
    }

    /**
     * Calcula el costo total del arriendo en función del número de días
     * y la tarifa del equipo arrendado.
     *
     * @return El costo total del arriendo.
     */
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

    /**
     * Finaliza el arriendo, registrando la fecha de devolución y calculando el costo total.
     */
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

    /**
     * Obtiene el ID del arriendo.
     *
     * @return El ID del arriendo.
     */
    public int getId() {
        return id;
    }

    /**
     * Establece el ID del arriendo.
     *
     * @param id El nuevo ID del arriendo.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtiene el equipo arrendado.
     *
     * @return El equipo arrendado.
     */
    public Equipo getEquipo() {
        return equipo;
    }

    /**
     * Establece el equipo arrendado.
     *
     * @param equipo El equipo que está siendo arrendado.
     */
    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    /**
     * Obtiene el RUT del cliente que arrienda el equipo.
     *
     * @return El RUT del cliente.
     */
    public String getRutCliente() {
        return rutCliente;
    }

    /**
     * Establece el RUT del cliente que arrienda el equipo.
     *
     * @param rutCliente El RUT del cliente.
     */
    public void setRutCliente(String rutCliente) {
        this.rutCliente = rutCliente;
    }

    /**
     * Obtiene la fecha de inicio del arriendo.
     *
     * @return La fecha de inicio del arriendo.
     */
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    /**
     * Establece la fecha de inicio del arriendo.
     *
     * @param fechaInicio La nueva fecha de inicio del arriendo.
     */
    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /**
     * Obtiene la fecha de finalización del arriendo.
     *
     * @return La fecha de finalización del arriendo, o null si el arriendo aún no ha finalizado.
     */
    public LocalDate getFechaFin() {
        return fechaFin;
    }

    /**
     * Establece la fecha de finalización del arriendo.
     *
     * @param fechaFin La nueva fecha de finalización del arriendo.
     */
    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    /**
     * Verifica si el arriendo está activo.
     *
     * @return true si el arriendo está activo, false si ha finalizado.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Establece si el arriendo está activo.
     *
     * @param active El nuevo estado del arriendo (activo o no).
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Obtiene el costo total del arriendo.
     *
     * @return El costo total del arriendo.
     */
    public double getCostoTotal() {
        return costoTotal;
    }

    /**
     * Establece el costo total del arriendo.
     *
     * @param costoTotal El nuevo costo total del arriendo.
     */
    public void setCostoTotal(double costoTotal) {
        this.costoTotal = costoTotal;
    }
}