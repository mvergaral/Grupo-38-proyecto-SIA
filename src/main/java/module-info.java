module com.sia {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.sql;
    requires java.naming; // Necesario para javax.naming.Referenceable

    requires org.hibernate.orm.core; // Hibernate core
    requires org.postgresql.jdbc; // Driver PostgreSQL

    opens com.grupo38 to javafx.fxml, org.hibernate.orm.core;
    exports com.grupo38;
}