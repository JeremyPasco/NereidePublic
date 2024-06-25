package org.example.ex4.step1;

public class DecoratorGourde implements Velo{
    private final String label;
    private final int price;

    public DecoratorGourde(Velo velo) {
        this.label = velo.getLabel() + " [Gourde]";
        this.price = velo.getPrice() + 20;
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
