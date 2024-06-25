package org.example.ex3.step2;

public class Configurator {
    public static void main(String[] args) {
        try {
            Velo velo = new Velo("electric", true);
            System.out.println(velo);
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
