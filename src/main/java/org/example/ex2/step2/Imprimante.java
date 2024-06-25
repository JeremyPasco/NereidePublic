package org.example.ex2.step2;

public class Imprimante {
    public static void main(String[] args) {
        Produit[] produits = {
                new Produit("Kayak", 50, 20, 40, 210),
                new Produit("Club de golf", 1, 110, 15, 10),
        };

        for(Produit p : produits) {
            EtiquetteFactory factory;
            if(p.getPoids() > 10) {
                factory = new EtiquettePoidsFactory();
            } else {
                factory = new EtiquetteTailleFactory();
            }
            Etiquette etiquette = factory.createEtiquette(p);
            etiquette.imprimer();
        }
    }
}
