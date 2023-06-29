package com.example.p3proyectoparalela;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {
    void returnPartialResult(float[] numbersParallel) throws RemoteException;
}
