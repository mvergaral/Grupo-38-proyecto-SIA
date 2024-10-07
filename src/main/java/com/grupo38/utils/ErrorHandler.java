package com.grupo38.utils;

import javafx.animation.PauseTransition;
import javafx.scene.control.Tooltip;
import javafx.scene.Node;
import javafx.util.Duration;

public class ErrorHandler {

    private Tooltip tooltip;

    public ErrorHandler() {
        this.tooltip = new Tooltip();
    }

    // Método para mostrar un error y desaparecer después de unos segundos
    public void showError(Node node, String message) {
        tooltip.setText(message);
        tooltip.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: 14px;");
        tooltip.show(node, node.getScene().getWindow().getX() + node.getLayoutX(), node.getScene().getWindow().getY() + node.getLayoutY());

        // Pausa de 3 segundos antes de ocultar el Tooltip
        PauseTransition delay = new PauseTransition(Duration.seconds(15));
        delay.setOnFinished(event -> tooltip.hide());
        delay.play();
    }

    // Método para mostrar un error desde una excepción
    public void showError(Node node, Exception e) {
        showError(node, e.getMessage());
    }
}