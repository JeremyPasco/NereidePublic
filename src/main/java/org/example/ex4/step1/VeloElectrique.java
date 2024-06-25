package org.example.ex4.step1;

public class VeloElectrique implements Velo {

    private String label;
    private int price;

    public VeloElectrique() {
        this.label = "Vélo électrique";
        this.price = 100;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public int getPrice() {
        return this.price;
    }
}
