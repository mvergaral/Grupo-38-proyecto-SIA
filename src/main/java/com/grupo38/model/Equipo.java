package com.grupo38.model;

import com.grupo38.exceptions.EquipoNoDisponibleException;
import com.grupo38.exceptions.EquipoNoArrendadoException;
import jakarta.persistence.*;
import org.hibernate.Session;
import java.util.ArrayList;
import java.util.List;

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

    // Constructor vacío requerido por Hibernate
    public Equipo() {}

    // Constructor con parámetros
    public Equipo(Sucursal sucursal, String nombre) {
        this.sucursal = sucursal;
        this.nombre = nombre;
    }

    // Método abstracto que será implementado en las subclases
    public abstract double calcularTarifa();
    public abstract String generarDescripcion();

    // Métodos de lógica de negocio

    public void arrendar(String rut) throws EquipoNoDisponibleException {
        try {
            if (this.prestado) {
                throw new EquipoNoDisponibleException();
            }
            Arriendo nuevoArriendo = new Arriendo(rut, this, java.time.LocalDate.now());
            this.arriendos.add(nuevoArriendo);
            this.prestado = true;
        } catch (EquipoNoDisponibleException e) {
            throw e; // Re-lanzar la excepción si es necesario manejarla en otro nivel
        }
    }

    public void devolver(Session session) throws EquipoNoArrendadoException {
        try {
            if (!this.prestado) {
                throw new EquipoNoArrendadoException();
            }

            // Obtener el último arriendo (el más reciente)
            Arriendo ultimoArriendo = this.arriendos.get(this.arriendos.size() - 1);

            // Verificar si el arriendo es activo
            if (ultimoArriendo.isActive()) {
                ultimoArriendo.finalizarArriendo();  // Registrar la fecha de fin y calcular el costo
                this.prestado = false;  // Marcar el equipo como disponible
                session.merge(ultimoArriendo);  // Actualizar el arriendo en la base de datos
                return;
            } else {
                throw new EquipoNoArrendadoException();
            }

        } catch (EquipoNoArrendadoException e) {
            throw e;
        }
    }

    // Métodos para gestionar la colección de Arriendo

    // Añadir un arriendo a la lista de arriendos
    public void añadirArriendo(Arriendo arriendo) {
        arriendo.setEquipo(this);  // Asignar la relación bidireccional con el equipo
        this.arriendos.add(arriendo);
    }

    // Eliminar un arriendo de la lista de arriendos
    public void eliminarArriendo(Arriendo arriendo) {
        this.arriendos.remove(arriendo);
        arriendo.setEquipo(null);  // Desvincular el equipo del arriendo
    }

    // Método para obtener el último arriendo
    public Arriendo ultimoArriendo() throws EquipoNoArrendadoException {
        if (arriendos.isEmpty()) {
            throw new EquipoNoArrendadoException();
        }
        return arriendos.get(arriendos.size() - 1);  // El último arriendo en la lista
    }

    // Métodos para gestionar la persistencia de Sucursal en Hibernate

    public void persistirEquipo(Session session) {
        session.persist(this);
    }

    public void actualizarEquipo(Session session) {
        session.merge(this);
    }

    public void eliminarEquipo(Session session) {
        session.remove(this);
    }

    // Getters y Setters

    public int getId() {
        return id;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isPrestado() {
        return prestado;
    }

    public void setPrestado(boolean prestado) {
        this.prestado = prestado;
    }

    public List<Arriendo> getArriendos() {
        return List.copyOf(arriendos); // Devolver una lista inmutable
    }
}