package org.example.ex4.step1;

public class DecoratorVtt implements Velo{
    private final String label;
    private final int price;

    public DecoratorVtt(Velo velo) {
        this.label = velo.getLabel() + " [VTT]";
        this.price = velo.getPrice() + 50;
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
