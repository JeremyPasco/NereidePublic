package org.example.ex2.step2;

public class EtiquettePoidsFactory implements EtiquetteFactory {

    public Etiquette createEtiquette(Produit produit) {
        return new EtiquettePoids(produit.getNom(), produit.getPoids() < 20 ? produit.getPoids() : produit.getPoids() + 5);
    }
}
