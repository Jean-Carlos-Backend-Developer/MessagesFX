package com.example.messagesfx;

import com.example.messagesfx.models.User;
import com.example.messagesfx.responses.UserResponse;
import com.example.messagesfx.utils.*;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class RegisterViewController {

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnRegister;

    @FXML
    private Button btnSelectImage;

    @FXML
    private ImageView imageView;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private PasswordField txtPaswordRep;

    @FXML
    private TextField txtUser;

    public String name;
    public String password;
    public String image;

    @FXML
    void onChooseImageClick(ActionEvent event) {
        ImageSelector imageSelector = new ImageSelector();
        File selectedImage = imageSelector.seleccionarImagen(null);
        if (selectedImage != null) {
            try {
                image = Base64Image.imageToBase64(selectedImage);
                imageView.setImage(new Image("file:" + selectedImage.getAbsolutePath()));
            } catch (Exception e) {
                MessageUtils.showError("Error", "Error convirtiendo la imagen a base64.");
                e.printStackTrace();
            }
        }
    }

    @FXML
    void onButtonRegisterClick(ActionEvent event) {
        try {
            name = txtUser.getText();
            password = txtPassword.getText();
            String passwordRep = txtPaswordRep.getText();

            if (name.isEmpty() || password.isEmpty()) {
                MessageUtils.showError("Error", "Los campos de usuario y contraseña no pueden estar vacíos.");
            } else if (!password.equals(passwordRep)) {
                MessageUtils.showError("Error", "Las contraseñas no coinciden.");
            } else if (image == null || image.isEmpty()) {
                MessageUtils.showError("Error", "Debes seleccionar una imagen.");
            } else {
                String json = String.format("{\"name\": \"%s\", \"password\": \"%s\", \"image\": \"%s\"}", name, password, image);
                registerUser(json);
            }
        } catch (Exception e) {
            MessageUtils.showError("Error", "Error al registar el usuario.");
        }
    }

    public void registerUser(String json) {
        SolicitudAPI register = new SolicitudAPI("http://localhost:8080/register", json, "POST");
        register.start();
        register.setOnSucceeded(e -> {
            procesarRespuesta(register.getValue());
        });
        register.setOnFailed(e -> {
            Throwable exception = register.getException();
            if (exception != null) {
                MessageUtils.showError("Error", "Ha ocurrido un error: " + exception.getMessage());
            }
        });
    }

    public void procesarRespuesta(String respuesta) {
        Gson gson = new Gson();
        UserResponse response = gson.fromJson(respuesta, UserResponse.class);
        if (response.getOk()) {
            //Crea un Alert con un botón OK
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Registro completo");
            alert.setTitle("Te has registrado correctamente");
            alert.setContentText("Serás redirigido a la página de inicio de sesión.");

            ButtonType buttonTypeOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(buttonTypeOk);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeOk) {
                //Redirige a la página de inicio de sesión
                try {
                    MessagesFx.ventanaLogin();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            MessageUtils.showMessage("Error", response.getError());
        }
    }

    @FXML
    void onButtonCancelClick(ActionEvent event) throws IOException {
        try {
            MessagesFx.ventanaLogin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
