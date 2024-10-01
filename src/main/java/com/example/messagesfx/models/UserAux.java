package com.example.messagesfx.models;

import javafx.scene.image.Image;

import java.util.List;

public class UserAux {
    public String getNombreUser() {
        return nombreUser;
    }

    public void setNombreUser(String nombreUser) {
        this.nombreUser = nombreUser;
    }

    public Image getImagenUser() {
        return imagenUser;
    }

    public void setImagenUser(Image imagenUser) {
        this.imagenUser = imagenUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Message> getMensajes() {
        return mensajes;
    }

    public void setMensajes(List<Message> mensajes) {
        this.mensajes = mensajes;
    }

    public static UserAux getINSTANCE() {
        return INSTANCE;
    }

    public static void setINSTANCE(UserAux INSTANCE) {
        UserAux.INSTANCE = INSTANCE;
    }

    private String nombreUser;
    private Image imagenUser;
    private String id;
    private List<Message> mensajes;

    private static UserAux INSTANCE = new UserAux();

    public static UserAux getInstance() {
        return INSTANCE;
    }
}
