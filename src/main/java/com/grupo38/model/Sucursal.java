package com.grupo38.model;

import jakarta.persistence.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.grupo38.exceptions.EquipoNoDisponibleException;
import com.grupo38.exceptions.EquipoNoArrendadoException;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sucursal")
public class Sucursal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "region", nullable = false)
    private String region;

    @Column(name = "comuna", nullable = false)
    private String comuna;

    @Column(name = "direccion", nullable = false)
    private String direccion;

    // Relación OneToMany con la entidad Equipo
    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Equipo> equipos = new ArrayList<>();

    // Propiedades observables de JavaFX (transient para que no se persistan)
    private transient SimpleIntegerProperty idProperty = new SimpleIntegerProperty();
    private transient SimpleStringProperty regionProperty = new SimpleStringProperty();
    private transient SimpleStringProperty comunaProperty = new SimpleStringProperty();
    private transient SimpleStringProperty direccionProperty = new SimpleStringProperty();

    // Constructor vacío requerido por Hibernate
    public Sucursal() {}

    // Constructor con parámetros
    public Sucursal(String region, String comuna, String direccion) {
        this.region = region;
        this.comuna = comuna;
        this.direccion = direccion;
        actualizarPropiedades(); // Actualizar las propiedades observables
    }

    // Método para actualizar las propiedades observables de JavaFX
    private void actualizarPropiedades() {
        this.idProperty.set(this.id);
        this.regionProperty.set(this.region);
        this.comunaProperty.set(this.comuna);
        this.direccionProperty.set(this.direccion);
    }

    // Sobrecarga de métodos: Obtener todos los equipos disponibles (sin parámetros)
    public List<Equipo> equiposDeSucursal(Session session) {
        String hql = "FROM Equipo e WHERE e.sucursal.id = :sucursalId";
        Query<Equipo> query = session.createQuery(hql, Equipo.class);
        query.setParameter("sucursalId", this.id);
        return query.list();
    }

    // Sobrecarga de métodos: Obtener equipos disponibles por tipo (con parámetro tipoEquipo)
    public List<Equipo> equiposDeSucursal(Session session, Class<? extends Equipo> tipoEquipo) {
        String hql = "FROM Equipo e WHERE e.sucursal.id = :sucursalId AND e.prestado = false AND e.class = :tipo";
        Query<Equipo> query = session.createQuery(hql, Equipo.class);
        query.setParameter("sucursalId", this.id);
        query.setParameter("tipo", tipoEquipo);
        return query.list();
    }

    // Método para añadir un equipo a la sucursal
    public void añadirEquipo(Session session, Equipo equipo) {
        equipo.setSucursal(this);
        session.persist(equipo); // Guardar el equipo en la base de datos
    }

    // Sobrecarga de métodos: Método para eliminar un equipo por su ID
    public void eliminarEquipo(Session session, int idEquipo) {
        Equipo equipo = session.find(Equipo.class, idEquipo); // Usar `find()` para obtener la entidad
        if (equipo != null && equipo.getSucursal().getId() == this.id) {
            equipos.remove(equipo);
            equipo.setSucursal(null); // Desvincular la relación con la sucursal
            session.remove(equipo); // Eliminar el equipo de la base de datos
        }
    }

    // Método para devolver un equipo (marcarlo como devuelto)
    public void devolverEquipo(Session session, int idEquipo) throws EquipoNoArrendadoException {
        Equipo equipo = session.find(Equipo.class, idEquipo);
        if (equipo != null && equipo.getSucursal().getId() == this.id) {
            try {
                equipo.devolver(session);
                session.merge(equipo);
                return;
            } catch (EquipoNoArrendadoException e) {
                throw e;
            }
        }
    }

    // Método para arrendar un equipo (cambiar su estado a "prestado")
    public void arrendarEquipo(Session session, int idEquipo, String rutCliente) throws EquipoNoDisponibleException {
        try {
            Equipo equipo = session.find(Equipo.class, idEquipo);
            if (equipo != null && equipo.getSucursal().getId() == this.id) {
                equipo.arrendar(rutCliente); // Puede lanzar EquipoNoDisponibleException
                session.merge(equipo); // Actualizar el equipo en la base de datos
            }
        } catch (EquipoNoDisponibleException e) {
            throw e;
        }
    }

    public String generarNombreLegible() {
        return direccion + ", " + comuna;
    }

    // Métodos para gestionar la persistencia de Sucursal en Hibernate

    public void persistirSucursal(Session session) {
        session.persist(this);
    }

    public void actualizarSucursal(Session session) {
        session.merge(this);
    }

    public void eliminarSucursal(Session session) {
        session.remove(this);
    }

    // Getters para las propiedades observables
    public SimpleIntegerProperty idProperty() {
        return new SimpleIntegerProperty(this.id);
    }

    public SimpleStringProperty regionProperty() {
        return regionProperty;
    }

    public SimpleStringProperty comunaProperty() {
        return comunaProperty;
    }

    public SimpleStringProperty direccionProperty() {
        return direccionProperty;
    }

    // Getters y Setters restantes

    public List<Equipo> getEquipos() {
        return List.copyOf(equipos); // Devuelve una copia inmutable de la lista
    }

    public int getId() {
        return id;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getComuna() {
        return comuna;
    }

    public void setComuna(String comuna) {
        this.comuna = comuna;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @PostLoad
    private void onPostLoad() {
        actualizarPropiedades();
    }
}