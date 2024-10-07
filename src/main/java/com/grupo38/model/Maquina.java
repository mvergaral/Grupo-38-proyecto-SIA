package com.grupo38.model;

import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;

@Entity
@DiscriminatorValue("MAQUINA") // Identificador en la columna discriminadora
public class Maquina extends Equipo {

    // Constructor vacío requerido por Hibernate
    public Maquina() {}

    // Constructor con parámetros
    public Maquina(Sucursal sucursal, String nombre) {
        super(sucursal, nombre); // Llamada al constructor de la clase abstracta Equipo
    }

    // Sobrescribir el método abstracto calcularTarifa
    @Override
    public double calcularTarifa() {
        return 100.0; // Tarifa fija para máquinas
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