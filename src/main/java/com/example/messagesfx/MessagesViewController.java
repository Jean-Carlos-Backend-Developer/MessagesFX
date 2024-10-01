package com.example.messagesfx;

import com.example.messagesfx.models.Message;
import com.example.messagesfx.models.User;
import com.example.messagesfx.models.UserAux;
import com.example.messagesfx.responses.MessageResponse;
import com.example.messagesfx.responses.UserResponse;
import com.example.messagesfx.utils.*;
import com.google.gson.Gson;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MessagesViewController {
    @FXML
    private ImageView imageView;

    @FXML
    private ImageView imageViewMens;

    @FXML
    private Label lblNombreUser;

    @FXML
    private Button btnSendMessage;

    @FXML
    private TableView<Message> tableMensajes;

    @FXML
    private TableColumn<Message, String> colMessage;

    @FXML
    private TableColumn<Message, ImageView> colMessageImage;

    @FXML
    private TableColumn<Message, String> colSent;

    @FXML
    private TableView<User> tableUsuarios;

    @FXML
    private TableColumn<User, ImageView> colUserImage;

    @FXML
    private TableColumn<User, String> colUserName;

    @FXML
    private Button btnDeleteMessage;

    @FXML
    private TextField txtNewMessage;

    private List<Message> messageList;
    private String idUsuario;
    private String idUsuarioMsg;
    private String messageId;
    private File image = null;

    @FXML
    public void initialize() {
        recuperarDatos();

        //Tabla de mensajes
        colMessage.setCellValueFactory(new PropertyValueFactory<Message, String>("message"));
        colMessageImage.setCellValueFactory(new PropertyValueFactory<Message, ImageView>("imageView"));
        //Codigo para formatear la fecha
        colSent.setCellValueFactory(cellData -> {
            SimpleStringProperty property = new SimpleStringProperty();
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            SimpleDateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy");
            originalFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                Date date = originalFormat.parse(cellData.getValue().getSent());
                property.setValue(targetFormat.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return property;
        });

        //Tabla de users
        colUserImage.setCellValueFactory(new PropertyValueFactory<User, ImageView>("imageView"));
        colUserName.setCellValueFactory(new PropertyValueFactory<User, String>("name"));

        //Llamada a funciones para añadir datos a las tablas
        addDatosUsuarios();
        addDatosMensajes();

        //Añadir un ChangeListener a la tabla de mensajes
        tableMensajes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                //Activa el botón de borrar
                btnDeleteMessage.setDisable(false);
                //Obtiene el id del mensaje para poder borrarlo de la lista
                messageId = newSelection.get_id();
            }
        });

        //Añadir un ChangeListener a la tabla de usuarios
        tableUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                //Obtiene el id del usuario para enviarle el mensaje
                idUsuarioMsg = newSelection.get_id();
            }
        });

        //Aañadir un ChangeListener para activar o desactivar boton de enviar mensaje
        txtNewMessage.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                btnSendMessage.setDisable(false); //Activa el botón si el TextField no está vacío
            } else {
                btnSendMessage.setDisable(true); //Desactiva el botón si el TextField está vacío
            }
        });

    }

    //Recuperar los datos gracias a mi clase auxiliar
    public void recuperarDatos() {
        UserAux userAux = UserAux.getInstance();
        lblNombreUser.setText(userAux.getNombreUser());
        imageView.setImage(userAux.getImagenUser());
        idUsuario = userAux.getId();
    }

    //Cambiar imagen de usuario
    @FXML
    void onBtnChangeImageClick(ActionEvent event) {
        ImageSelector imageSelector = new ImageSelector();
        File selectedImage = imageSelector.seleccionarImagen(null);
        if (selectedImage != null) {
            //Llamada a funcion que cambia imagen
            cambiarImagenUsuario(selectedImage);
        } else {
            MessageUtils.showError("Error", "Error seleccionado la imagen .");
        }
    }

    @FXML
    void onBtnRefreshClick(ActionEvent event) {
        addDatosMensajes();
    }

    private void cambiarImagenUsuario(File image) {
        //Convertir la imagen a Base64
        String imagenBase64 = Base64Image.imageToBase64(image);
        String json = String.format("{\"image\": \"%s\"}", imagenBase64);
        SolicitudAPI cambiarImagen = new SolicitudAPI("http://localhost:8080/users/" + idUsuario, json, "PUT");
        cambiarImagen.start();
        cambiarImagen.setOnSucceeded(e -> {
            Gson gson = new Gson();
            UserResponse response = gson.fromJson(cambiarImagen.getValue(), UserResponse.class);
            if (response.getOk()) {
                imageView.setImage(new Image("file:" + image.getAbsolutePath()));
            } else {
                MessageUtils.showError("Error", response.getError());
            }
        });
        cambiarImagen.setOnFailed(e -> {
            Throwable exception = cambiarImagen.getException();
            if (exception != null) {
                MessageUtils.showError("Error", exception.getMessage());
                exception.printStackTrace();
            }
        });
    }

    @FXML
    void onBtnDeleteClick(ActionEvent event) {
        SolicitudAPI borrarMensaje = new SolicitudAPI("http://localhost:8080/messages/" + messageId, null, "DELETE");
        borrarMensaje.start();
        borrarMensaje.setOnSucceeded(e -> {
            Gson gson = new Gson();
            MessageResponse response = gson.fromJson(borrarMensaje.getValue(), MessageResponse.class);
            if (response.getOk()) {
                MessageUtils.showMessage("Info", "Mensaje borrado correctamente.");

                //Encuentra el mensaje en la lista
                Message mensajeBorrar = null;
                for (Message message : messageList) {
                    if (message.get_id().equals(messageId)) {
                        mensajeBorrar = message;
                        break;
                    }
                }

                //Elimina el mensaje de la lista y actualiza la tabla
                if (mensajeBorrar != null) {
                    messageList.remove(mensajeBorrar);
                    tableMensajes.getItems().setAll(messageList);
                }

            } else {
                MessageUtils.showError("Error", response.getError());
            }
        });
        borrarMensaje.setOnFailed(e -> {
            Throwable exception = borrarMensaje.getException();
            if (exception != null) {
                MessageUtils.showError("Error", exception.getMessage());
                exception.printStackTrace();
            }
        });
    }

    @FXML
    void onBtnSendMessageClick(ActionEvent event) {
        if (idUsuarioMsg != null || !txtNewMessage.getText().isEmpty()) {
            if (imageViewMens.getImage() != null && this.image != null) {
                nuevoMensaje(idUsuarioMsg, txtNewMessage.getText(), this.image);
            } else {
                nuevoMensaje(idUsuarioMsg, txtNewMessage.getText());
            }
        }
    }

    //Mensaje con imagen
    private void nuevoMensaje(String id, String mensaje, File image) {
        //Convertir imagen a base64
        String imageData = Base64Image.imageToBase64(image);
        //Coger fecha de envio
        Instant fecha = Instant.now();
        String fechaString = fecha.toString();
        String json = String.format("{\"message\": \"%s\", \"image\": \"%s\", \"sent\": \"%s\"}", mensaje, imageData, fechaString);
        System.out.println(json);
        SolicitudAPI nuevoMensaje = new SolicitudAPI("http://localhost:8080/messages/" + id, json, "POST");
        System.out.println(nuevoMensaje);
        nuevoMensaje.start();
        nuevoMensaje.setOnSucceeded(e -> {
            Gson gson = new Gson();
            MessageResponse response = gson.fromJson(nuevoMensaje.getValue(), MessageResponse.class);
            if (response.getOk()) {
                MessageUtils.showMessage("Info", "Mensaje enviado correctamente.");
                txtNewMessage.clear();
                imageViewMens.setImage(null);
                this.image = null;
            } else {
                MessageUtils.showError("Error", "Error enviado el mensaje.");
            }
        });
        nuevoMensaje.setOnFailed(e -> {
            Throwable exception = nuevoMensaje.getException();
            if(exception != null){
                System.out.println(exception.getMessage());
                exception.printStackTrace();
            }
        });
    }

    //Mensaje sin imagen
    private void nuevoMensaje(String id, String mensaje) {
        //Coger fecha de envio
        Instant fecha = Instant.now();
        String fechaString = fecha.toString();
        String json = String.format("{\"message\": \"%s\", \"sent\": \"%s\"}", mensaje, fechaString);
        SolicitudAPI nuevoMensaje = new SolicitudAPI("http://localhost:8080/messages/" + id, json, "POST");
        System.out.println(nuevoMensaje);
        nuevoMensaje.start();
        nuevoMensaje.setOnSucceeded(e -> {
            Gson gson = new Gson();
            MessageResponse response = gson.fromJson(nuevoMensaje.getValue(), MessageResponse.class);
            if (response.getOk()) {
                MessageUtils.showMessage("Info", "Mensaje enviado correctamente.");
                txtNewMessage.clear();
                imageViewMens.setImage(null);
            } else {
                MessageUtils.showError("Error", "Error enviado el mensaje.");
            }
        });
        nuevoMensaje.setOnFailed(e -> {
            Throwable exception = nuevoMensaje.getException();
            if(exception != null){
                System.out.println(exception.getMessage());
                exception.printStackTrace();
            }
        });
    }

    @FXML
    void onbtnSelectImageClick(ActionEvent event) {
        ImageSelector imageSelector = new ImageSelector();
        this.image = imageSelector.seleccionarImagen(null);
        if (image != null) {
            imageViewMens.setImage(new Image("file:" + this.image.getAbsolutePath()));
        } else {
            MessageUtils.showError("Error", "Error seleccionado la imagen .");
        }
    }

    public void addDatosMensajes() {
        //Crea una instancia del servicio de mensajes
        SolicitudAPI getMensajes = new SolicitudAPI("http://localhost:8080/messages", null, "GET");
        getMensajes.start();
        getMensajes.setOnSucceeded(e -> {
            Gson gson = new Gson();
            MessageResponse response = gson.fromJson(getMensajes.getValue(), MessageResponse.class);
            if (response.getOk()) {
                tableMensajes.getItems().clear();
                tableMensajes.getItems().addAll(response.getMessages());
                messageList = response.getMessages();
            } else {

                System.err.println("Error obteniendo los mensajes: " + response.getError());
            }
        });
        //Define lo que sucede si el servicio falla
        getMensajes.setOnFailed(e -> {
            Throwable exception = getMensajes.getException();
            System.err.println("Error: " + exception.getMessage());
            exception.printStackTrace();
        });
    }

    public void addDatosUsuarios() {
        SolicitudAPI getUsuarios = new SolicitudAPI("http://localhost:8080/users", null, "GET");
        getUsuarios.start();
        getUsuarios.setOnSucceeded(e -> {
            Gson gson = new Gson();
            UserResponse response = gson.fromJson(getUsuarios.getValue(), UserResponse.class);
            if (response.getOk()) {
                tableUsuarios.getItems().clear();
                tableUsuarios.getItems().addAll(response.getUsers());
            } else {
                System.err.println("Error obteniendo los usuarios: " + response.getError());
            }
        });
        getUsuarios.setOnFailed(e -> {
            Throwable exception = getUsuarios.getException();
            if (exception != null) {
                System.out.println("Error" + exception.getMessage());
                exception.printStackTrace();
            }
        });
    }
}
