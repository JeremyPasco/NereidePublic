package org.example.ex2.step2;

public class EtiquetteTailleFactory implements EtiquetteFactory {

    public Etiquette createEtiquette(Produit produit) {
        int hauteur = produit.getHauteur();
        int largeur = produit.getLargeur();
        int longueur = produit.getLongueur();

        if(hauteur < largeur) {
            if(hauteur < longueur) {
                hauteur += 10;
            } else {
                longueur += 10;
            }
        } else {
            if(largeur < longueur) {
                largeur += 10;
            } else {
                longueur += 10;
            }
        }

        return new EtiquetteTaille(produit.getNom(), hauteur, largeur, longueur);
    }
}
