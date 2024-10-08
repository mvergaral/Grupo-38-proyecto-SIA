package com.grupo38.controller;

import com.grupo38.model.Sucursal;
import com.grupo38.config.HibernateUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.hibernate.Session;

public class EditarSucursalController {

    @FXML
    private TextField comunaField;

    @FXML
    private TextField direccionField;

    @FXML
    private TextField regionField;

    @FXML
    private Button confirmarButton;

    private Sucursal sucursal;
    private VerSucursalesController verSucursalesController;

    // Método para establecer la sucursal y cargar sus datos en los campos
    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
        cargarDatosSucursal();
    }

    // Método para cargar los datos de la sucursal en los TextField
    private void cargarDatosSucursal() {
        comunaField.setText(sucursal.getComuna());
        direccionField.setText(sucursal.getDireccion());
        regionField.setText(sucursal.getRegion());
    }

    // Establecer la referencia al controlador principal
    public void setVerSucursalesController(VerSucursalesController verSucursalesController) {
        this.verSucursalesController = verSucursalesController;
    }

    // Método para confirmar y guardar los cambios de la sucursal
    @FXML
    public void confirmarCambios() {
        // Actualizar los datos de la sucursal
        sucursal.setComuna(comunaField.getText());
        sucursal.setDireccion(direccionField.getText());
        sucursal.setRegion(regionField.getText());

        // Guardar los cambios en la base de datos
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(sucursal);  // Actualizar la sucursal en la base de datos
            session.getTransaction().commit();

            // Actualizar la tabla de sucursales
            verSucursalesController.cargarSucursales();

            // Mostrar el tooltip de éxito
            verSucursalesController.mostrarTooltip("¡Sucursal actualizada con éxito!", true);

            // Cerrar la ventana después de confirmar
            Stage stage = (Stage) confirmarButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            verSucursalesController.mostrarTooltip("No se pudo actualizar la sucursal.", false);
            e.printStackTrace();
        }
    }

}