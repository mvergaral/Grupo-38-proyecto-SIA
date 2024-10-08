package com.grupo38.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Clase que representa una máquina dentro del sistema.
 * Extiende de la clase abstracta {@link Equipo} y proporciona implementaciones
 * específicas para los métodos abstractos, como el cálculo de la tarifa.
 */
@Entity
@DiscriminatorValue("MAQUINA") // Identificador en la columna discriminadora
public class Maquina extends Equipo {

    /**
     * Constructor vacío requerido por Hibernate.
     */
    public Maquina() {}

    /**
     * Constructor que permite crear una máquina con una sucursal y nombre específicos.
     *
     * @param sucursal La sucursal a la que pertenece la máquina.
     * @param nombre El nombre de la máquina.
     */
    public Maquina(Sucursal sucursal, String nombre) {
        super(sucursal, nombre); // Llamada al constructor de la clase abstracta Equipo
    }

    /**
     * Calcula la tarifa de arrendamiento para una máquina.
     * La tarifa es fija y se establece en 100.0.
     *
     * @return La tarifa de arrendamiento para la máquina.
     */
    @Override
    public double calcularTarifa() {
        return 100.0; // Tarifa fija para máquinas
    }

    /**
     * Genera una descripción para la máquina, que incluye su ID, nombre, tipo y estado.
     *
     * @return La descripción de la máquina en formato de cadena.
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