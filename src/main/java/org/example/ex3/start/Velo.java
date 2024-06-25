package org.example.ex3.start;

public class Velo {
    private String motor;
    private boolean bottle;

    public String getMotor() {
        return motor;
    }

    public void setMotor(String motor) {
        this.motor = motor;
    }

    public boolean hasBottle() {
        return bottle;
    }

    public void setBottle(boolean bottle) {
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
