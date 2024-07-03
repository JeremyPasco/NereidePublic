package org.example.multithreading.ex0;

public class Main {
    public static void main(String[] args) {

        var t = new MyThread();
        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("End");
    }
}
