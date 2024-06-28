package org.example.multithreading.ex1;

public class WebService1 {
    public static int SendRequest() throws InterruptedException {
        Thread.sleep(2000);
        return 1;
    }
}
