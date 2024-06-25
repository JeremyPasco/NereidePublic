package org.example.ex2.step2;

public class EtiquettePoids implements Etiquette {

    private final String nomDuProduit;
    private final int poids;

    public EtiquettePoids(String nomDuProduit, int poids) {
        this.nomDuProduit = nomDuProduit;
        this.poids = poids;
    }

    public void imprimer() {
        System.out.println(nomDuProduit + " (" + poids + " kg)");
    }
}
