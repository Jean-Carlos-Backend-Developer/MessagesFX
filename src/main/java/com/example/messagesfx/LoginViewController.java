package com.example.messagesfx;

import com.example.messagesfx.models.UserAux;
import com.example.messagesfx.responses.UserResponse;
import com.example.messagesfx.utils.*;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;

import java.io.IOException;

public class LoginViewController {
    @FXML
    private Label lblError;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtUser;

    @FXML
    void onLogginClick() {
        String user = txtUser.getText();
        String pass = txtPassword.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            MessageUtils.showError("Error", "Los campo de usuario y contraseña no pueden estar vacíos.");
        } else {
            String json = String.format("{\"name\": \"%s\", \"password\": \"%s\"}", user, pass);
            loginUsuario(json);
        }
    }

    public void loginUsuario(String json) {
        SolicitudAPI login = new SolicitudAPI("http://localhost:8080/login", json, "POST");
        login.start();
        login.setOnSucceeded(e -> {
            procesarRespuesta(login.getValue());
        });
        login.setOnFailed(e -> {
            Throwable exception = login.getException();
            if (exception != null) {
                MessageUtils.showError("Error", "Ha ocurrido un error: " + exception.getMessage());
            }
        });
    }

    public void procesarRespuesta(String respuesta) {
        Gson gson = new Gson();
        UserResponse response = gson.fromJson(respuesta, UserResponse.class);
        if (response.getOk()) {
            // Almacena el token en ServiceUtils
            ServiceUtils.setToken(response.getToken());

            //Obtener image, nombre y el id para futuras peticiones como cambio de imagen etc
            UserAux userAux = UserAux.getInstance();
            userAux.setNombreUser(response.getName());
            userAux.setImagenUser(new Image("http://localhost:8080/" + response.getImage()));
            userAux.setId(response.getId());

            //Abrir ventana de Mensajes
            try {
                MessagesFx.ventanaMensajes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            lblError.setVisible(true);
        }
    }

    @FXML
    void onRegisterClick(ActionEvent event) {
        try {
            MessagesFx.ventanaRegistro();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}