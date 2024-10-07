package com.grupo38.exceptions;

// Excepción lanzada cuando se intenta arrendar un equipo que ya está arrendado
public class EquipoNoDisponibleException extends Exception {
    public EquipoNoDisponibleException() {
        super("El equipo ya está arrendado y no está disponible.");
    }
}
