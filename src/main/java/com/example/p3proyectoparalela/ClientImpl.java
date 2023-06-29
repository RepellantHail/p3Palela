package com.example.p3proyectoparalela;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ClientImpl extends Application implements ClientInterface {
    private int clientNumber;
    private String clientStatus = "Esperando";
    private ServerInterface server;
    private Label lblClientNumber;
    private Label lblClientStatus;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        setupUI(stage);
    }

    public void setupUI(Stage stage) {
        lblClientNumber = new Label("Número de Cliente: " + clientNumber);
        lblClientStatus = new Label("Estado: " + clientStatus);

        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        vbox.getChildren().addAll(lblClientNumber, lblClientStatus);

        Scene scene = new Scene(vbox);
        stage.setScene(scene);
        stage.setTitle("P3 Proyecto");
        stage.setWidth(300);
        stage.setHeight(200);
        stage.show();
        connectToServer();
    }

    void connectToServer() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 9000);
            ServerInterface  remoteInterface  = (ServerInterface) registry.lookup("ServerP");
            server = remoteInterface;
            updateClientStatus("Conectado al servidor");
        } catch (Exception e) {
            updateClientStatus("Error en la conexión del servidor");
            System.out.println("Error al conectarse al servidor: " + e.getMessage());
        }
    }

    @Override
    public void returnPartialResult(float[] numbersParallel) throws RemoteException {
        // Implementación del método para recibir los resultados parciales del servidor RMI
        // Puedes agregar el código necesario aquí para manejar los resultados parciales
    }

    // Método para actualizar el estado del cliente en la interfaz de usuario
    private void updateClientStatus(String status) {
        clientStatus = status;
        updateLabel(lblClientStatus, "Estado: " + clientStatus);
    }

    // Método para actualizar una etiqueta de la interfaz de usuario en el hilo de la interfaz de usuario
    private void updateLabel(Label label, String text) {
        Platform.runLater(() -> label.setText(text));
    }

}
