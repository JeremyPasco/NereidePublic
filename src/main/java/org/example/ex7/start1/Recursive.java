package org.example.ex7.start1;

import java.util.Random;

public class Recursive {
    public static void main(String[] args) {
        System.out.println(randomNumber());
    }

    private static float randomNumber() {
        var random = new Random();
        float res;
        while(true) {
            float x = random.nextFloat(0, 1);
            if(x < 0.0000000001) {
                res = x;
                break;
            }
        }
        return res;
    }
}
