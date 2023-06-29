package com.example.p3proyectoparalela;

import javafx.scene.control.ProgressBar;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerImpl extends UnicastRemoteObject implements ServerInterface {
    public ServerImpl() throws RemoteException {
        // Constructor vacío necesario para la exportación del objeto remoto
    }

    @Override
    public void sortParallel(float[] numbersParallel, int tiempoHilo, ProgressBar progressBarP, int numberOfThreads, int numberOfCLients) throws RemoteException{

    }
}
