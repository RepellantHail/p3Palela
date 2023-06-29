package com.example.p3proyectoparalela;

import javafx.scene.control.ProgressBar;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ServerImpl extends UnicastRemoteObject implements ServerInterface {
    private static final int RMI_PORT = 9000;
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
        clients.add(client);
        numberOfClients = clients.size(); // Actualizar el contador de clientes
        System.out.println("Cliente registrado. Total de clientes: " + numberOfClients);
    }

    public static void main(String[] args) {
        try {
            ServerImpl server = new ServerImpl();
            Registry registry = LocateRegistry.createRegistry(RMI_PORT);
            registry.rebind("ServerP", server);
            System.out.println("Servidor RMI en ejecución...");
        } catch (RemoteException e) {
            System.err.println("Error al iniciar el servidor RMI: " + e.getMessage());
        }
    }
}
