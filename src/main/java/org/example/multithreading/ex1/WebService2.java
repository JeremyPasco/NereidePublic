package org.example.multithreading.ex1;

public class WebService2 {
    public static int SendRequest() throws InterruptedException {
        Thread.sleep(5000);
        return 3;
    }
}
