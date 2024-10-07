package com.grupo38.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import com.grupo38.model.Sucursal;
import com.grupo38.config.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class SucursalController {

    // Campos para crear una nueva sucursal
    @FXML
    private TextField regionField;
    @FXML
    private TextField comunaField;
    @FXML
    private TextField direccionField;

    @FXML
    private Label confirmationLabel;

    // Botón para crear la sucursal
    @FXML
    private Button crearSucursalButton;


    // Método para crear una nueva sucursal
    @FXML
    private void crearSucursal(ActionEvent event) {
        String region = regionField.getText();
        String comuna = comunaField.getText();
        String direccion = direccionField.getText();

        // Validar los campos
        if (region.isEmpty() || comuna.isEmpty() || direccion.isEmpty()) {
            confirmationLabel.setText("Por favor, complete todos los campos.");
            confirmationLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Crear la nueva sucursal
        Sucursal nuevaSucursal = new Sucursal(region, comuna, direccion);

        // Guardar la sucursal en la base de datos
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            nuevaSucursal.persistirSucursal(session); // Usar el método del modelo
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            confirmationLabel.setText("Error al crear la sucursal.");
            confirmationLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Limpiar los campos
        regionField.clear();
        comunaField.clear();
        direccionField.clear();

        // Confirmar que la sucursal fue creada
        confirmationLabel.setText("Sucursal creada exitosamente.");
        confirmationLabel.setStyle("-fx-text-fill: green;");
    }
}