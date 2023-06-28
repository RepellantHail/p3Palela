package com.example.p3proyectoparalela;

import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import java.util.*;
import java.util.Collections;

public class BucketSort {
    public static void bucketSort(float arr[], int n, int tiempo, ProgressBar progressBar) {
        if (n <= 0)
            return;

        @SuppressWarnings("unchecked")
        Vector<Float>[] buckets = new Vector[n];

        for (int i = 0; i < n; i++) {
            buckets[i] = new Vector<Float>();
        }

        for (int i = 0; i < n; i++) {
            int idx = (int)(arr[i] * n);
            if (idx == n) {
                idx--;
            }
            buckets[idx].add(arr[i]);
        }

        for (int i = 0; i < n; i++) {
            Collections.sort(buckets[i]);
        }

        int index = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < buckets[i].size(); j++) {
                try {
                    Thread.sleep(tiempo); // Pausa de 1 segundo
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                arr[index++] = buckets[i].get(j);
            }
            double progress = (double) (i + 1) / n;
            Platform.runLater(() -> progressBar.setProgress(progress));
        }
    }

    public static void bucketSortP(float arr[], int n, int tiempo, ProgressBar progressBar,int numThreads) {
        if (n <= 0)
            return;
        Thread[] threads = new Thread[numThreads]; // Arreglo de hilos
        int elementsPerThread = n / numThreads; // Elementos por hilo
        @SuppressWarnings("unchecked")
        Vector<Float>[] buckets = new Vector[n];

        for (int i = 0; i < n; i++) {
            buckets[i] = new Vector<Float>();
        }

        for (int i = 0; i < n; i++) {
            int idx = (int)(arr[i] * n);
            if (idx == n) {
                idx--;
            }
            buckets[idx].add(arr[i]);
        }

        int[] finalIndex = { 0 }; // Variable final adicional

        for (int i = 0; i < numThreads; i++) {
            int start = i * elementsPerThread;
            int end = (i == numThreads - 1) ? n : start + elementsPerThread;

            threads[i] = new Thread(() -> {
                for (int j = start; j < end; j++) {
                    for (int k = 0; k < buckets[j].size(); k++) {
                        try {
                            Thread.sleep(tiempo); // Pausa de 1 segundo
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        int currentIndex;
                        synchronized (finalIndex) { // Sincronizar el acceso a finalIndex
                            currentIndex = finalIndex[0]++; // Utilizar variable final adicional
                        }
                        arr[currentIndex] = buckets[j].get(k);
                        double progress = (double) (currentIndex + 1) / n;
                        Platform.runLater(() -> progressBar.setProgress(progress));
                    }
                }
            });
            threads[i].start();
        }

        // Esperar a que todos los hilos terminen
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Concatenar los elementos ordenados en el arreglo principal
        int index = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < buckets[i].size(); j++) {
                arr[index++] = buckets[i].get(j);
            }
        }

        double progress = 1.0;
        Platform.runLater(() -> progressBar.setProgress(progress));
    }



}