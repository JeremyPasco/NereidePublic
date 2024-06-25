package org.example.ex3.step2;

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

    public Velo(String motor, boolean bottle) throws IllegalArgumentException {
        if(Objects.equals(motor, "electric") && bottle) {
            throw new IllegalArgumentException("Un vélo électrique ne peut pas posséder de gourde");
        }
        this.motor = motor;
        this.bottle = bottle;
    }

    @Override
    public String toString() {
        return "Velo{" +
                "motor='" + motor + '\'' +
                ", bottle=" + bottle +
                '}';
    }
}
