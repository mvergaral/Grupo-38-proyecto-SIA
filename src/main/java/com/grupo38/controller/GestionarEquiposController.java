package com.grupo38.controller;

import com.grupo38.model.Arriendo;
import com.grupo38.model.Equipo;
import com.grupo38.model.Herramienta;
import com.grupo38.model.Maquina;
import com.grupo38.model.Sucursal;
import com.grupo38.config.HibernateUtil;
import com.grupo38.exceptions.EquipoNoArrendadoException;
import com.grupo38.exceptions.EquipoNoDisponibleException;

import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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

import org.hibernate.Hibernate;
import org.hibernate.Session;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GestionarEquiposController {

    @FXML
    private ComboBox<Sucursal> sucursalComboBox;
    @FXML
    private ComboBox<String> tipoEquipoComboBox;
    @FXML
    private ComboBox<String> estadoComboBox;
    @FXML
    private ListView<Equipo> equipoListView;

    private Sucursal sucursalSeleccionada;
    private Equipo equipoSeleccionado;
    private Map<Sucursal, List<Equipo>> cacheEquiposPorSucursal = new HashMap<>();

    @FXML
    public void initialize() {
        cargarSucursales();
        tipoEquipoComboBox.setItems(FXCollections.observableArrayList("Todos", "Herramienta", "Maquina"));
        tipoEquipoComboBox.getSelectionModel().selectFirst();
        estadoComboBox.setItems(FXCollections.observableArrayList("Todos", "Disponible", "Arrendado"));
        estadoComboBox.getSelectionModel().selectFirst();

        sucursalComboBox.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Sucursal> call(ListView<Sucursal> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Sucursal sucursal, boolean empty) {
                        super.updateItem(sucursal, empty);
                        setText((empty || sucursal == null) ? null : sucursal.generarNombreLegible());
                    }
                };
            }
        });

        sucursalComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Sucursal sucursal, boolean empty) {
                super.updateItem(sucursal, empty);
                setText((empty || sucursal == null) ? null : sucursal.generarNombreLegible());
            }
        });

        equipoListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Equipo equipo, boolean empty) {
                super.updateItem(equipo, empty);
                setText((empty || equipo == null) ? null : equipo.generarDescripcion());
            }
        });

        equipoListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> equipoSeleccionado = newValue);
    }

    private void cargarSucursales() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Sucursal> sucursales = session.createQuery("from Sucursal", Sucursal.class).list();
            sucursalComboBox.setItems(FXCollections.observableArrayList(sucursales));
        }
    }

    @FXML
    private void cargarEquipos() {
        sucursalSeleccionada = sucursalComboBox.getSelectionModel().getSelectedItem();

        if (sucursalSeleccionada != null) {
            // Verificar si los equipos ya están en la caché
            if (cacheEquiposPorSucursal.containsKey(sucursalSeleccionada)) {
                aplicarFiltros(cacheEquiposPorSucursal.get(sucursalSeleccionada));
            } else {
                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    List<Equipo> equipos = sucursalSeleccionada.equiposDeSucursal(session);
                    cacheEquiposPorSucursal.put(sucursalSeleccionada, equipos);  // Guardar en la caché
                    aplicarFiltros(equipos);
                }
            }
        }
    }

    @FXML
    private void filtrarEquipos() {
        if (sucursalSeleccionada != null && cacheEquiposPorSucursal.containsKey(sucursalSeleccionada)) {
            aplicarFiltros(cacheEquiposPorSucursal.get(sucursalSeleccionada));
        }
    }

    // Aplicar los filtros actuales de tipo y estado
    // Aplicar los filtros actuales de tipo y estado
    private void aplicarFiltros(List<Equipo> equipos) {
        String tipoSeleccionado = tipoEquipoComboBox.getSelectionModel().getSelectedItem();
        String estadoSeleccionado = estadoComboBox.getSelectionModel().getSelectedItem();

        List<Equipo> equiposFiltrados = equipos.stream()
                .filter(equipo -> tipoSeleccionado == null || "Todos".equals(tipoSeleccionado) || 
                    (tipoSeleccionado.equals("Herramienta") && equipo instanceof Herramienta) || 
                    (tipoSeleccionado.equals("Maquina") && equipo instanceof Maquina))
                .filter(equipo -> estadoSeleccionado == null || "Todos".equals(estadoSeleccionado) || 
                    (estadoSeleccionado.equals("Disponible") && !equipo.isPrestado()) || 
                    (estadoSeleccionado.equals("Arrendado") && equipo.isPrestado()))
                .collect(Collectors.toList());

        equipoListView.setItems(FXCollections.observableArrayList(equiposFiltrados));
    }

    @FXML
    private void arrendarEquipo() {
        if (equipoSeleccionado != null) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                session.beginTransaction();
                try {
                    sucursalSeleccionada.arrendarEquipo(session, equipoSeleccionado.getId(), "12345678-9");
                    session.getTransaction().commit();
                    mostrarMensaje("Equipo arrendado", "El equipo ha sido arrendado correctamente.");
                    limpiarCacheEquipos();
                    cargarEquipos();
                } catch (EquipoNoDisponibleException e) {
                    session.getTransaction().rollback();
                    mostrarMensaje("Error", e.getMessage());
                }
            }
        }
    }

    @FXML
    private void devolverEquipo() {
        if (equipoSeleccionado != null) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                session.beginTransaction();
                try {
                    equipoSeleccionado = session.find(Equipo.class, equipoSeleccionado.getId());
                    Hibernate.initialize( equipoSeleccionado.getArriendos() );
                    sucursalSeleccionada.devolverEquipo(session, equipoSeleccionado.getId());
                    Arriendo ultimoArriendo = equipoSeleccionado.ultimoArriendo();
                    session.getTransaction().commit();
                    mostrarMensaje("Éxito", "El equipo ha sido devuelto. Total a pagar: $" + ultimoArriendo.getCostoTotal());
                    limpiarCacheEquipos();
                    cargarEquipos();
                } catch (EquipoNoArrendadoException e) {
                    mostrarMensaje("Error", e.getMessage());
                }
            }
        }
    }

    @FXML
    public void eliminarEquipo() {
        if (equipoSeleccionado != null) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                session.beginTransaction();
                session.refresh(equipoSeleccionado.getSucursal());  // Refrescar para evitar problemas de LazyInitializationException
                equipoSeleccionado.getSucursal().eliminarEquipo(session, equipoSeleccionado.getId());
                session.getTransaction().commit();
                mostrarMensaje("Éxito", "El equipo ha sido eliminado correctamente.");
                limpiarCacheEquipos();
                cargarEquipos();
            } catch (Exception e) {
                e.printStackTrace();
                mostrarMensaje("Error", "No se pudo eliminar el equipo.");
            }
        }
    }

    @FXML
    private void editarEquipo() {
        if (equipoSeleccionado != null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/grupo38/view/equipo/EditarEquipoView.fxml"));
                Parent root = fxmlLoader.load();
                EditarEquipoController editarEquipoController = fxmlLoader.getController();
                editarEquipoController.setEquipo(equipoSeleccionado);
                editarEquipoController.setGestionarEquiposController(this);
                Stage stage = new Stage();
                stage.setTitle("Editar Equipo");
                stage.setScene(new Scene(root));
                stage.showAndWait();  // Esperar a que se cierre la ventana
                limpiarCacheEquipos();
                cargarEquipos();
            } catch (IOException e) {
                e.printStackTrace();
                mostrarMensaje("Error", "No se pudo abrir el formulario de edición.");
            }
        }
    }

    @FXML
    private void exportarEquiposACSV() {
        if (sucursalSeleccionada != null) {
            List<Equipo> equipos = equipoListView.getItems();
            if (equipos.isEmpty()) {
                mostrarMensaje("Error", "No hay equipos para exportar.");
                return;
            }
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
                    writer.append("ID,Nombre,Tipo,Estado\n");
                    for (Equipo equipo : equipos) {
                        String tipo = equipo instanceof Herramienta ? "Herramienta" : "Maquina";
                        String estado = equipo.isPrestado() ? "Arrendado" : "Disponible";
                        writer.append(String.format("%d,%s,%s,%s\n",
                                equipo.getId(),
                                equipo.getNombre(),
                                tipo,
                                estado));
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

    private void limpiarCacheEquipos() {
        cacheEquiposPorSucursal.remove(sucursalSeleccionada);  // Solo limpiar el caché de la sucursal actual
    }

    public void mostrarTooltip(String mensaje) {
        Tooltip tooltip = new Tooltip(mensaje);
        tooltip.show(equipoListView.getScene().getWindow(),
            equipoListView.getScene().getWindow().getX() + equipoListView.getLayoutX(),
            equipoListView.getScene().getWindow().getY() + equipoListView.getLayoutY());

        // Timeline para ocultar el tooltip después de 3 segundos (3000 ms)
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(3000), e -> tooltip.hide()));
        timeline.setCycleCount(1);  // Ejecuta el timeline solo una vez
        timeline.play();
    }

    public void mostrarMensaje(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}