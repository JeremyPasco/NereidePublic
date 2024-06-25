package org.example.ex3.step4;

import java.util.Objects;

public class Velo {
    private String motor;
    private boolean bottle;

    public String getMotor() {
        return motor;
    }

    public boolean hasBottle() {
        return bottle;
    }

    public Velo(VeloBuilder builder) throws IllegalArgumentException {
        if(Objects.equals(builder.motor, "electric") && builder.bottle) {
            throw new IllegalArgumentException("Un vélo électrique ne peut pas posséder de gourde");
        }
        this.motor = builder.motor;
        this.bottle = builder.bottle;
    }

    @Override
    public String toString() {
        return "Velo{" +
                "motor='" + motor + '\'' +
                ", bottle=" + bottle +
                '}';
    }

    public static class VeloBuilder {
        private String motor = "muscular";
        private boolean bottle = false;

        public VeloBuilder(String motor) {
            this.motor = motor;
        }

        public VeloBuilder bottle(boolean bottle) {
            this.bottle = bottle;
            return this;
        }

        public Velo build() {
            return new Velo(this);
        }
    }
}
