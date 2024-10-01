package com.example.messagesfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MessagesFx extends Application {
    public static Stage currentStage; //Ventana actual

    @Override
    //Método para la ventana de Login
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MessagesFx.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setScene(scene);
        primaryStage.show();
        currentStage = primaryStage;
    }

    public static void main(String[] args) {
        launch();
    }

    //Metodo para abrir la ventana de Login
    public static void ventanaLogin() throws IOException {
        if (currentStage != null) {
            currentStage.close();
        }
        FXMLLoader fxmlLoader = new FXMLLoader(MessagesFx.class.getResource("login-view.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
        currentStage = stage;
    }

    //Método para abrir la ventana de Registro
    public static void ventanaRegistro() throws IOException {
        if (currentStage != null) {
            currentStage.close();
        }
        FXMLLoader fxmlLoader = new FXMLLoader(MessagesFx.class.getResource("register-view.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Register");
        stage.setScene(scene);
        stage.show();
        currentStage = stage;
    }

    //Método para abrir la ventana Mensajes
    public static void ventanaMensajes() throws IOException {
        if (currentStage != null) {
            currentStage.close();
        }
        FXMLLoader fxmlLoader = new FXMLLoader(MessagesFx.class.getResource("messages-view.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Messages");
        stage.setScene(scene);
        stage.show();
        currentStage = stage;
    }
}
