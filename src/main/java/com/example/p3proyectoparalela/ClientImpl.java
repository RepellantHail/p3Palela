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
import java.rmi.server.UnicastRemoteObject;

public class ClientImpl extends Application implements ClientInterface {
    private int clientNumber;
    private String clientStatus;
    private ServerInterface server;

    private Label lblClientNumber;
    private Label lblClientStatus;

    public ClientImpl(int clientNumber) {
        this.clientNumber = clientNumber;
        this.clientStatus = "Esperando";
    }

    @Override
    public void start(Stage primaryStage) {
        lblClientNumber = new Label("Número de Cliente: " + clientNumber);
        lblClientStatus = new Label("Estado: " + clientStatus);

        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        vbox.getChildren().addAll(lblClientNumber, lblClientStatus);

        Scene scene = new Scene(vbox);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Cliente RMI");
        primaryStage.show();

        initializeServer();
    }

    private void initializeServer() {
        try {
            server = (ServerInterface) Naming.lookup("//localhost/Server");
            server.registerClient(this);
            System.out.println("Cliente " + clientNumber + " registrado en el servidor RMI.");
        } catch (Exception e) {
            e.printStackTrace();
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

    public static void main(String[] args) {
        int clientNumber = Integer.parseInt(args[0]); // Obtener el número de cliente del argumento de línea de comandos
        ClientImpl client = new ClientImpl(clientNumber);
        client.launch(args);
    }
}
