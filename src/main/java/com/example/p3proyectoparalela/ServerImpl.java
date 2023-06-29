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
        super();
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
        clients.add(client);// Actualizar el contador de clientes
        numberOfClients = clients.size();
        System.out.println("Cliente registrado. Total de clientes: " + numberOfClients);
    }

}
