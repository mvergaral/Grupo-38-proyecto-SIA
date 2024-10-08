package com.grupo38.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import com.grupo38.exceptions.EquipoNoArrendadoException;
import com.grupo38.exceptions.EquipoNoDisponibleException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Clase abstracta que representa un equipo que puede ser arrendado.
 * Implementa la lógica común de negocio para los equipos, como el arrendar,
 * devolver, y gestionar el estado del equipo.
 * Las subclases deben implementar los métodos abstractos para calcular la tarifa y generar una descripción.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_equipo", discriminatorType = DiscriminatorType.STRING)
@Table(name = "equipo")
public abstract class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_sucursal", nullable = false)
    private Sucursal sucursal;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "prestado", nullable = false)
    private boolean prestado = false;

    @OneToMany(mappedBy = "equipo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Arriendo> arriendos = new ArrayList<>();

    /**
     * Constructor vacío requerido por Hibernate.
     */
    public Equipo() {}

    /**
     * Constructor con parámetros.
     *
     * @param sucursal La sucursal a la que pertenece el equipo.
     * @param nombre El nombre del equipo.
     */
    public Equipo(Sucursal sucursal, String nombre) {
        this.sucursal = sucursal;
        this.nombre = nombre;
    }

    /**
     * Método abstracto para calcular la tarifa del equipo.
     * Debe ser implementado por las subclases.
     *
     * @return La tarifa del equipo.
     */
    public abstract double calcularTarifa();

    /**
     * Método abstracto para generar una descripción del equipo.
     * Debe ser implementado por las subclases.
     *
     * @return Una descripción del equipo.
     */
    public abstract String generarDescripcion();

    /**
     * Arrenda el equipo al cliente especificado.
     *
     * @param rut El RUT del cliente que arrendará el equipo.
     * @throws EquipoNoDisponibleException Si el equipo ya está arrendado.
     */
    public void arrendar(String rut) throws EquipoNoDisponibleException {
        if (this.prestado) {
            throw new EquipoNoDisponibleException();
        }
        Arriendo nuevoArriendo = new Arriendo(rut, this, java.time.LocalDate.now());
        this.arriendos.add(nuevoArriendo);
        this.prestado = true;
    }

    /**
     * Devuelve el equipo, finalizando el arriendo más reciente.
     *
     * @param session La sesión de Hibernate para realizar la persistencia.
     * @throws EquipoNoArrendadoException Si el equipo no ha sido arrendado.
     */
    public void devolver(Session session) throws EquipoNoArrendadoException {
        if (!this.prestado) {
            throw new EquipoNoArrendadoException();
        }

        Arriendo ultimoArriendo = this.arriendos.get(this.arriendos.size() - 1);
        if (ultimoArriendo.isActive()) {
            ultimoArriendo.finalizarArriendo();
            this.prestado = false;
            session.merge(ultimoArriendo);
        } else {
            throw new EquipoNoArrendadoException();
        }
    }

    /**
     * Añade un nuevo arriendo a la lista de arriendos del equipo.
     *
     * @param arriendo El arriendo a añadir.
     */
    public void añadirArriendo(Arriendo arriendo) {
        arriendo.setEquipo(this);
        this.arriendos.add(arriendo);
    }

    /**
     * Elimina un arriendo de la lista de arriendos del equipo.
     *
     * @param arriendo El arriendo a eliminar.
     */
    public void eliminarArriendo(Arriendo arriendo) {
        this.arriendos.remove(arriendo);
        arriendo.setEquipo(null);
    }

    /**
     * Obtiene el último arriendo del equipo.
     *
     * @return El último arriendo del equipo.
     * @throws EquipoNoArrendadoException Si no existen arriendos para el equipo.
     */
    public Arriendo ultimoArriendo() throws EquipoNoArrendadoException {
        if (arriendos.isEmpty()) {
            throw new EquipoNoArrendadoException();
        }
        return arriendos.get(arriendos.size() - 1);
    }

    /**
     * Persiste el equipo en la base de datos.
     *
     * @param session La sesión de Hibernate para realizar la persistencia.
     */
    public void persistirEquipo(Session session) {
        session.persist(this);
    }

    /**
     * Actualiza el equipo en la base de datos.
     *
     * @param session La sesión de Hibernate para realizar la actualización.
     */
    public void actualizarEquipo(Session session) {
        session.merge(this);
    }

    /**
     * Elimina el equipo de la base de datos.
     *
     * @param session La sesión de Hibernate para realizar la eliminación.
     */
    public void eliminarEquipo(Session session) {
        session.remove(this);
    }

    // Getters y Setters

    /**
     * Obtiene el ID del equipo.
     *
     * @return El ID del equipo.
     */
    public int getId() {
        return id;
    }

    /**
     * Obtiene la sucursal a la que pertenece el equipo.
     *
     * @return La sucursal del equipo.
     */
    public Sucursal getSucursal() {
        return sucursal;
    }

    /**
     * Establece la sucursal del equipo.
     *
     * @param sucursal La nueva sucursal del equipo.
     */
    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    /**
     * Obtiene el nombre del equipo.
     *
     * @return El nombre del equipo.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del equipo.
     *
     * @param nombre El nuevo nombre del equipo.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Verifica si el equipo está arrendado.
     *
     * @return true si el equipo está arrendado, false en caso contrario.
     */
    public boolean isPrestado() {
        return prestado;
    }

    /**
     * Establece el estado de arriendo del equipo.
     *
     * @param prestado true si el equipo está arrendado, false en caso contrario.
     */
    public void setPrestado(boolean prestado) {
        this.prestado = prestado;
    }

    /**
     * Obtiene una lista inmutable de los arriendos del equipo.
     *
     * @return La lista de arriendos del equipo.
     */
    public List<Arriendo> getArriendos() {
        return List.copyOf(arriendos);
    }
}