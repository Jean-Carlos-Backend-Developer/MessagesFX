package com.example.messagesfx.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class Base64Image {
    public static String imageToBase64(File imagen) {
        String data = "";
        try {
            byte[] imageBytes = Files.readAllBytes(Paths.get(imagen.getAbsolutePath()));
            data = Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e){
            System.err.println("Error procesando la imagen.");
            e.printStackTrace();
        }
        return data;
    }
}