package org.example.ex3.step4;

public class Configurator {
    public static void main(String[] args) {

        Velo velo1 = new Velo.VeloBuilder("electric")
                .build(); //électrique sans gourde

        Velo velo2 = new Velo.VeloBuilder("muscular")
                .bottle(true)
                .build(); //électrique sans gourde

        Velo velo3 = new Velo.VeloBuilder("muscular")
                .bottle(true)
                .build();
    }
}
