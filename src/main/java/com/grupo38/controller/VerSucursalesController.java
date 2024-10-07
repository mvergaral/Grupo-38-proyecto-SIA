package com.grupo38.controller;

import com.grupo38.model.Sucursal;
import com.grupo38.config.HibernateUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.TableCell;
import org.hibernate.Session;
import java.util.List;

public class VerSucursalesController {

    @FXML
    private TableView<Sucursal> sucursalTableView;

    @FXML
    private TableColumn<Sucursal, Integer> idColumn;

    @FXML
    private TableColumn<Sucursal, String> comunaColumn;

    @FXML
    private TableColumn<Sucursal, String> direccionColumn;

    @FXML
    private TableColumn<Sucursal, String> regionColumn;

    @FXML
    private TableColumn<Sucursal, Void> actionColumn;  // Columna para el botón

    @FXML
    public void initialize() {
        // Inicializar las columnas del TableView
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        comunaColumn.setCellValueFactory(new PropertyValueFactory<>("comuna"));
        direccionColumn.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));

        // Añadir botón de "Añadir equipo" a la columna de acción
        addButtonToTable();

        // Cargar las sucursales en la tabla
        cargarSucursales();
    }

    // Método para cargar las sucursales en la tabla
    public void cargarSucursales() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Sucursal> sucursales = session.createQuery("from Sucursal", Sucursal.class).list();
            sucursalTableView.setItems(FXCollections.observableArrayList(sucursales));
        }
    }

    // Método para añadir un botón "Añadir equipo" en la tabla
    private void addButtonToTable() {
        Callback<TableColumn<Sucursal, Void>, TableCell<Sucursal, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Sucursal, Void> call(final TableColumn<Sucursal, Void> param) {
                final TableCell<Sucursal, Void> cell = new TableCell<>() {

                    private final Button btn = new Button("+");

                    {
                        // Estilo para el hover que muestra "Añadir equipo"
                        btn.setTooltip(new javafx.scene.control.Tooltip("Añadir equipo"));
                        btn.setOnAction(event -> {
                            Sucursal sucursal = getTableView().getItems().get(getIndex());
                            abrirFormularioAgregarEquipo(sucursal);  // Llamar al método para abrir el formulario
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        actionColumn.setCellFactory(cellFactory);
    }

    // Método para abrir el formulario de agregar equipo para la sucursal seleccionada
    private void abrirFormularioAgregarEquipo(Sucursal sucursal) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/grupo38/view/equipo/AgregarEquipoView.fxml"));
            Parent root = fxmlLoader.load();

            // Pasar la sucursal seleccionada al controlador del formulario
            AgregarEquipoController agregarEquipoController = fxmlLoader.getController();
            agregarEquipoController.setSucursal(sucursal);

            // Crear una nueva ventana
            Stage stage = new Stage();
            stage.setTitle("Añadir Equipo a " + sucursal.generarNombreLegible());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error", "No se pudo abrir el formulario para añadir equipo.");
        }
    }

    @FXML
    public void añadirSucursal() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/grupo38/view/sucursal/CrearSucursalView.fxml"));
            Parent root = fxmlLoader.load();

            // Obtener el controlador de la vista de crear sucursal
            CrearSucursalController crearSucursalController = fxmlLoader.getController();
            crearSucursalController.setVerSucursalesController(this);  // Pasar referencia de este controlador

            // Crear una nueva ventana
            Stage stage = new Stage();
            stage.setTitle("Añadir Sucursal");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error", "No se pudo abrir el formulario para añadir sucursal.");
        }
    }

    // Método para mostrar un tooltip de éxito
    public void mostrarTooltipExito() {
        Tooltip tooltip = new Tooltip("¡Sucursal creada con éxito!");
        tooltip.show(sucursalTableView.getScene().getWindow(),
            sucursalTableView.getScene().getWindow().getX() + sucursalTableView.getLayoutX(),
            sucursalTableView.getScene().getWindow().getY() + sucursalTableView.getLayoutY());
    }

    // Método para mostrar mensajes de alerta
    private void mostrarMensaje(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}