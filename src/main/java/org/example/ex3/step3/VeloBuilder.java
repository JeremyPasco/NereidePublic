package org.example.ex3.step3;

public class VeloBuilder {
    private String motor = "muscular";
    private boolean bottle = false;

    public VeloBuilder motor(String motor) {
        this.motor = motor;
        return this;
    }

    public VeloBuilder bottle(boolean bottle) {
        this.bottle = bottle;
        return this;
    }

    public Velo build() {
        return new Velo(motor, bottle);
    }
}
