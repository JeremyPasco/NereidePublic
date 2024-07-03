package org.example.multithreading.re01;

import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(2);

        Callable<Integer> fn = () -> {
            var val = 0;
            for (int i = 0; i < 500; i++) {
                val++;
            }

            return val;
        };

        Future<Integer> result1 = service.submit(fn);
        Future<Integer> result2 = service.submit(fn);

        try {
            var total = result1.get() + result2.get();
            System.out.println(total);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
