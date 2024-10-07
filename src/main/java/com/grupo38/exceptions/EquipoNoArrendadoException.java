package com.grupo38.exceptions;

// Excepción lanzada cuando se intenta devolver un equipo que no está arrendado
public class EquipoNoArrendadoException extends Exception {
    public EquipoNoArrendadoException() {
        super("El equipo no está arrendado.");
    }
}
