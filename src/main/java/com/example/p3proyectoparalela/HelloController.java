package com.example.p3proyectoparalela;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;
import java.util.Random;

import static com.example.p3proyectoparalela.BucketSort.bucketSort;
import static com.example.p3proyectoparalela.BucketSort.bucketSortP;

public class HelloController {
    @FXML
    private BarChart<String, Number> timeChart;
    private volatile boolean isSortingStopped = false;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private ProgressBar progressBarP;
    @FXML
    private Label lblSecuencial;
    @FXML
    private Label lblConcurrente;
    @FXML
    private Label lblHilos;
    @FXML
    private Label lblElementos;
    private int numberOfThreads = 2;
    private float[] numbers;
    private float[] numbersParallel;
    private boolean isSortingS = false;
    private boolean isSortingP = false;
    int numberOfElements = 100;
    int tiempoHilo = 1;
    public ProgressBar getProgressBar() {
        return progressBar;
    }
    public ProgressBar getProgressBarP() {
        return progressBarP;
    }
    public void onStartButtonClick(ActionEvent actionEvent) {
        isSortingS = true;
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
        updateLabel(lblHilos, "N° Hilos: "+Integer.toString(numberOfThreads));
        updateLabel(lblElementos, "N° Elementos: "+Integer.toString(numberOfElements));
        numbers = generateRandomNumbers(numberOfElements);
        numbersParallel = numbers.clone();

        Thread counterThread = new Thread(() -> {
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

        Thread counterThreadP = new Thread(() -> {
            int countP = 0;
            while (isSortingP && !isSortingStopped) {
                countP++;
                updateLabel(lblConcurrente, Integer.toString(countP));
                try {
                    Thread.sleep(1000);//Tiempo de actualizacion label
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        counterThread.start();
        counterThreadP.start();

        Thread sortingThread = new Thread(() -> {
            long startTime = System.currentTimeMillis();
            bucketSort(numbers, numbers.length, tiempoHilo, getProgressBar());
            long endTime = System.currentTimeMillis();
            isSortingS = false;
            for (float n : numbers){
                System.out.println(n);
            }
            System.out.println("Secuencial Termino");

            // Actualizar el gráfico con el tiempo de procesamiento secuencial
            updateChart("Secuencial", endTime - startTime);
        });
        sortingThread.start();
        //Paralelo
        Thread sortingThreadParallel = new Thread(() -> {
            long startTime = System.currentTimeMillis();
            bucketSortP(numbersParallel, numbersParallel.length, tiempoHilo, getProgressBarP(),numberOfThreads);
            long endTime = System.currentTimeMillis();
            isSortingP = false;
            Platform.runLater(() -> {
                for (float n : numbersParallel) {
                    System.out.println(n);
                }
                System.out.println("Concurrente terminó");

                // Actualizar el gráfico con el tiempo de procesamiento concurrente
                updateChart("Concurrente", endTime - startTime);
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



}