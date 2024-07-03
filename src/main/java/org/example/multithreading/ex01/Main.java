package org.example.multithreading.ex01;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Count count = new Count();

        for (int i = 0; i < 1000; i++) {
            service.execute(count::addOne);
        }

        try {
            service.awaitTermination(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println(count.val);
        service.shutdown();
    }
}
