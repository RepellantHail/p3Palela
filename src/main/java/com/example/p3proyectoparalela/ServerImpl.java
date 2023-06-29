package com.example.p3proyectoparalela;

import javafx.scene.control.ProgressBar;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ServerImpl extends UnicastRemoteObject implements ServerInterface {
    private int numberOfClients = 0;
    private List<ClientInterface> clients;
    public ServerImpl() throws RemoteException {
        clients = new ArrayList<>();
    }

    @Override
    public void sortParallel(float[] numbersParallel, int tiempoHilo, ProgressBar progressBarP, int numberOfThreads, int numberOfCLients) throws RemoteException{

    }

    @Override
    public int getClientCount() throws RemoteException {
        return numberOfClients;
    }

    public void registerClient(ClientInterface client) throws RemoteException {
        clients.add(client);
        numberOfClients = clients.size(); // Actualizar el contador de clientes
        System.out.println("Cliente registrado. Total de clientes: " + numberOfClients);
    }

    public static void main(String[] args) {
        try {
            ServerImpl server = new ServerImpl();
            System.out.println("El servidor RMI ha iniciado correctamente.");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
