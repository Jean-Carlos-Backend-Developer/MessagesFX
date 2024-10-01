package com.example.messagesfx.utils;

import javafx.scene.control.Alert;

public class MessageUtils {
    public static void showError(String header,String message) {
        Alert dialog = new Alert(Alert.AlertType.ERROR);
        dialog.setTitle("Error");
        dialog.setHeaderText(header);
        dialog.setContentText(message);
        dialog.showAndWait();
    }

    public static void showMessage(String header,String message) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setHeaderText(header);
        dialog.setContentText(message);
        dialog.showAndWait();
    }
}
