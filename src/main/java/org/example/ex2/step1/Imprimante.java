package org.example.ex2.step1;

public class Imprimante {
    public static void main(String[] args) {
        Produit[] produits = {
                new Produit("Kayak", 50, 20, 40, 210),
                new Produit("Club de golf", 1, 110, 15, 10),
        };

        for(Produit p : produits) {
            Etiquette etiquette;
            if(p.getPoids() > 10) {
                etiquette = new EtiquettePoids(p.getNom(), p.getPoids());

            } else {
                etiquette = new EtiquetteTaille(p.getNom(), p.getHauteur(), p.getLargeur(), p.getLongueur());
            }
            etiquette.imprimer();
        }
    }
}
