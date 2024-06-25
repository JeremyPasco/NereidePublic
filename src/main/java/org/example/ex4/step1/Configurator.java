package org.example.ex4.step1;

public class Configurator {
    public static void main(String[] args) {
        Velo velo1 = new VeloElectrique();
        Velo velo2 = new DecoratorVtt(new VeloElectrique());
        Velo velo3 = new DecoratorGourde(new DecoratorVtt(new VeloElectrique()));
        Velo velo4 = new DecoratorGourde(new VeloElectrique());

        System.out.println(velo1.getLabel()); //Doit retourner "Vélo électrique"
        System.out.println(velo2.getLabel()); //Doit retourner "Vélo électrique [VTT]"
        System.out.println(velo3.getLabel()); //Doit retourner "Vélo électrique [VTT] [Gourde]"
        System.out.println(velo4.getLabel()); //Doit retourner "Vélo électrique [Gourde]"

        System.out.println(velo1.getPrice()); //Doit retourner 100
        System.out.println(velo2.getPrice()); //Doit retourner 150
        System.out.println(velo3.getPrice()); //Doit retourner 170
        System.out.println(velo4.getPrice()); //Doit retourner 120
    }
}
