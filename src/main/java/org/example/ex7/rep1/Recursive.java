package org.example.ex7.rep1;

import java.util.Random;

public class Recursive {
    public static void main(String[] args) {
        System.out.println(randomNumber(100, 1));
    }

    private static float randomNumber(int maxDepth, int depth) {
        var random = new Random().nextFloat(0, 1);
        if(random < 0.0000000001) {
            return random;
        }
        depth++;
        if(depth > maxDepth) {
            throw new RuntimeException("Depth exceeded");
        }
        return randomNumber(maxDepth, depth);
    }
}
