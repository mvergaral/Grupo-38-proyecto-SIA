package com.grupo38.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Clase que representa una herramienta dentro del sistema.
 * Extiende de la clase abstracta {@link Equipo} y proporciona implementaciones
 * específicas para los métodos abstractos, como el cálculo de la tarifa.
 */
@Entity
@DiscriminatorValue("HERRAMIENTA") // Identificador en la columna discriminadora
public class Herramienta extends Equipo {

    /**
     * Constructor vacío requerido por Hibernate.
     */
    public Herramienta() {}

    /**
     * Constructor que permite crear una herramienta con una sucursal y nombre específicos.
     *
     * @param sucursal La sucursal a la que pertenece la herramienta.
     * @param nombre El nombre de la herramienta.
     */
    public Herramienta(Sucursal sucursal, String nombre) {
        super(sucursal, nombre); // Llamada al constructor de la clase abstracta Equipo
    }

    /**
     * Calcula la tarifa de arrendamiento para una herramienta.
     * La tarifa es fija y se establece en 50.0.
     *
     * @return La tarifa de arrendamiento para la herramienta.
     */
    @Override
    public double calcularTarifa() {
        return 50.0; // Tarifa fija para herramientas (puedes ajustar esta lógica según tu caso)
    }

    /**
     * Genera una descripción para la herramienta, que incluye su ID, nombre, tipo y estado.
     *
     * @return La descripción de la herramienta en formato de cadena.
     */
    @Override
    public String generarDescripcion() {
        return String.format("ID: %d, Nombre: %s, Tipo: %s, Estado: %s",
            getId(),
            getNombre(),
            getClass().getSimpleName(),
            isPrestado() ? "Arrendado" : "Disponible");
    }

}