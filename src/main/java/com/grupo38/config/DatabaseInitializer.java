package com.grupo38.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseInitializer {

    private static DatabaseInitializer instance;

    private DatabaseInitializer() {
        // Constructor privado para evitar múltiples instancias
    }

    public static synchronized DatabaseInitializer getInstance() {
        if (instance == null) {
            instance = new DatabaseInitializer();
        }
        return instance;
    }

    public void initializeDatabase() {
        createRoleIfNotExists();
        createDatabaseIfNotExists();
    }

    private void createRoleIfNotExists() {
        String url = "jdbc:postgresql://localhost:5432/postgres";  // Conectarse a la base de datos postgres predeterminada
        String user = "postgres";  // El superusuario de PostgreSQL
        String password = "";  // La contraseña del superusuario

        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement()) {

            // Comando SQL para crear el rol (usuario)
            String sql = "DO $$ " +
                    "BEGIN " +
                    "   IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'user38') THEN " +
                    "      CREATE ROLE user38 WITH LOGIN PASSWORD 'your_password'; " +
                    "   END IF; " +
                    "END $$;";

            statement.execute(sql);

            System.out.println("Role user38 created or already exists!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createDatabaseIfNotExists() {
        String url = "jdbc:postgresql://localhost:5432/postgres";  // Conectarse a la base de datos postgres predeterminada
        String user = "postgres";  // El superusuario de PostgreSQL
        String password = "";  // La contraseña del superusuario

        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement()) {

            // Comprobar si la base de datos existe
            String checkDatabaseSql = "SELECT 1 FROM pg_database WHERE datname = 'sia'";
            var resultSet = statement.executeQuery(checkDatabaseSql);

            if (!resultSet.next()) {
                // Crear la base de datos si no existe
                String createDatabaseSql = "CREATE DATABASE sia OWNER grupo38";
                statement.executeUpdate(createDatabaseSql);
                System.out.println("Database sia created successfully!");
            } else {
                System.out.println("Database sia already exists!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}