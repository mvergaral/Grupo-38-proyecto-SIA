package com.grupo38.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.hibernate.Session;
import com.grupo38.config.HibernateUtil;
import com.grupo38.model.Equipo;
import com.grupo38.model.Herramienta;
import com.grupo38.model.Maquina;

public class EditarEquipoController {

    @FXML
    private TextField nombreEquipoField;
    @FXML
    private ComboBox<String> tipoEquipoComboBox;

    private Equipo equipo;
    private GestionarEquiposController gestionarEquiposController;

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
        cargarDatosEquipo();
    }

    @FXML
    public void initialize() {
        // Inicializa el ComboBox aquí, en el controlador, no en el FXML
        tipoEquipoComboBox.setItems(FXCollections.observableArrayList("Herramienta", "Maquina"));
    }

    // Cargar los datos del equipo seleccionado en el formulario
    private void cargarDatosEquipo() {
        nombreEquipoField.setText(equipo.getNombre());

        if (equipo instanceof Herramienta) {
            tipoEquipoComboBox.setValue("Herramienta");
        } else if (equipo instanceof Maquina) {
            tipoEquipoComboBox.setValue("Maquina");
        }
    }

    // Guardar los cambios realizados en el equipo
    @FXML
    private void guardarCambios() {
        String nuevoNombre = nombreEquipoField.getText();
        String nuevoTipo = tipoEquipoComboBox.getValue();

        // Si el tipo cambia, se debe crear una nueva instancia del tipo correcto
        if ((nuevoTipo.equals("Herramienta") && equipo instanceof Maquina) ||
            (nuevoTipo.equals("Maquina") && equipo instanceof Herramienta)) {

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                session.beginTransaction();

                // Refrescar la colección para asegurarse de que esté inicializada
                session.refresh(equipo.getSucursal());  // Inicializar la colección lazy

                // Eliminar el equipo antiguo de la sucursal y de la base de datos
                equipo.getSucursal().eliminarEquipo(session, equipo.getId());

                // Crear el nuevo equipo con el tipo correcto
                Equipo nuevoEquipo;
                if (nuevoTipo.equals("Herramienta")) {
                    nuevoEquipo = new Herramienta(equipo.getSucursal(), nuevoNombre);
                } else {
                    nuevoEquipo = new Maquina(equipo.getSucursal(), nuevoNombre);
                }

                // Añadir el nuevo equipo a la sucursal y persistirlo en la base de datos
                equipo.getSucursal().añadirEquipo(session, nuevoEquipo);
                session.getTransaction().commit();
                gestionarEquiposController.mostrarTooltip("Los cambios se han guardado correctamente.");

            } catch (Exception e) {
                e.printStackTrace();
                gestionarEquiposController.mostrarMensaje("Error", "No se pudieron guardar los cambios.");
            }

        } else {
            // Si no cambia el tipo, simplemente actualizamos el nombre
            equipo.setNombre(nuevoNombre);

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                session.beginTransaction();
                session.merge(equipo);  // Actualizamos el equipo en la base de datos
                session.getTransaction().commit();
                gestionarEquiposController.mostrarTooltip("Los cambios se han guardado correctamente.");
            } catch (Exception e) {
                e.printStackTrace();
                gestionarEquiposController.mostrarMensaje("Error", "No se pudieron guardar los cambios.");
            }
        }

        // Cerrar la ventana después de guardar los cambios
        Stage stage = (Stage) nombreEquipoField.getScene().getWindow();
        stage.close();
    }

    // Método para establecer el controlador de la vista principal
    public void setGestionarEquiposController(GestionarEquiposController controller) {
        this.gestionarEquiposController = controller;
    }
}