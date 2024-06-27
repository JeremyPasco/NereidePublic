package org.example.ex6.step4;

@ProductClass
public class Kayak {
    private int weight;

    @LabelField(label="Largeur")
    public int width;

    @LabelField
    public int length;

    public int height;
}
