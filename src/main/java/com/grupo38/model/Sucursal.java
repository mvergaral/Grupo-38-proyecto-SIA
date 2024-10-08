package com.grupo38.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.grupo38.exceptions.EquipoNoArrendadoException;
import com.grupo38.exceptions.EquipoNoDisponibleException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Clase que representa una sucursal dentro del sistema.
 * Las sucursales tienen una relación OneToMany con los equipos y gestionan
 * operaciones como añadir, eliminar, arrendar y devolver equipos.
 */
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

    /**
     * Constructor vacío requerido por Hibernate.
     */
    public Sucursal() {}

    /**
     * Constructor con parámetros.
     * Permite crear una sucursal con una región, comuna y dirección específicas.
     *
     * @param region La región donde se ubica la sucursal.
     * @param comuna La comuna donde se ubica la sucursal.
     * @param direccion La dirección de la sucursal.
     */
    public Sucursal(String region, String comuna, String direccion) {
        this.region = region;
        this.comuna = comuna;
        this.direccion = direccion;
        actualizarPropiedades(); // Actualizar las propiedades observables
    }

    /**
     * Actualiza las propiedades observables de JavaFX.
     */
    private void actualizarPropiedades() {
        this.idProperty.set(this.id);
        this.regionProperty.set(this.region);
        this.comunaProperty.set(this.comuna);
        this.direccionProperty.set(this.direccion);
    }

    /**
     * Obtiene todos los equipos de la sucursal.
     * 
     * @param session La sesión de Hibernate.
     * @return Una lista de todos los equipos de la sucursal.
     */
    public List<Equipo> equiposDeSucursal(Session session) {
        String hql = "FROM Equipo e WHERE e.sucursal.id = :sucursalId";
        Query<Equipo> query = session.createQuery(hql, Equipo.class);
        query.setParameter("sucursalId", this.id);
        return query.list();
    }

    /**
     * Obtiene los equipos disponibles de la sucursal por tipo.
     *
     * @param session La sesión de Hibernate.
     * @param tipoEquipo El tipo de equipo a buscar.
     * @return Una lista de equipos disponibles del tipo especificado.
     */
    public List<Equipo> equiposDeSucursal(Session session, Class<? extends Equipo> tipoEquipo) {
        String hql = "FROM Equipo e WHERE e.sucursal.id = :sucursalId AND e.prestado = false AND e.class = :tipo";
        Query<Equipo> query = session.createQuery(hql, Equipo.class);
        query.setParameter("sucursalId", this.id);
        query.setParameter("tipo", tipoEquipo);
        return query.list();
    }

    /**
     * Añade un equipo a la sucursal.
     *
     * @param session La sesión de Hibernate.
     * @param equipo El equipo a añadir.
     */
    public void añadirEquipo(Session session, Equipo equipo) {
        equipo.setSucursal(this);
        session.persist(equipo); // Guardar el equipo en la base de datos
    }

    /**
     * Elimina un equipo de la sucursal por su ID.
     *
     * @param session La sesión de Hibernate.
     * @param idEquipo El ID del equipo a eliminar.
     */
    public void eliminarEquipo(Session session, int idEquipo) {
        Equipo equipo = session.find(Equipo.class, idEquipo); // Usar `find()` para obtener la entidad
        if (equipo != null && equipo.getSucursal().getId() == this.id) {
            equipos.remove(equipo);
            equipo.setSucursal(null); // Desvincular la relación con la sucursal
            session.remove(equipo); // Eliminar el equipo de la base de datos
        }
    }

    /**
     * Devuelve un equipo, marcando el arriendo como finalizado.
     *
     * @param session La sesión de Hibernate.
     * @param idEquipo El ID del equipo a devolver.
     * @throws EquipoNoArrendadoException Si el equipo no está arrendado.
     */
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

    /**
     * Arrenda un equipo de la sucursal.
     *
     * @param session La sesión de Hibernate.
     * @param idEquipo El ID del equipo a arrendar.
     * @param rutCliente El RUT del cliente que arrendará el equipo.
     * @throws EquipoNoDisponibleException Si el equipo no está disponible.
     */
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

    /**
     * Genera un nombre legible para la sucursal basado en su dirección y comuna.
     *
     * @return El nombre legible de la sucursal.
     */
    public String generarNombreLegible() {
        return direccion + ", " + comuna;
    }

    // Métodos para gestionar la persistencia de Sucursal en Hibernate

    /**
     * Persiste la sucursal en la base de datos.
     *
     * @param session La sesión de Hibernate.
     */
    public void persistirSucursal(Session session) {
        session.persist(this);
    }

    /**
     * Actualiza la sucursal en la base de datos.
     *
     * @param session La sesión de Hibernate.
     */
    public void actualizarSucursal(Session session) {
        session.merge(this);
    }

    /**
     * Elimina la sucursal de la base de datos.
     *
     * @param session La sesión de Hibernate.
     */
    public void eliminarSucursal(Session session) {
        session.remove(this);
    }

    // Getters para las propiedades observables

    /**
     * Obtiene la propiedad observable del ID de la sucursal.
     *
     * @return La propiedad observable del ID.
     */
    public SimpleIntegerProperty idProperty() {
        return new SimpleIntegerProperty(this.id);
    }

    /**
     * Obtiene la propiedad observable de la región de la sucursal.
     *
     * @return La propiedad observable de la región.
     */
    public SimpleStringProperty regionProperty() {
        return regionProperty;
    }

    /**
     * Obtiene la propiedad observable de la comuna de la sucursal.
     *
     * @return La propiedad observable de la comuna.
     */
    public SimpleStringProperty comunaProperty() {
        return comunaProperty;
    }

    /**
     * Obtiene la propiedad observable de la dirección de la sucursal.
     *
     * @return La propiedad observable de la dirección.
     */
    public SimpleStringProperty direccionProperty() {
        return direccionProperty;
    }

    // Getters y Setters restantes

    /**
     * Obtiene la lista de equipos de la sucursal.
     *
     * @return La lista de equipos de la sucursal (copia inmutable).
     */
    public List<Equipo> getEquipos() {
        return List.copyOf(equipos); // Devuelve una copia inmutable de la lista
    }

    /**
     * Obtiene el ID de la sucursal.
     *
     * @return El ID de la sucursal.
     */
    public int getId() {
        return id;
    }

    /**
     * Obtiene la región de la sucursal.
     *
     * @return La región de la sucursal.
     */
    public String getRegion() {
        return region;
    }

    /**
     * Establece la región de la sucursal.
     *
     * @param region La nueva región de la sucursal.
     */
    public void setRegion(String region) {
        this.region = region;
    }
    /**
     * Obtiene la comuna de la sucursal.
     *
     * @return La comuna de la sucursal.
     */
    public String getComuna() {
        return comuna;
    }

    /**
     * Establece la comuna de la sucursal.
     *
     * @param comuna La nueva comuna de la sucursal.
     */
    public void setComuna(String comuna) {
        this.comuna = comuna;
    }

    /**
     * Obtiene la dirección de la sucursal.
     *
     * @return La dirección de la sucursal.
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * Establece la dirección de la sucursal.
     *
     * @param direccion La nueva dirección de la sucursal.
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * Método llamado automáticamente después de que una entidad Sucursal
     * es cargada desde la base de datos. Este método actualiza las propiedades
     * observables de JavaFX.
     *
     * <p>Este método es anotado con {@link PostLoad}, lo que indica que debe
     * ejecutarse después de que Hibernate haya cargado los datos de la base
     * de datos en esta entidad.</p>
     */
    @PostLoad
    private void onPostLoad() {
        actualizarPropiedades();
    }
}