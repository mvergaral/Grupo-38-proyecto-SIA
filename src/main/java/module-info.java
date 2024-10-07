module com.sia {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.sql;
    requires java.naming; // Necesario para javax.naming.Referenceable
    requires jakarta.persistence;

    requires org.hibernate.orm.core; // Hibernate core
    requires org.postgresql.jdbc; // Driver PostgreSQL

    opens com.grupo38 to javafx.fxml, org.hibernate.orm.core;
    exports com.grupo38;

    opens com.grupo38.model to org.hibernate.orm.core; // Asegúrate de abrir el paquete para Hibernate
    exports com.grupo38.model;

    // Abre el paquete de controladores para JavaFX
    opens com.grupo38.controller to javafx.fxml;

    // Exporta el paquete de controladores
    exports com.grupo38.controller;
}