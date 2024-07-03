package org.example.multithreading.re3;

import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class MaClasse {
    public static void main(String[] args) {
//        try(var executor = Executors.newFixedThreadPool(100_000)) {
//            IntStream.range(0, 100_000).forEach(i -> {
//                executor.submit(() -> {
//                    Thread.sleep(1000);
//                    return i;
//                });
//            });
//        }

        try(var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 100_000).forEach(i -> {
                executor.submit(() -> {
                    Thread.sleep(1000);
                    return i;
                });
            });
        }
    }
}
