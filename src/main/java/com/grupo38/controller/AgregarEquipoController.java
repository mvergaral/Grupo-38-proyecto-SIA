package com.grupo38.controller;

import com.grupo38.model.Equipo;
import com.grupo38.model.Herramienta;
import com.grupo38.model.Maquina;
import com.grupo38.model.Sucursal;
import com.grupo38.config.HibernateUtil;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import org.hibernate.Session;

public class AgregarEquipoController {

    @FXML
    private TextField nombreEquipoTextField;  // Campo para ingresar el nombre del equipo

    @FXML
    private ComboBox<String> tipoEquipoComboBox;  // ComboBox para seleccionar el tipo de equipo (Herramienta o Maquina)

    private Sucursal sucursal;

    // Inicializar los valores para el tipo de equipo
    @FXML
    public void initialize() {
        tipoEquipoComboBox.getItems().addAll("Herramienta", "Maquina");
    }

    // Método para recibir la sucursal seleccionada desde el controlador VerSucursalesController
    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    // Método que se ejecuta al presionar el botón para agregar un equipo
    @FXML
    private void agregarEquipo() {
        String nombreEquipo = nombreEquipoTextField.getText();
        String tipoEquipoSeleccionado = tipoEquipoComboBox.getSelectionModel().getSelectedItem();

        if (nombreEquipo.isEmpty() || tipoEquipoSeleccionado == null) {
            mostrarMensaje("Error", "Por favor, ingresa el nombre del equipo y selecciona su tipo.");
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            Equipo nuevoEquipo;
            if (tipoEquipoSeleccionado.equals("Herramienta")) {
                nuevoEquipo = new Herramienta(sucursal, nombreEquipo);
            } else {
                nuevoEquipo = new Maquina(sucursal, nombreEquipo);
            }

            // Asignar el equipo a la sucursal seleccionada
            sucursal.añadirEquipo(session, nuevoEquipo);

            session.getTransaction().commit();
            mostrarMensaje("Éxito", "Equipo añadido correctamente a la sucursal.");
            // Cerrar la ventana actual después de añadir el equipo
            Stage stage = (Stage) nombreEquipoTextField.getScene().getWindow();  // Obtener el stage desde cualquier componente
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("Error", "Hubo un problema al añadir el equipo.");
        }
    }

    // Método para mostrar mensajes de alerta
    private void mostrarMensaje(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}