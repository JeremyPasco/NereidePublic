package org.example.multithreading.re1;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MaClasse {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        ExecutorService service = Executors.newFixedThreadPool(3);
        Future<Integer> f1 = service.submit(WebService1::SendRequest);
        Future<Integer> f2 = service.submit(WebService2::SendRequest);
        Future<Integer> f3 = service.submit(WebService3::SendRequest);

        try {
            System.out.println(f1.get() + f2.get() + f3.get());
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        service.shutdown();

        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime-startTime) + "ms");
    }
}

