package org.example.ex3.step3;

public class Configurator {
    public static void main(String[] args) {
        Velo velo1 = new VeloBuilder().motor("electric").build(); //électrique sans gourde
        Velo velo2 = new VeloBuilder().motor("muscular").bottle(true).build(); //électrique sans gourde
        Velo velo3 = new VeloBuilder().bottle(true).build();
    }
}
