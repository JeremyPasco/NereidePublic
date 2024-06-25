package org.example.ex2.step2;

public class EtiquetteTaille implements Etiquette {

    private final String nomDuProduit;
    private final int hauteur;
    private final int largeur;
    private final int longueur;

    public EtiquetteTaille(String nomDuProduit, int hauteur, int largeur, int longueur) {
        this.nomDuProduit = nomDuProduit;
        this.hauteur = hauteur;
        this.largeur = largeur;
        this.longueur = longueur;
    }

    public void imprimer() {
        System.out.println(nomDuProduit + " (" + hauteur + " cm x " + largeur + " cm x " + longueur + " cm)");
    }
}
