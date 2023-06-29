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

import static javafx.application.Application.launch;

public class ClientImpl extends UnicastRemoteObject  implements ClientInterface {
    private int clientNumber = 0;
    private String clientStatus = "Esperando";
    private Label lblClientNumber = new Label();
    private Label lblClientStatus = new Label();

    public ClientImpl() throws RemoteException {
        super();
    }

    public static void main(String[] args) throws RemoteException {
        launch(ClientApp.class, args);
    }



    public void setupUI() {
        Platform.runLater(() ->{
            lblClientNumber = new Label("Número de Cliente: " + clientNumber);
            lblClientStatus = new Label("Estado: " + clientStatus);

            VBox vbox = new VBox(10);
            vbox.setAlignment(Pos.CENTER);
            vbox.setPadding(new Insets(20));
            vbox.getChildren().addAll(lblClientNumber, lblClientStatus);

            Scene scene = new Scene(vbox);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Cliente RMI");
            stage.setWidth(300);
            stage.setHeight(200);
            stage.show();
        });
    }

    void connectToServer() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 9000);
            ServerInterface  remoteInterface  = (ServerInterface) registry.lookup("ServerP");
            ServerInterface server = remoteInterface;
            server.registerClient(this);
            clientNumber = server.getClientCount();
            System.out.println("Conexion exitosa, cliente: "+ server.getClientCount());
            updateClientStatus("Conectado al servidor");
            updateLabel(lblClientNumber, "Número de Cliente: " + clientNumber);
            updateLabel(lblClientStatus, "Estado: Esperando");
        } catch (Exception e) {
            updateClientStatus("Error en la conexión del servidor");
            System.out.println("Error al conectarse al servidor: " + e.getMessage());
        }
    }

    @Override
    public void returnPartialResult(float[] numbersParallel) throws RemoteException {
        String result = "Cliente " + clientNumber + ": " + numbersParallel.length + " números ordenados parcialmente";
        if (lblClientStatus != null) {
            updateLabel(lblClientStatus, result);
        }
    }

    // Método para actualizar el estado del cliente en la interfaz de usuario
    private void updateClientStatus(String status) {
        clientStatus = status;
        updateLabel(lblClientStatus, "Estado: " + clientStatus + ", Número de clientes: " + clientNumber);
    }

    // Método para actualizar una etiqueta de la interfaz de usuario en el hilo de la interfaz de usuario
    private void updateLabel(Label label, String text) {
        Platform.runLater(() -> label.setText(text));
    }

}
