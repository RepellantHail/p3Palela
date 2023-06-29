package com.example.p3proyectoparalela;

import javafx.application.Application;
import javafx.stage.Stage;

import java.rmi.RemoteException;

public class ClientApp extends Application {
    @Override
    public void start(Stage primaryStage) throws RemoteException {
        ClientImpl client = new ClientImpl();
        client.connectToServer();
        client.setupUI();
    }

    public static void main(String[] args) {
        launch(args);
    }
}