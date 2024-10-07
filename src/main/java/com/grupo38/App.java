package com.grupo38;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

import com.grupo38.config.DatabaseInitializer;
import com.grupo38.config.DatabaseSeeder;
import com.grupo38.config.HibernateUtil;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        try {
            primaryStage = stage;

            // Inicializar la base de datos
            DatabaseInitializer initializer = DatabaseInitializer.getInstance();
            initializer.initializeDatabase();

            // Inicializar Hibernate
            HibernateUtil.getSessionFactory();

            // Seeder
            DatabaseSeeder.seedDatabase();  // Llenar la base de datos con datos de prueba

            // Cargar la vista principal
            scene = new Scene(loadFXML("MainView"), 1920, 1080);
            stage.setScene(scene);
            stage.setTitle("Gestión de Sucursales");
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/grupo38/view/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();  // Lanza la aplicación JavaFX
        HibernateUtil.shutdown();  // Cierra la sesión de Hibernate al finalizar
    }
}