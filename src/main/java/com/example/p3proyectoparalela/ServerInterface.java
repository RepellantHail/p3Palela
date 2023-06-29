package com.example.p3proyectoparalela;

import javafx.scene.control.ProgressBar;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
    void sortParallel(float[] numbersParallel, int tiempoHilo, ProgressBar progressBarP, int numberOfThreads,int numberOfCLients) throws RemoteException;
    int getClientCount() throws RemoteException;
    void registerClient(ClientInterface client) throws RemoteException;
}