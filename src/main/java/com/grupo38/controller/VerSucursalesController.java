package com.grupo38.controller;

import com.grupo38.model.Sucursal;
import com.grupo38.config.HibernateUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
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
import org.hibernate.Session;
import javafx.scene.control.TableCell;
import java.io.IOException;
import java.util.List;
import javafx.scene.layout.HBox;

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

        // Añadir botones de "Añadir equipo", "Editar" y "Eliminar" a la columna de acción
        addButtonsToTable();

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

    // Método para añadir los botones "Añadir equipo", "Editar" y "Eliminar" en la tabla
    private void addButtonsToTable() {
        Callback<TableColumn<Sucursal, Void>, TableCell<Sucursal, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Sucursal, Void> call(final TableColumn<Sucursal, Void> param) {
                final TableCell<Sucursal, Void> cell = new TableCell<>() {

                    private final Button btnAdd = new Button("+");
                    private final Button btnEdit = new Button("Editar");
                    private final Button btnDelete = new Button("Eliminar");

                    {
                        // Estilo para el botón de añadir equipo
                        btnAdd.setTooltip(new Tooltip("Añadir equipo"));
                        btnAdd.setOnAction(event -> {
                            Sucursal sucursal = getTableView().getItems().get(getIndex());
                            abrirFormularioAgregarEquipo(sucursal);  // Llamar al método para abrir el formulario de añadir equipo
                        });

                        // Estilo para el botón de editar sucursal
                        btnEdit.setTooltip(new Tooltip("Editar sucursal"));
                        btnEdit.setOnAction(event -> {
                            Sucursal sucursal = getTableView().getItems().get(getIndex());
                            abrirFormularioEditarSucursal(sucursal);  // Llamar al método para abrir el formulario de editar
                        });

                        // Estilo para el botón de eliminar sucursal
                        btnDelete.setTooltip(new Tooltip("Eliminar sucursal"));
                        btnDelete.setOnAction(event -> {
                            Sucursal sucursal = getTableView().getItems().get(getIndex());
                            eliminarSucursal(sucursal);  // Llamar al método para eliminar la sucursal
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            // Crear un contenedor HBox para alinear los botones
                            HBox buttonGroup = new HBox(5, btnAdd, btnEdit, btnDelete);
                            setGraphic(buttonGroup);
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
            mostrarTooltip("Error: No se pudo abrir el formulario para añadir equipo.", false);
        }
    }

    // Método para abrir el formulario de edición de sucursal
    private void abrirFormularioEditarSucursal(Sucursal sucursal) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/grupo38/view/sucursal/EditarSucursalView.fxml"));
            Parent root = fxmlLoader.load();

            // Pasar la sucursal seleccionada al controlador del formulario
            EditarSucursalController editarSucursalController = fxmlLoader.getController();
            editarSucursalController.setSucursal(sucursal);
            editarSucursalController.setVerSucursalesController(this);

            // Crear una nueva ventana
            Stage stage = new Stage();
            stage.setTitle("Editar Sucursal " + sucursal.generarNombreLegible());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarTooltip("Error: No se pudo abrir el formulario para editar la sucursal.", false);
        }
    }

    // Método para eliminar una sucursal
    private void eliminarSucursal(Sucursal sucursal) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.remove(sucursal);
            session.getTransaction().commit();
            mostrarTooltip("La sucursal ha sido eliminada correctamente.", true);
            cargarSucursales();  // Refrescar la tabla después de eliminar
        } catch (Exception e) {
            e.printStackTrace();
            mostrarTooltip("Error: No se pudo eliminar la sucursal.", false);
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
            mostrarTooltip("Error: No se pudo abrir el formulario para añadir sucursal.", false);
        }
    }

    // Método para mostrar mensajes con Tooltip
    public void mostrarTooltip(String mensaje, boolean esExito) {
        Tooltip tooltip = new Tooltip(mensaje);

        // Cambiar el estilo dependiendo de si es éxito o error
        if (esExito) {
            tooltip.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");  // Verde para éxito
        } else {
            tooltip.setStyle("-fx-background-color: #FF6347; -fx-text-fill: white; -fx-font-weight: bold;");  // Rojo para error
        }

        // Mostrar el tooltip
        tooltip.show(sucursalTableView.getScene().getWindow(),
            sucursalTableView.getScene().getWindow().getX() + sucursalTableView.getLayoutX(),
            sucursalTableView.getScene().getWindow().getY() + sucursalTableView.getLayoutY());

        // Ocultar el tooltip después de unos segundos
        javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
        delay.setOnFinished(event -> tooltip.hide());  // Ocultar el tooltip después de 3 segundos
        delay.play();  // Reproducir la transición
    }
}