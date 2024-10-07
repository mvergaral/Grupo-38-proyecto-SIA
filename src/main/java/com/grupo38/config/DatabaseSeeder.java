package com.grupo38.config;

import com.grupo38.model.Sucursal;
import com.grupo38.model.Herramienta;
import com.grupo38.model.Maquina;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class DatabaseSeeder {

    public static void seedDatabase() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Verificar si ya hay sucursales en la base de datos
            List<Sucursal> sucursalesExistentes = session.createQuery("from Sucursal", Sucursal.class).list();

            // Si ya existen sucursales, no realizar la inserción
            if (!sucursalesExistentes.isEmpty()) {
                System.out.println("Ya existen sucursales en la base de datos. No se realizará la inserción.");
                return;
            }

            Transaction transaction = session.beginTransaction();

            // Crear 3 sucursales
            for (int i = 1; i <= 3; i++) {
                Sucursal sucursal = new Sucursal("Región " + i, "Comuna " + i, "Dirección " + i);

                // Persistir la sucursal primero
                session.persist(sucursal);

                // Crear 3 herramientas
                for (int j = 1; j <= 3; j++) {
                    Herramienta herramienta = new Herramienta(sucursal, "Herramienta " + j + " de Sucursal " + i);
                    sucursal.añadirEquipo(session, herramienta);
                }

                // Crear 3 máquinas
                for (int k = 1; k <= 3; k++) {
                    Maquina maquina = new Maquina(sucursal, "Maquina " + k + " de Sucursal " + i);
                    sucursal.añadirEquipo(session, maquina);
                }

                // Actualizar la sucursal con los equipos ya añadidos
                session.persist(sucursal);  // O usar session.saveOrUpdate(sucursal);
            }

            transaction.commit();
            System.out.println("Base de datos 'seeded' correctamente con 3 sucursales y 6 equipos en cada una.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}