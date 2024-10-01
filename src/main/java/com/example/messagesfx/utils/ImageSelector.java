package com.example.messagesfx.utils;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class ImageSelector {
    private FileChooser fileChooser;

    public ImageSelector() {
        this.fileChooser = new FileChooser();
        this.fileChooser.setTitle("Selecciona la nueva imagen...");
        this.fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.webp"));
    }

    public File seleccionarImagen(Window window) {
        return this.fileChooser.showOpenDialog(window);
    }
}