package com.grupo38.controller;

import com.grupo38.model.Sucursal;
import com.grupo38.config.HibernateUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.hibernate.Session;

public class CrearSucursalController {

    @FXML
    private TextField regionField;

    @FXML
    private TextField comunaField;

    @FXML
    private TextField direccionField;

    private VerSucursalesController verSucursalesController;  // Referencia al controlador principal

    @FXML
    private void crearSucursal() {
        String region = regionField.getText();
        String comuna = comunaField.getText();
        String direccion = direccionField.getText();

        if (!region.isEmpty() && !comuna.isEmpty() && !direccion.isEmpty()) {
            Sucursal nuevaSucursal = new Sucursal(region, comuna, direccion);
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                session.beginTransaction();
                session.persist(nuevaSucursal);
                session.getTransaction().commit();

                // Cerrar la ventana actual si el guardado es exitoso
                Stage stage = (Stage) regionField.getScene().getWindow();
                stage.close();

                verSucursalesController.mostrarTooltipExito();

                // Recargar la lista de sucursales en la tabla
                if (verSucursalesController != null) {
                    verSucursalesController.cargarSucursales();
                }
            }
        }
    }

    // Método para establecer el controlador de la vista principal
    public void setVerSucursalesController(VerSucursalesController controller) {
        this.verSucursalesController = controller;
    }
}