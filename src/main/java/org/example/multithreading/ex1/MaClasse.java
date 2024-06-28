package org.example.multithreading.ex1;

public class MaClasse {
    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();

        int val1 = WebService1.SendRequest();
        int val2 = WebService2.SendRequest();
        int val3 = WebService3.SendRequest();
        System.out.println(val1 + val2 + val3);

        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime-startTime) + "ms");
    }
}
