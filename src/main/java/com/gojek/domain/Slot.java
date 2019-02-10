package com.gojek.domain;

public class Slot {

    private int number;
    private boolean occupied;
    private Car car;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Slot)) return false;

        Slot slot = (Slot) o;

        if (getNumber() != slot.getNumber()) return false;
        if (isOccupied() != slot.isOccupied()) return false;
        return getCar().equals(slot.getCar());
    }

    @Override
    public int hashCode() {
        int result = getNumber();
        result = 31 * result + (isOccupied() ? 1 : 0);
        result = 31 * result + getCar().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Slot{" +
                "number=" + number +
                ", occupied=" + occupied +
                ", car=" + car +
                '}';
    }
}
