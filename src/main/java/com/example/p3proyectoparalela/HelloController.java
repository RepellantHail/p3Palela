package com.example.p3proyectoparalela;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextInputDialog;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Optional;
import java.util.Random;

import static com.example.p3proyectoparalela.BucketSort.*;

public class HelloController extends UnicastRemoteObject implements ServerInterface{
    @FXML
    private BarChart<String, Number> timeChart;
    private volatile boolean isSortingStopped = false;
    @FXML
    private ProgressBar progressBarS;
    @FXML
    private ProgressBar progressBarC;
    @FXML
    private ProgressBar progressBarP;
    @FXML
    private Label lblSecuencial;
    @FXML
    private Label lblConcurrente;
    @FXML
    private Label lblParalelo;
    @FXML
    private Label lblHilos;
    @FXML
    private Label lblElementos;
    @FXML
    private Label lblClientes;
    private ServerInterface server;
    private int numberOfThreads = 2;
    private float[] numbers;
    private float[] numbersConcurrent;
    private float[] numbersParallel;
    private boolean isSortingS = false;
    private boolean isSortingC = false;
    private boolean isSortingP = false;
    int numberOfElements = 100;
    int tiempoHilo = 1;
    int numberOfClients = 0;

    public HelloController() throws RemoteException {
        super();
        try {
            Registry registry = LocateRegistry.createRegistry(9000);
            registry.rebind("ServerP", this);
            System.out.println("Servidor RMI en ejecución...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ProgressBar getProgressBarS() {
        return progressBarS;
    }

    public ProgressBar getProgressBarC() {
        return progressBarC;
    }

    public ProgressBar getProgressBarP() {
        return progressBarP;
    }

    public void onStartButtonClick(ActionEvent actionEvent) {
        isSortingS = true;
        isSortingC = true;
        isSortingP = true;

//        Solicitar n Hilos
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Parámetros de Ejecución");
        dialog.setHeaderText("Ingrese los parámetros de ejecución:");
        dialog.setContentText("Número de hilos (H) y cantidad de elementos (N) separados por una coma:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(s -> {
            String[] params = s.split(",");
            if (params.length == 2) {
                try {
                    numberOfThreads = Integer.parseInt(params[0]);
                    numberOfElements = Integer.parseInt(params[1]);
                } catch (NumberFormatException e) {
                    numberOfThreads = 1; // Valor predeterminado si ocurre un error en la conversión
                    numberOfElements = 100; // Valor predeterminado si ocurre un error en la conversión
                }
            }
        });

        // Muestra el número de hilos en la etiqueta correspondiente
        updateLabel(lblHilos, "N° Hilos: " + Integer.toString(numberOfThreads));
        updateLabel(lblElementos, "N° Elementos: " + Integer.toString(numberOfElements));
        try {
            numberOfClients = server.getClientCount();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        updateLabel(lblClientes, "N° Clientes: " + Integer.toString(numberOfClients));
        numbers = generateRandomNumbers(numberOfElements);
        numbersConcurrent = numbers.clone();
        numbersParallel = numbers.clone();

        Thread counterThreadS = new Thread(() -> {
            int count = 0;
            while (isSortingS && !isSortingStopped) {
                count++;
                updateLabel(lblSecuencial, Integer.toString(count));
                try {
                    Thread.sleep(1000);//Tiempo de actualizacion label
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread counterThreadC = new Thread(() -> {
            int countC = 0;
            while (isSortingC && !isSortingStopped) {
                countC++;
                updateLabel(lblConcurrente, Integer.toString(countC));
                try {
                    Thread.sleep(1000);//Tiempo de actualizacion label
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread counterThreadP = new Thread(() -> {
            int countC = 0;
            while (isSortingP && !isSortingStopped) {
                countC++;
                updateLabel(lblParalelo, Integer.toString(countC));
                try {
                    Thread.sleep(1000);//Tiempo de actualizacion label
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        counterThreadS.start();
        counterThreadC.start();
        counterThreadP.start();

        Thread sortingThread = new Thread(() -> {
            long startTime = System.currentTimeMillis();
            bucketSort(numbers, numbers.length, tiempoHilo, getProgressBarS());
            long endTime = System.currentTimeMillis();
            isSortingS = false;
            for (float n : numbers) {
                System.out.println(n);
            }
            System.out.println("Secuencial Termino");

            // Actualizar el gráfico con el tiempo de procesamiento secuencial
            updateChart("Secuencial", endTime - startTime);
        });
        sortingThread.start();
        Thread sortingThreadConcurrent = new Thread(() -> {
            long startTime = System.currentTimeMillis();
            bucketSortC(numbersConcurrent, numbersConcurrent.length, tiempoHilo, getProgressBarC(), numberOfThreads);
            long endTime = System.currentTimeMillis();
            isSortingC = false;
            Platform.runLater(() -> {
                for (float n : numbersConcurrent) {
                    System.out.println(n);
                }
                System.out.println("Concurrente terminó");

                // Actualizar el gráfico con el tiempo de procesamiento concurrente
                updateChart("Concurrente", endTime - startTime);
            });
        });
        sortingThreadConcurrent.start();
        Thread sortingThreadParallel = new Thread(() -> {
            long startTime = System.currentTimeMillis();
            try {
                server.sortParallel(numbersParallel, tiempoHilo, getProgressBarP(), numberOfThreads, numberOfClients);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            long endTime = System.currentTimeMillis();
            isSortingP = false;
            Platform.runLater(() -> {
                System.out.println("Concurrente terminó");
                updateChart("¨Paralelo", endTime - startTime);
            });
        });
        sortingThreadParallel.start();
    }

    public void onStopButtonClick(ActionEvent actionEvent) {
        isSortingStopped = true;
    }

    public void onResumeButtonClick(ActionEvent actionEvent) {
        isSortingStopped = false;
    }

    private void updateLabel(Label label, String text) {
        Platform.runLater(() -> label.setText(text));
    }

    private float[] generateRandomNumbers(int numberOfElements) {
        float[] arr = new float[numberOfElements];
        Random random = new Random();
        for (int i = 0; i < numberOfElements; i++) {
            arr[i] = random.nextFloat();
        }
        return arr;
    }

    private void updateChart(String processingType, long time) {
        Platform.runLater(() -> {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.getData().add(new XYChart.Data<>(processingType, time));
            timeChart.getData().add(series);
        });
    }


    @Override
    public void sortParallel(float[] numbersParallel, int tiempoHilo, ProgressBar progressBarP, int numberOfThreads, int numberOfCLients) throws RemoteException {

    }

    @Override
    public int getClientCount() throws RemoteException {
        return 0;
    }

    @Override
    public void registerClient(ClientInterface client) throws RemoteException {

    }
}