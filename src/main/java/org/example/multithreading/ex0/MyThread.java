package org.example.multithreading.ex0;

public class MyThread extends Thread {
    public void run() {
        System.out.println("START");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("STOP");
    }
}
