package com.grupo38.model;

import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;

@Entity
@DiscriminatorValue("HERRAMIENTA") // Identificador en la columna discriminadora
public class Herramienta extends Equipo {

    // Constructor vacío requerido por Hibernate
    public Herramienta() {}

    // Constructor con parámetros
    public Herramienta(Sucursal sucursal, String nombre) {
        super(sucursal, nombre); // Llamada al constructor de la clase abstracta Equipo
    }

    // Sobrescribir el método abstracto calcularTarifa
    @Override
    public double calcularTarifa() {
        return 50.0; // Tarifa fija para herramientas (puedes ajustar esta lógica según tu caso)
    }

    @Override
    public String generarDescripcion() {
        return String.format("ID: %d, Nombre: %s, Tipo: %s, Estado: %s",
            getId(),
            getNombre(),
            getClass().getSimpleName(),
            isPrestado() ? "Arrendado" : "Disponible");
    }

}