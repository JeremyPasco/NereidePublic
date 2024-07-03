package org.example.ex7.rep2;

import java.util.Random;

public class BigDataOps {
    public static int blackBox() {
        byte[] data = new byte[50 * 1024 * 1024];
        new Random().nextBytes(data);
        if(data[0] > 10) {
            return new Random().nextInt(0, 9);
        } else {
            return new Random().nextInt(0, 8);
        }
    }
}
