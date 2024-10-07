package com.grupo38.controller;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    private Pane contentPane;

    @FXML
    private void verSucursales(ActionEvent event) {
        // Navegar a la vista de ver sucursales
        cargarVista("sucursal/VerSucursalesView", "Ver Sucursales");
    }

    @FXML
    private void gestionarEquipos(ActionEvent event) {
        // Navegar a la vista de gestionar equipos
        cargarVista("equipo/GestionarEquiposView", "Gestionar Equipos");
    }

    // Método para cargar una nueva vista en la escena principal
    private void cargarVista(String fxml, String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/grupo38/view/" + fxml + ".fxml"));
            Parent root = fxmlLoader.load();
            contentPane.getChildren().clear(); // Limpiar el contenido actual
            contentPane.getChildren().add(root); // Agregar la nueva vista
            Stage stage = (Stage) contentPane.getScene().getWindow();
            stage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}