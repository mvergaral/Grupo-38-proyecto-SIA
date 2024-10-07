package com.grupo38.controller;

import com.grupo38.model.Arriendo;
import com.grupo38.model.Equipo;
import com.grupo38.model.Herramienta;
import com.grupo38.model.Maquina;
import com.grupo38.model.Sucursal;
import com.grupo38.config.HibernateUtil;
import com.grupo38.exceptions.EquipoNoArrendadoException;
import com.grupo38.exceptions.EquipoNoDisponibleException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Callback;
import org.hibernate.Session;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class GestionarEquiposController {

    @FXML
    private ComboBox<Sucursal> sucursalComboBox;  // ComboBox para seleccionar la sucursal
    @FXML
    private ComboBox<String> tipoEquipoComboBox;  // ComboBox para filtrar por tipo de equipo (Herramienta o Maquina)

    @FXML
    private ComboBox<String> estadoComboBox;

    @FXML
    private ListView<Equipo> equipoListView;  // ListView para mostrar los equipos

    private Sucursal sucursalSeleccionada;  // Para almacenar la sucursal seleccionada
    private Equipo equipoSeleccionado;  // Para almacenar el equipo seleccionado

    @FXML
    public void initialize() {
        // Cargar las sucursales desde la base de datos
        cargarSucursales();

        // Agregar opciones de tipo de equipo al ComboBox
        tipoEquipoComboBox.setItems(FXCollections.observableArrayList("Todos", "Herramienta", "Maquina"));
        estadoComboBox.setItems(FXCollections.observableArrayList("Todos", "Disponible", "Arrendado"));

        sucursalComboBox.setCellFactory(new Callback<ListView<Sucursal>, ListCell<Sucursal>>() {
            @Override
            public ListCell<Sucursal> call(ListView<Sucursal> param) {
                return new ListCell<Sucursal>() {
                    @Override
                    protected void updateItem(Sucursal sucursal, boolean empty) {
                        super.updateItem(sucursal, empty);
                        if (empty || sucursal == null) {
                            setText(null);
                        } else {
                            // Mostrar el nombre legible de la sucursal
                            setText(sucursal.generarNombreLegible());
                        }
                    }
                };
            }
        });

        // También mostrar el nombre legible cuando se seleccione una sucursal en el ComboBox
        sucursalComboBox.setButtonCell(new ListCell<Sucursal>() {
            @Override
            protected void updateItem(Sucursal sucursal, boolean empty) {
                super.updateItem(sucursal, empty);
                if (empty || sucursal == null) {
                    setText(null);
                } else {
                    setText(sucursal.generarNombreLegible());
                }
            }
        });

        equipoListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Equipo equipo, boolean empty) {
                super.updateItem(equipo, empty);
                if (empty || equipo == null) {
                    setText(null);
                } else {
                    setText(equipo.generarDescripcion());  // Mostrar la descripción legible
                }
            }
        });

        // Manejar la selección de equipo
        equipoListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            equipoSeleccionado = newValue;
        });
    }

    // Cargar las sucursales en el ComboBox
    private void cargarSucursales() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Sucursal> sucursales = session.createQuery("from Sucursal", Sucursal.class).list();
            sucursalComboBox.setItems(FXCollections.observableArrayList(sucursales));
        }
    }

    // Cargar los equipos de la sucursal seleccionada
    @FXML
    private void cargarEquipos() {
        sucursalSeleccionada = sucursalComboBox.getSelectionModel().getSelectedItem();

        if (sucursalSeleccionada != null) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                // Cargar todos los equipos disponibles
                List<Equipo> equipos = sucursalSeleccionada.equiposDeSucursal(session);
                equipoListView.setItems(FXCollections.observableArrayList(equipos));
            }
        }
    }

    // Filtrar equipos por tipo (Herramienta o Maquina)
    @FXML
    private void filtrarEquipos() {
        String tipoSeleccionado = tipoEquipoComboBox.getSelectionModel().getSelectedItem();
        String estadoSeleccionado = estadoComboBox.getSelectionModel().getSelectedItem();

        if (sucursalSeleccionada != null) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                List<Equipo> equiposFiltrados;

                if ("Herramienta".equals(tipoSeleccionado)) {
                    // Filtrar por equipos de tipo Herramienta
                    equiposFiltrados = sucursalSeleccionada.equiposDeSucursal(session, Herramienta.class);
                } else if ("Maquina".equals(tipoSeleccionado)) {
                    // Filtrar por equipos de tipo Maquina
                    equiposFiltrados = sucursalSeleccionada.equiposDeSucursal(session, Maquina.class);
                } else {
                    equiposFiltrados = sucursalSeleccionada.equiposDeSucursal(session);
                }

                if ("Disponible".equals(estadoSeleccionado)) {
                    equiposFiltrados.removeIf(equipo -> equipo.isPrestado());
                } else if ("Arrendado".equals(estadoSeleccionado)) {
                    equiposFiltrados.removeIf(equipo -> !equipo.isPrestado());
                }

                equipoListView.setItems(FXCollections.observableArrayList(equiposFiltrados));
            }
        }
    }

    // Arrendar el equipo seleccionado
    @FXML
    private void arrendarEquipo() {
        Equipo equipoSeleccionado = equipoListView.getSelectionModel().getSelectedItem();

        if (equipoSeleccionado != null) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                session.beginTransaction();

                // Verificar si el equipo está disponible para arrendar
                try {
                    sucursalSeleccionada.arrendarEquipo(session, equipoSeleccionado.getId(), "12345678-9");
                    session.getTransaction().commit();
                    mostrarMensaje("Equipo arrendado", "El equipo ha sido arrendado correctamente.");
                    cargarEquipos();  // Actualizar la lista de equipos
                } catch (EquipoNoDisponibleException e) {
                    session.getTransaction().rollback();
                    mostrarMensaje("Error", e.getMessage());
                    return;
                }

            } catch (Exception e) {
                mostrarMensaje("Error", "El equipo no está disponible para arrendar.");
            }
        }
    }

    // Método para devolver el equipo seleccionado
    @FXML
    private void devolverEquipo() {
        if (sucursalSeleccionada != null) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                session.beginTransaction();

                try {
                    equipoSeleccionado = session.find(Equipo.class, equipoSeleccionado.getId());
                    // Intentar devolver el equipo
                    sucursalSeleccionada.devolverEquipo(session, equipoSeleccionado.getId());
                    session.getTransaction().commit();

                    Arriendo ultimoArriendo = equipoSeleccionado.ultimoArriendo();
                    double total = ultimoArriendo.getCostoTotal();

                    mostrarMensaje("Éxito", "El equipo ha sido devuelto. Total a pagar: $" + total);
                    cargarEquipos();  // Actualizar la lista de equipos

                } catch (EquipoNoArrendadoException e) {
                    mostrarMensaje("Error", e.getMessage());
                }

            } catch (Exception e) {
                e.printStackTrace();
                mostrarMensaje("Error", "Hubo un problema al devolver el equipo.");
            }
        } else {
            mostrarMensaje("Error", "Por favor, seleccione una sucursal y un equipo.");
        }
    }

    @FXML
    public void eliminarEquipo() {
        Equipo equipoSeleccionado = equipoListView.getSelectionModel().getSelectedItem();

        if (equipoSeleccionado != null) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                session.beginTransaction();

                // Refrescar la colección equipos para inicializarla
                session.refresh(equipoSeleccionado.getSucursal());

                // Eliminar el equipo de la sucursal y de la base de datos
                equipoSeleccionado.getSucursal().eliminarEquipo(session, equipoSeleccionado.getId());

                session.getTransaction().commit();
                mostrarMensaje("Éxito", "El equipo ha sido eliminado correctamente.");
                cargarEquipos();  // Volver a cargar los equipos
            } catch (Exception e) {
                e.printStackTrace();
                mostrarMensaje("Error", "No se pudo eliminar el equipo.");
            }
        } else {
            mostrarMensaje("Error", "Por favor, seleccione un equipo para eliminar.");
        }
    }

    // Método para editar el equipo seleccionado
    @FXML
    private void editarEquipo() {
        if (equipoSeleccionado != null) {
            try {
                // Abrir una nueva ventana para editar los detalles del equipo
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/grupo38/view/equipo/EditarEquipoView.fxml"));
                Parent root = fxmlLoader.load();

                // Pasar el equipo seleccionado al controlador de edición
                EditarEquipoController editarEquipoController = fxmlLoader.getController();
                editarEquipoController.setEquipo(equipoSeleccionado);
                editarEquipoController.setGestionarEquiposController(this);

                // Mostrar el formulario de edición
                Stage stage = new Stage();
                stage.setTitle("Editar Equipo");
                stage.setScene(new Scene(root));
                stage.showAndWait();  // Esperar hasta que se cierre

                cargarEquipos();  // Actualizar la lista de equipos después de editar

            } catch (IOException e) {
                e.printStackTrace();
                mostrarMensaje("Error", "No se pudo abrir el formulario de edición.");
            }
        } else {
            mostrarMensaje("Error", "Por favor, seleccione un equipo para editar.");
        }
    }

    @FXML
    private void exportarEquiposACSV() {
        if (sucursalSeleccionada != null) {
            List<Equipo> equipos = equipoListView.getItems(); // Equipos actualmente mostrados en la lista

            if (equipos.isEmpty()) {
                mostrarMensaje("Error", "No hay equipos para exportar.");
                return;
            }

            // Crear el nombre del archivo basado en la sucursal
            String nombreArchivo = sucursalSeleccionada.getId() + "_" +
                    sucursalSeleccionada.getDireccion().replace(" ", "_") + "_" +
                    sucursalSeleccionada.getComuna().replace(" ", "_") + "_" +
                    sucursalSeleccionada.getRegion().replace(" ", "_") + ".csv";

            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName(nombreArchivo);
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));
            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                try (FileWriter writer = new FileWriter(file)) {
                    // Escribir el encabezado del CSV
                    writer.append("ID,Nombre,Tipo,Estado\n");

                    // Escribir los equipos
                    for (Equipo equipo : equipos) {
                        String tipo = equipo instanceof Herramienta ? "Herramienta" : "Maquina";
                        String estado = equipo.isPrestado() ? "Arrendado" : "Disponible";
                        writer.append(String.format("%d,%s,%s,%s\n",
                                equipo.getId(),
                                equipo.getNombre(),
                                tipo,
                                estado
                        ));
                    }

                    mostrarMensaje("Éxito", "Los equipos han sido exportados correctamente a CSV.");
                } catch (IOException e) {
                    mostrarMensaje("Error", "No se pudo exportar los equipos.");
                    e.printStackTrace();
                }
            }
        } else {
            mostrarMensaje("Error", "Por favor, seleccione una sucursal primero.");
        }
    }

    public void mostrarTooltip(String mensaje) {
        Tooltip tooltip = new Tooltip(mensaje);
        tooltip.show(equipoListView.getScene().getWindow(),
            equipoListView.getScene().getWindow().getX() + equipoListView.getLayoutX(),
            equipoListView.getScene().getWindow().getY() + equipoListView.getLayoutY());
    }

    // Mostrar mensajes de error o confirmación
    public void mostrarMensaje(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}