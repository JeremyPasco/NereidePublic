package org.example.ex7.rep2;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MultiThreading {

    public static void main(String[] args) {
        int nbThreads = 16;
        int nbTasks = 1000;

        long startTime = System.currentTimeMillis();
//        try (ExecutorService executor = Executors.newFixedThreadPool(nbThreads)) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            CompletableFuture<?>[] futures = new CompletableFuture[nbTasks];

            IntStream.range(0, nbTasks).forEach(i ->
                    futures[i] = CompletableFuture.supplyAsync(BigDataOps::blackBox, executor));

            CompletableFuture.allOf(futures).join();
            String combined = Stream.of(futures)
                    .map(f -> {
                        try {
                            return String.valueOf(f.get());
                        } catch (InterruptedException | ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.joining());

            System.out.println(combined);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime-startTime) + "ms");
    }
}
