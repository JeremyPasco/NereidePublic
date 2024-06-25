package org.example.ex2.start;

public class Produit {
    private String nom;
    private int poids;
    private int hauteur;
    private int largeur;
    private int longueur;

    public Produit(String nom, int poids, int hauteur, int largeur, int longueur) {
        this.nom = nom;
        this.poids = poids;
        this.hauteur = hauteur;
        this.largeur = largeur;
        this.longueur = longueur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getPoids() {
        return poids;
    }

    public void setPoids(int poids) {
        this.poids = poids;
    }

    public int getHauteur() {
        return hauteur;
    }

    public void setHauteur(int hauteur) {
        this.hauteur = hauteur;
    }

    public int getLargeur() {
        return largeur;
    }

    public void setLargeur(int largeur) {
        this.largeur = largeur;
    }

    public int getLongueur() {
        return longueur;
    }

    public void setLongueur(int longueur) {
        this.longueur = longueur;
    }
}
